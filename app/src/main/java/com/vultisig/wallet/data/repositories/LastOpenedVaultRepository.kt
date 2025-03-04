package com.vultisig.wallet.data.repositories

import androidx.datastore.preferences.core.stringPreferencesKey
import com.vultisig.wallet.data.on_board.db.VaultDB
import com.vultisig.wallet.data.sources.AppDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal interface LastOpenedVaultRepository {

    val lastOpenedVaultId: Flow<String?>

    suspend fun setLastOpenedVaultId(vaultId: String)

}

internal class LastOpenedVaultRepositoryImpl @Inject constructor(
    private val vaultDb: VaultDB,
    private val dataStore: AppDataStore,
) : LastOpenedVaultRepository {

    override val lastOpenedVaultId: Flow<String?> = dataStore
        .readData(LastOpenedVaultId)
        .map { it ?: vaultDb.selectAll().firstOrNull()?.id }

    override suspend fun setLastOpenedVaultId(vaultId: String) {
        dataStore.set(LastOpenedVaultId, vaultId)
    }

    companion object {
        private val LastOpenedVaultId = stringPreferencesKey(name = "last_opened_vault_id")
    }

}
