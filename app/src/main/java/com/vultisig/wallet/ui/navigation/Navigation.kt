package com.vultisig.wallet.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.vultisig.wallet.data.models.TransactionId

internal sealed class Destination(
    val route: String,
) {

    companion object {
        const val ARG_VAULT_ID = "vault_id"
        const val ARG_CHAIN_ID = "chain_id"
        const val ARG_TOKEN_ID = "token_id"
        const val ARG_DST_ADDRESS = "dst_address"
        const val ARG_AMOUNT = "amount"
        const val ARG_TRANSACTION_ID = "transaction_id"

        val transactionArgs = listOf(
            navArgument(ARG_TRANSACTION_ID) { type = NavType.StringType }
        )

    }

    data class Keysign(
        val transactionId: TransactionId,
    ) : Destination(
        route = buildRoute(transactionId)
    ) {
        companion object {
            val staticRoute = buildRoute(
                "{$ARG_TRANSACTION_ID}",
            )

            private fun buildRoute(
                transactionId: TransactionId,
            ) = "send/${transactionId}/sign"
        }
    }

    data class Send(
        val vaultId: String,
        val chainId: String,
    ) : Destination(
        route = "vault_detail/${vaultId}/account/${chainId}/send"
    ) {
        companion object {
            const val staticRoute =
                "vault_detail/{$ARG_VAULT_ID}/account/{$ARG_CHAIN_ID}/send"
        }
    }

    data class VerifyTransaction(
        val transactionId: TransactionId,
    ) : Destination(
        route = buildRoute(transactionId)
    ) {
        companion object {

            val staticRoute = buildRoute(
                "{$ARG_TRANSACTION_ID}",
            )

            private fun buildRoute(
                transactionId: TransactionId,
            ) = "transaction/${transactionId}/verify"
        }
    }

    data class SelectTokens(
        val vaultId: String,
        val chainId: String,
    ) : Destination(
        route = "vault_detail/${vaultId}/account/${chainId}/select_tokens"
    ) {
        companion object {
            const val staticRoute =
                "vault_detail/{$ARG_VAULT_ID}/account/{$ARG_CHAIN_ID}/select_tokens"
        }
    }

    data object ScanQr : Destination(route = "scan_qr")

    data object Back : Destination(
        route = ""
    )

    data object Home : Destination(route = "home_screen")
    data class VaultSettings(val vaultId: String) :
        Destination(route = "vault_detail/$vaultId/settings") {

        companion object {
            const val ARG_VAULT_ID = "vault_id"
            const val STATIC_ROUTE = "vault_detail/{vault_id}/settings"
        }
    }

    data class Details(val vaultId: String) :
        Destination(route = "vault_detail/$vaultId/settings/details") {
        companion object {
            const val STATIC_ROUTE = VaultSettings.STATIC_ROUTE + "/details"
        }
    }

    data class Rename(val vaultId: String) :
        Destination(route = "vault_detail/$vaultId/settings/rename") {
        companion object {
            const val STATIC_ROUTE = VaultSettings.STATIC_ROUTE + "/rename"
        }
    }

    data object Settings : Destination(route = "settings")
    data object DefaultChainSetting : Destination(route = "settings/default_chains")
    data object FAQSetting : Destination(route = "settings/faq")
    data object VultisigToken : Destination(route = "settings/vultisig_token")
    data object LanguageSetting : Destination(route = "settings/language")
    data object CurrencyUnitSetting : Destination(route = "settings/currency")
    data class QrAddressScreen(val address:String) : Destination(route = "vault_details/qr_address_screen/$address"){
        companion object {
            const val ARG_COIN_ADDRESS = "coin_address"
            const val STATIC_ROUTE = "vault_details/qr_address_screen/{$ARG_COIN_ADDRESS}"
        }
    }

}