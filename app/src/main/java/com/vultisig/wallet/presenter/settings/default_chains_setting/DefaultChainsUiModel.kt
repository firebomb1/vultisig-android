package com.vultisig.wallet.presenter.settings.default_chains_setting

import androidx.annotation.DrawableRes
import com.vultisig.wallet.models.Chain
import com.vultisig.wallet.models.Ticker
import com.vultisig.wallet.models.logo

data class DefaultChainsUiModel(
    val allDefaultChains: List<DefaultChain> = emptyList(),
    val selectedDefaultChains: List<DefaultChain> = emptyList(),
)

data class DefaultChain(
    val title: String,
    val subtitle: String,
    @DrawableRes val logo: Int
)

fun Chain.toUiModel() = DefaultChain(title = raw, subtitle = Ticker, logo = logo)

fun DefaultChain.toDataModel() = Chain.entries.first { it.raw == title }

fun List<Chain>.toUiModel() = map { it.toUiModel() }