package com.vultisig.wallet.ui.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen(route = "welcome_screen")
    data object Home : Screen(route = "home_screen")

    data object CreateNewVault : Screen(route = "create_new_vault")
    data object JoinKeygen : Screen(route = "join_keygen")
    data object ImportFile : Screen(route = "import_file/{has_file}")
    data object Setup : Screen(route = "setup")
    data object KeygenFlow : Screen(route = "keygen_flow/{vault_name}") {
        const val ARG_VAULT_NAME = "vault_name"
        const val DEFAULT_NEW_VAULT = "*vultisig_new_vault*"
        fun createRoute(vaultName: String): String {
            return "keygen_flow/$vaultName"
        }
    }

    data object SigningError : Screen(route = "signing_error")

    data object AddChainAccount : Screen(route = "vault_detail/{vault_id}/add_account") {
        const val ARG_VAULT_ID = "vault_id"
        fun createRoute(vaultId: String): String {
            return "vault_detail/$vaultId/add_account"
        }
    }

    data object JoinKeysign : Screen(route = "join_keysign/{vault_id}") {
        const val ARG_VAULT_ID = "vault_id"
        fun createRoute(vaultId: String): String {
            return "join_keysign/$vaultId"
        }
    }

    data object ChainCoin : Screen(route = "chainCoin/{chain_raw}/{vault_id}") {
        const val CHAIN_COIN_PARAM_CHAIN_RAW = "chain_raw"
        const val CHAIN_COIN_PARAM_VAULT_ID = "vault_id"
        fun createRoute(chainRaw: String, vaultId: String): String {
            return "chainCoin/$chainRaw/$vaultId"
        }
    }


}