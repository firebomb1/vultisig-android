package com.vultisig.wallet.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.vultisig.wallet.R
import kotlinx.parcelize.Parcelize
import wallet.core.jni.CoinType

@Parcelize
data class Coin(
    val chain: Chain,
    val ticker: String,
    val logo: String,
    val address: String,
    @SerializedName("decimals")
    val decimal: Int,
    val hexPublicKey: String,
    val priceProviderID: String,
    val contractAddress: String,
    val isNativeToken: Boolean,
) : Parcelable {
    val id: String
        get() = ticker

    val coinType: CoinType
        get() = chain.coinType

}

object Coins {
    val SupportedCoins = listOf(
        Coin(
            chain = Chain.bitcoin,
            ticker = "BTC",
            logo = "btc",
            address = "",
            decimal = 8,
            hexPublicKey = "",
            priceProviderID = "bitcoin",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.bitcoinCash,
            ticker = "BCH",
            logo = "bch",
            address = "",
            decimal = 8,
            hexPublicKey = "",
            priceProviderID = "bitcoin-cash",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.litecoin,
            ticker = "LTC",
            logo = "ltc",
            address = "",
            decimal = 8,
            hexPublicKey = "",
            priceProviderID = "litecoin",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.dogecoin,
            ticker = "DOGE",
            logo = "doge",
            address = "",
            decimal = 8,
            hexPublicKey = "",
            priceProviderID = "dogecoin",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.dash,
            ticker = "DASH",
            logo = "dash",
            address = "",
            decimal = 8,
            hexPublicKey = "",
            priceProviderID = "dash",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.thorChain,
            ticker = "RUNE",
            logo = "rune",
            address = "",
            decimal = 8,
            hexPublicKey = "",
            priceProviderID = "thorchain",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.mayaChain,
            ticker = "CACAO",
            logo = "cacao",
            address = "",
            decimal = 10,
            hexPublicKey = "",
            priceProviderID = "cacao",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.mayaChain,
            ticker = "MAYA",
            logo = "maya",
            address = "",
            decimal = 4,
            hexPublicKey = "",
            priceProviderID = "maya",
            contractAddress = "",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.ethereum,
            ticker = "ETH",
            logo = "eth",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "ethereum",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.ethereum,
            ticker = "USDC",
            logo = "usdc",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "usd-coin",
            contractAddress = "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.ethereum,
            ticker = "USDT",
            logo = "usdt",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "tether",
            contractAddress = "0xdAC17F958D2ee523a2206206994597C13D831ec7",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.ethereum,
            ticker = "UNI",
            logo = "uni",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "uniswap",
            contractAddress = "0x1f9840a85d5af5bf1d1762f925bdaddc4201f984",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.ethereum,
            ticker = "MATIC",
            logo = "polygon",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "matic-network",
            contractAddress = "0x7d1afa7b718fb893db30a3abc0cfc608aacfebb0",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.ethereum,
            ticker = "WBTC",
            logo = "wbtc",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "wrapped-bitcoin",
            contractAddress = "0x2260fac5e5542a773aa44fbcfedf7c193bc2c599",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.ethereum,
            ticker = "LINK",
            logo = "link",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "chainlink",
            contractAddress = "0x514910771af9ca656af840dff83e8264ecf986ca",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.ethereum,
            ticker = "FLIP",
            logo = "flip",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "chainflip",
            contractAddress = "0x826180541412d574cf1336d22c0c0a287822678a",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.ethereum,
            ticker = "TGT",
            logo = "tgt",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "thorwallet",
            contractAddress = "0x108a850856Db3f85d0269a2693D896B394C80325",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.avalanche,
            ticker = "AVAX",
            logo = "eth",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "avalanche-2",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.avalanche,
            ticker = "USDC",
            logo = "usdc",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "usd-coin",
            contractAddress = "0xb97ef9ef8734c71904d8002f8b6bc66dd9c48a6e",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.solana,
            ticker = "SOL",
            logo = "sol",
            address = "",
            decimal = 9,
            hexPublicKey = "",
            priceProviderID = "solana",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.bscChain,
            ticker = "BNB",
            logo = "bsc",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "binancecoin",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.bscChain,
            ticker = "USDT",
            logo = "usdt",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "binancecoin",
            contractAddress = "",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.base,
            ticker = "ETH",
            logo = "eth_base",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "ethereum",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.arbitrum,
            ticker = "ETH",
            logo = "eth_arbitrum",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "ethereum",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.arbitrum,
            ticker = "ARB",
            logo = "arbitrum",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "arbitrum",
            contractAddress = "0x912CE59144191C1204E64559FE8253a0e49E6548",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.arbitrum,
            ticker = "TGT",
            logo = "tgt",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "thorwallet",
            contractAddress = "0x429fEd88f10285E61b12BDF00848315fbDfCC341",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.optimism,
            ticker = "ETH",
            logo = "eth_optimism",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "ethereum",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.optimism,
            ticker = "OP",
            logo = "optimism",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "optimism",
            contractAddress = "0x4200000000000000000000000000000000000042",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.polygon,
            ticker = "ETH",
            logo = "polygon",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "matic-network",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.polygon,
            ticker = "WETH",
            logo = "wETH",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "ethereum",
            contractAddress = "0x7ceB23fD6bC0adD59E62ac25578270cFf1b9f619",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.blast,
            ticker = "ETH",
            logo = "eth_blast",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "ethereum",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.blast,
            ticker = "WETH",
            logo = "wETH",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "ethereum",
            contractAddress = "0x4300000000000000000000000000000000000004",
            isNativeToken = false,
        ),
        Coin(
            chain = Chain.cronosChain,
            ticker = "CRO",
            logo = "cro",
            address = "",
            decimal = 18,
            hexPublicKey = "",
            priceProviderID = "crypto-com-chain",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.gaiaChain,
            ticker = "ATOM",
            logo = "atom",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "cosmos",
            contractAddress = "",
            isNativeToken = true,
        ),
        Coin(
            chain = Chain.kujira,
            ticker = "KUJI",
            logo = "kuji",
            address = "",
            decimal = 6,
            hexPublicKey = "",
            priceProviderID = "kujira",
            contractAddress = "",
            isNativeToken = true,
        ),
    )

    fun getCoin(ticker: String, address: String, hexPublicKey: String, coinType: CoinType): Coin? {
        return SupportedCoins.find { it.ticker == ticker && it.coinType == coinType }
            ?.copy(address = address, hexPublicKey = hexPublicKey)

    }

    fun getCoinLogo(logoName: String): Int {
        return when (logoName) {
            "btc" -> R.drawable.bitcoin
            "bch" -> R.drawable.bitcoincash
            "ltc" -> R.drawable.litecoin
            "doge" -> R.drawable.doge
            "dash" -> R.drawable.dash
            "rune" -> R.drawable.rune
            "eth" -> R.drawable.ethereum
            "sol" -> R.drawable.solana
            "cacao" -> R.drawable.cacao
            "maya" -> R.drawable.maya_token_02
            "usdc" -> R.drawable.usdc
            "usdt" -> R.drawable.usdt
            "link" -> R.drawable.link
            "uni" -> R.drawable.uni
            "matic" -> R.drawable.matic
            "wbtc" -> R.drawable.wbtc
            "flip" -> R.drawable.chainflip
            "avax" -> R.drawable.avax
            "eth_optimism" -> R.drawable.eth_optimism
            "optimism" -> R.drawable.optimism
            "eth_arbitrum" -> R.drawable.eth_arbitrum
            "eth_base" -> R.drawable.eth_base
            "bsc" -> R.drawable.bsc
            "blast" -> R.drawable.eth_blast
            "cro" -> R.drawable.eth_cro
            "arbitrum" -> R.drawable.eth_arbitrum
            "kuji" -> R.drawable.kuji
            "atom" -> R.drawable.atom
            "polygon" -> R.drawable.polygon
            "tgt" -> R.drawable.tgt
            else -> R.drawable.danger
        }
    }
}