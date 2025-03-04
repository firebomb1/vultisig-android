package com.vultisig.wallet.presenter.keysign

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.vultisig.wallet.chains.AtomHelper
import com.vultisig.wallet.chains.ERC20Helper
import com.vultisig.wallet.chains.EvmHelper
import com.vultisig.wallet.chains.KujiraHelper
import com.vultisig.wallet.chains.MayaChainHelper
import com.vultisig.wallet.chains.SolanaHelper
import com.vultisig.wallet.chains.THORCHainHelper
import com.vultisig.wallet.chains.THORChainSwaps
import com.vultisig.wallet.chains.utxoHelper
import com.vultisig.wallet.common.md5
import com.vultisig.wallet.common.toHexBytes
import com.vultisig.wallet.data.api.BlockChairApi
import com.vultisig.wallet.data.api.CosmosApiFactory
import com.vultisig.wallet.data.api.EvmApiFactory
import com.vultisig.wallet.data.api.MayaChainApi
import com.vultisig.wallet.data.api.SolanaApi
import com.vultisig.wallet.data.api.ThorChainApi
import com.vultisig.wallet.data.repositories.ExplorerLinkRepository
import com.vultisig.wallet.mediator.MediatorService
import com.vultisig.wallet.models.Chain
import com.vultisig.wallet.models.SignedTransactionResult
import com.vultisig.wallet.models.Vault
import com.vultisig.wallet.tss.LocalStateAccessor
import com.vultisig.wallet.tss.TssKeyType
import com.vultisig.wallet.tss.TssMessagePuller
import com.vultisig.wallet.tss.TssMessenger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import tss.ServiceImpl
import tss.Tss
import java.util.Base64

enum class KeysignState {
    CreatingInstance,
    KeysignECDSA,
    KeysignEdDSA,
    KeysignFinished,
    ERROR
}

internal class KeysignViewModel(
    val vault: Vault,
    private val keysignCommittee: List<String>,
    private val serverAddress: String,
    private val sessionId: String,
    private val encryptionKeyHex: String,
    private val messagesToSign: List<String>,
    private val keyType: TssKeyType,
    private val keysignPayload: KeysignPayload,
    private val gson: Gson,
    private val thorChainApi: ThorChainApi,
    private val blockChairApi: BlockChairApi,
    private val evmApiFactory: EvmApiFactory,
    private val mayaChainApi: MayaChainApi,
    private val cosmosApiFactory: CosmosApiFactory,
    private val solanaApi: SolanaApi,
    private val explorerLinkRepository: ExplorerLinkRepository,
) : ViewModel() {
    private var tssInstance: ServiceImpl? = null
    private val tssMessenger: TssMessenger =
        TssMessenger(serverAddress, sessionId, encryptionKeyHex)
    private val localStateAccessor: LocalStateAccessor = LocalStateAccessor(vault)
    val currentState: MutableStateFlow<KeysignState> =
        MutableStateFlow(KeysignState.CreatingInstance)
    val errorMessage: MutableState<String> = mutableStateOf("")
    private var _messagePuller: TssMessagePuller? = null
    private val signatures: MutableMap<String, tss.KeysignResponse> = mutableMapOf()
    val txHash = MutableStateFlow("")
    val txLink = txHash.map {
        explorerLinkRepository.getTransactionLink(keysignPayload.coin.chain, it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ""
    )

    fun startKeysign() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                signAndBroadcast()
            }
        }
    }

    private suspend fun signAndBroadcast() {
        Timber.d("Start to SignAndBroadcast")
        currentState.value = KeysignState.CreatingInstance
        try {
            this.tssInstance = Tss.newService(this.tssMessenger, this.localStateAccessor, false)
            this.tssInstance ?: run {
                throw Exception("Failed to create TSS instance")
            }
            _messagePuller = TssMessagePuller(
                service = this.tssInstance!!,
                hexEncryptionKey = encryptionKeyHex,
                serverAddress = serverAddress,
                localPartyKey = vault.localPartyID,
                sessionID = sessionId
            )
            this.messagesToSign.forEach() { message ->
                signMessageWithRetry(this.tssInstance!!, message, 1)
            }
            broadcastTransaction()
            currentState.value = KeysignState.KeysignFinished
            this._messagePuller?.stop()
        } catch (e: Exception) {
            Timber.e(e)
            currentState.value = KeysignState.ERROR
            errorMessage.value = e.message ?: "Unknown error"
        }
    }

    private suspend fun signMessageWithRetry(service: ServiceImpl, message: String, attempt: Int) {
        val keysignVerify = KeysignVerify(serverAddress, sessionId, gson)
        try {
            Timber.d("signMessageWithRetry: $message, attempt: $attempt")
            val msgHash = message.md5()
            this.tssMessenger.setMessageID(msgHash)
            Timber.d("signMessageWithRetry: msgHash: $msgHash")
            this._messagePuller?.pullMessages(msgHash)
            val keysignReq = tss.KeysignRequest()
            keysignReq.localPartyKey = vault.localPartyID
            keysignReq.keysignCommitteeKeys = keysignCommittee.joinToString(",")
            keysignReq.messageToSign = Base64.getEncoder().encodeToString(message.toHexBytes())
            keysignReq.derivePath = keysignPayload.coin.coinType.derivationPath()
            val keysignResp = when (keyType) {
                TssKeyType.ECDSA -> {
                    keysignReq.pubKey = vault.pubKeyECDSA
                    currentState.value = KeysignState.KeysignECDSA
                    service.keysignECDSA(keysignReq)
                }

                TssKeyType.EDDSA -> {
                    keysignReq.pubKey = vault.pubKeyEDDSA
                    currentState.value = KeysignState.KeysignEdDSA
                    service.keysignEdDSA(keysignReq)
                }
            }
            this.signatures[message] = keysignResp
            keysignVerify.markLocalPartyKeysignComplete(message, keysignResp)
            this._messagePuller?.stop()
            Thread.sleep(1000) // backoff for 1 second
        } catch (e: Exception) {
            this._messagePuller?.stop()
            Timber.tag("KeysignViewModel")
                .d("signMessageWithRetry error: %s", e.stackTraceToString())
            val resp = keysignVerify.checkKeysignComplete(message)
            resp?.let {
                this.signatures[message] = it
                return
            }
            if (attempt > 3) {
                throw e
            }
            signMessageWithRetry(service, message, attempt + 1)
        }
    }

    private suspend fun broadcastTransaction() {
        try {
            val signedTransaction = getSignedTransaction()
            when (keysignPayload.coin.chain) {
                Chain.thorChain -> {
                    this.thorChainApi.broadcastTransaction(signedTransaction.rawTransaction)
                        ?.let {
                            txHash.value = it
                            Timber.d("transaction hash:$it")
                        }
                }

                Chain.bitcoin, Chain.bitcoinCash, Chain.litecoin, Chain.dogecoin, Chain.dash -> {
                    this.blockChairApi.broadcastTransaction(
                        keysignPayload.coin,
                        signedTransaction.rawTransaction
                    ).let {
                        txHash.value = it
                        Timber.d("transaction hash:$it")
                    }
                }

                Chain.ethereum, Chain.cronosChain, Chain.blast, Chain.bscChain, Chain.avalanche, Chain.base, Chain.polygon, Chain.optimism, Chain.arbitrum -> {
                    val evmApi = evmApiFactory.createEvmApi(keysignPayload.coin.chain)
                    val txid = evmApi.sendTransaction(signedTransaction.rawTransaction)
                    txHash.value = txid
                    Timber.d("transaction hash:$txHash")
                }

                Chain.solana -> {
                    solanaApi.broadcastTransaction(signedTransaction.rawTransaction)
                        ?.let {
                            txHash.value = it
                            Timber.d("transaction hash:$it")
                        }

                }

                Chain.gaiaChain, Chain.kujira -> {
                    val cosmosApi = cosmosApiFactory.createCosmosApi(keysignPayload.coin.chain)
                    cosmosApi.broadcastTransaction(signedTransaction.rawTransaction)
                        ?.let {
                            txHash.value = it
                            Timber.d("transaction hash:$it")
                        }
                }

                Chain.mayaChain -> {
                    mayaChainApi.broadcastTransaction(signedTransaction.rawTransaction)
                        ?.let {
                            txHash.value = it
                            Timber.d("transaction hash:$it")
                        }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            errorMessage.value = e.message ?: "Unknown error"
            currentState.value = KeysignState.ERROR
        }
    }

    private fun getSignedTransaction(): SignedTransactionResult {
        if (keysignPayload.swapPayload != null) {
            return THORChainSwaps(vault.pubKeyECDSA, vault.hexChainCode)
                .getSignedTransaction(keysignPayload.swapPayload, keysignPayload, signatures)
        }
        if (keysignPayload.approvePayload != null) {
            return THORChainSwaps(vault.pubKeyECDSA, vault.hexChainCode)
                .getSignedApproveTransaction(
                    keysignPayload.approvePayload,
                    keysignPayload,
                    signatures
                )
        }
        // we could define an interface to make the following more simpler,but I will leave it for later
        when (keysignPayload.coin.chain) {
            Chain.bitcoin, Chain.dash, Chain.bitcoinCash, Chain.dogecoin, Chain.litecoin -> {
                val utxo = utxoHelper.getHelper(vault, keysignPayload.coin.coinType)
                return utxo.getSignedTransaction(keysignPayload, signatures)
            }

            Chain.thorChain -> {
                val thorHelper = THORCHainHelper(vault.pubKeyECDSA, vault.hexChainCode)
                return thorHelper.getSignedTransaction(keysignPayload, signatures)
            }

            Chain.gaiaChain -> {
                val atomHelper = AtomHelper(vault.pubKeyECDSA, vault.hexChainCode)
                return atomHelper.getSignedTransaction(keysignPayload, signatures)
            }

            Chain.kujira -> {
                val kujiraHelper = KujiraHelper(vault.pubKeyECDSA, vault.hexChainCode)
                return kujiraHelper.getSignedTransaction(keysignPayload, signatures)
            }

            Chain.solana -> {
                val solanaHelper = SolanaHelper(vault.pubKeyEDDSA)
                return solanaHelper.getSignedTransaction(keysignPayload, signatures)
            }

            Chain.ethereum, Chain.avalanche, Chain.bscChain, Chain.cronosChain, Chain.blast, Chain.arbitrum, Chain.optimism, Chain.polygon, Chain.base -> {
                if (keysignPayload.coin.isNativeToken) {
                    val evmHelper = EvmHelper(
                        keysignPayload.coin.coinType,
                        vault.pubKeyECDSA,
                        vault.hexChainCode
                    )
                    return evmHelper.getSignedTransaction(keysignPayload, signatures)
                } else {
                    val erc20Helper = ERC20Helper(
                        keysignPayload.coin.coinType,
                        vault.pubKeyECDSA,
                        vault.hexChainCode
                    )
                    return erc20Helper.getSignedTransaction(keysignPayload, signatures)
                }

            }

            Chain.mayaChain -> {
                val mayaHelper = MayaChainHelper(vault.pubKeyECDSA, vault.hexChainCode)
                return mayaHelper.getSignedTransaction(keysignPayload, signatures)
            }
        }
    }

    fun stopService(context: Context) {
        // start mediator service
        val intent = Intent(context, MediatorService::class.java)
        context.stopService(intent)
        Timber.d("stop MediatorService: Mediator service stopped")

    }
}
