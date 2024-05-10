package com.vultisig.wallet.common

import com.google.gson.JsonArray
import java.math.BigInteger

fun BigInteger.toJson(): JsonArray {
    val jsonArray = JsonArray()
    jsonArray.add("+")
    jsonArray.add(this)
    return jsonArray
}