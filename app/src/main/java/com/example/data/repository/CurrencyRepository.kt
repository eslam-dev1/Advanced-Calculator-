package com.example.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object CurrencyRepository {

    private val defaultRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "EGP" to 48.5,
        "SAR" to 3.75,
        "AED" to 3.67,
        "KWD" to 0.308,
        "QAR" to 3.64,
        "JPY" to 154.2,
        "CAD" to 1.38,
        "AUD" to 1.52,
        "CNY" to 7.23,
        "INR" to 83.5,
        "CHF" to 0.90
    )

    var currentRates: Map<String, Double> = defaultRates
        private set

    var isLive: Boolean = false
        private set

    suspend fun fetchLatestRates(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://open.er-api.com/v6/latest/USD")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 6000
            conn.readTimeout = 6000
            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseText)
                if (json.optString("result") == "success") {
                    val ratesObj = json.getJSONObject("rates")
                    val updatedRates = mutableMapOf<String, Double>()
                    val keys = ratesObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        updatedRates[key] = ratesObj.getDouble(key)
                    }
                    currentRates = updatedRates
                    isLive = true
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLive = false
        return@withContext false
    }

    fun convertCurrency(from: String, to: String, amount: Double): Double {
        val fromRate = currentRates[from] ?: 1.0
        val toRate = currentRates[to] ?: 1.0
        val inUsd = amount / fromRate
        return inUsd * toRate
    }
}
