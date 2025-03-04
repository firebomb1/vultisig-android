package com.vultisig.wallet.chains

import com.google.gson.Gson
import com.google.protobuf.ByteString
import com.vultisig.wallet.common.Numeric
import com.vultisig.wallet.models.Coin
import com.vultisig.wallet.models.Coins
import com.vultisig.wallet.models.CosmoSignature
import com.vultisig.wallet.models.SignedTransactionResult
import com.vultisig.wallet.models.transactionHash
import com.vultisig.wallet.presenter.keysign.BlockChainSpecific
import com.vultisig.wallet.presenter.keysign.KeysignPayload
import com.vultisig.wallet.tss.getSignatureWithRecoveryID
import tss.KeysignResponse
import wallet.core.jni.AnyAddress
import wallet.core.jni.CoinType
import wallet.core.jni.DataVector
import wallet.core.jni.PublicKey
import wallet.core.jni.PublicKeyType
import wallet.core.jni.TransactionCompiler
import wallet.core.jni.proto.Cosmos

@OptIn(ExperimentalStdlibApi::class)
internal class MayaChainHelper(
    private val vaultHexPublicKey: String,
    private val vaultHexChainCode: String,
) {
    private val coinType: CoinType = CoinType.THORCHAIN
    companion object {
        const val MayaChainGasUnit: Long = 2000000000
    }
    fun getCoin(): Coin? {
        val derivedPublicKey = PublicKeyHelper.getDerivedPublicKey(
            vaultHexPublicKey,
            vaultHexChainCode,
            coinType.derivationPath()
        )
        val publicKey = PublicKey(derivedPublicKey.hexToByteArray(), PublicKeyType.SECP256K1)
        val address = AnyAddress(publicKey, CoinType.THORCHAIN, "maya")
        return Coins.getCoin("MAYA", address.description(), derivedPublicKey, coinType)
    }

    fun getSwapPreSignedInputData(
        keysignPayload: KeysignPayload,
        input: Cosmos.SigningInput.Builder,
    ): ByteArray {
        val thorchainData = keysignPayload.blockChainSpecific as? BlockChainSpecific.THORChain
            ?: throw Exception("Invalid blockChainSpecific")
        val publicKey =
            PublicKey(keysignPayload.vaultPublicKeyECDSA.hexToByteArray(), PublicKeyType.SECP256K1)
        val inputData = input.apply {
            this.publicKey = ByteString.copyFrom(publicKey.data())
            this.accountNumber = thorchainData.accountNumber.toLong()
            this.sequence = thorchainData.sequence.toLong()
            this.mode = Cosmos.BroadcastMode.SYNC
            this.fee = Cosmos.Fee.newBuilder().apply {
                this.gas = MayaChainGasUnit
                this.amountsList.add(Cosmos.Amount.newBuilder().apply {
                    this.denom = "cacao"
                    this.amount = MayaChainGasUnit.toString()
                }.build())
            }.build()
        }.build()
        return inputData.toByteArray()
    }

    fun getPreSignInputData(keysignPayload: KeysignPayload): ByteArray {
        val fromAddress = AnyAddress(keysignPayload.coin.address, coinType,"maya").data()
        val toAddress = AnyAddress(keysignPayload.toAddress, coinType,"maya").data()
        val thorchainData = keysignPayload.blockChainSpecific as? BlockChainSpecific.THORChain
            ?: throw Exception("Invalid blockChainSpecific")
        val publicKey =
            PublicKey(keysignPayload.coin.hexPublicKey.hexToByteArray(), PublicKeyType.SECP256K1)

        val sendAmount = Cosmos.Amount.newBuilder().apply {
            this.denom = "cacao"
            this.amount = keysignPayload.toAmount.toString()
        }.build()

        val msgSend = Cosmos.Message.THORChainSend.newBuilder().apply {
            this.fromAddress = ByteString.copyFrom(fromAddress)
            this.toAddress = ByteString.copyFrom(toAddress)
            this.addAllAmounts(listOf(sendAmount))
        }.build()

        val feeAmount = Cosmos.Amount.newBuilder().apply {
            this.denom = "cacao"
            this.amount = MayaChainGasUnit.toString()
        }.build()
        val input = Cosmos.SigningInput.newBuilder().apply {
            this.publicKey = ByteString.copyFrom(publicKey.data())
            this.signingMode = Cosmos.SigningMode.Protobuf
            this.chainId = "mayachain-mainnet-v1"
            this.accountNumber = thorchainData.accountNumber.toLong()
            this.sequence = thorchainData.sequence.toLong()
            this.mode = Cosmos.BroadcastMode.SYNC
            keysignPayload.memo?.let {
                this.memo = it
            }

            this.addAllMessages(listOf(Cosmos.Message.newBuilder().apply {
                this.thorchainSendMessage = msgSend
            }.build()))

            this.fee = Cosmos.Fee.newBuilder().apply {
                this.gas = MayaChainGasUnit
                this.addAllAmounts(listOf(feeAmount))
            }.build()
        }.build()
        return input.toByteArray()
    }

    fun getPreSignedImageHash(keysignPayload: KeysignPayload): List<String> {
        val inputData = getPreSignInputData(keysignPayload)
        val preHashes = TransactionCompiler.preImageHashes(coinType, inputData)
        val preSigningOutput =
            wallet.core.jni.proto.TransactionCompiler.PreSigningOutput.parseFrom(preHashes)
        return listOf(Numeric.toHexStringNoPrefix(preSigningOutput.dataHash.toByteArray()))
    }

    fun getSignedTransaction(
        keysignPayload: KeysignPayload,
        signatures: Map<String, KeysignResponse>,
    ): SignedTransactionResult {
        val inputData = getPreSignInputData(keysignPayload)
        return getSignedTransaction(inputData, signatures)
    }

    fun getSignedTransaction(
        inputData: ByteArray,
        signatures: Map<String, KeysignResponse>,
    ): SignedTransactionResult {
        val thorchainPublicKey = PublicKeyHelper.getDerivedPublicKey(
            hexPublicKey = vaultHexPublicKey,
            hexChainCode = vaultHexChainCode,
            coinType.derivationPath()
        )
        val publicKey = PublicKey(thorchainPublicKey.hexToByteArray(), PublicKeyType.SECP256K1)
        val preHashes = TransactionCompiler.preImageHashes(coinType, inputData)
        val preSigningOutput =
            wallet.core.jni.proto.TransactionCompiler.PreSigningOutput.parseFrom(preHashes)
        val allSignatures = DataVector()
        val allPublicKeys = DataVector()
        val key = Numeric.toHexStringNoPrefix(preSigningOutput.dataHash.toByteArray())
        val signature = signatures[key]?.getSignatureWithRecoveryID()
            ?: throw Exception("Signature not found")

        if (!publicKey.verify(signature, preSigningOutput.dataHash.toByteArray())) {
            throw Exception("Signature verification failed")
        }
        allSignatures.add(signature)
        allPublicKeys.add(publicKey.data())
        val compileWithSignature = TransactionCompiler.compileWithSignatures(
            coinType,
            inputData,
            allSignatures,
            allPublicKeys
        )
        val output = Cosmos.SigningOutput.parseFrom(compileWithSignature)
        val cosmosSig = Gson().fromJson(output.serialized, CosmoSignature::class.java)
        return SignedTransactionResult(
            output.serialized,
            cosmosSig.transactionHash(),
        )
    }
}