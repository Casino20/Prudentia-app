package com.example.data.api

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AlexaAIService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val systemPrompt = """
        You are Alexa AI, the official AI Guide & Assistant for PRUDENTI INVESTMENT SITE.
        You speak in a warm, professional, encouraging financial guide tone.
        Key Platform Information:
        - Starter Plan: Cost GH₵ 120 -> Earnings GH₵ 10 daily -> Referral Bonus GH₵ 60.
        - Standard Plan: Cost GH₵ 220 -> Earnings GH₵ 16 daily -> Referral Bonus GH₵ 90.
        - Premium Plan: Cost GH₵ 300 -> Earnings GH₵ 22 daily -> Referral Bonus GH₵ 130.
        - Withdrawals: Investment earnings withdrawable ONLY on SUNDAYS. Referral commissions withdrawable ONLY on FRIDAYS.
        - Minimum withdrawal is GH₵ 20.
        - Deposits: Pay to Payment Number 0507861747 (Account Name: Maxwell) and upload receipt screenshot.
        - Registration requires a valid, unused Coupon Code from an authorized Vendor.
        Answer user questions concisely, accurately, and assist them in navigating daily tasks, referral links, deposits, and earnings.
    """.trimIndent()

    suspend fun askAlexa(userMessage: String): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val jsonBody = JSONObject().apply {
                    val contentsArr = JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val partsArr = JSONArray().apply {
                                put(JSONObject().put("text", userMessage))
                            }
                            put("parts", partsArr)
                        }
                        put(contentObj)
                    }
                    put("contents", contentsArr)

                    val sysObj = JSONObject().apply {
                        val partsArr = JSONArray().apply {
                            put(JSONObject().put("text", systemPrompt))
                        }
                        put("parts", partsArr)
                    }
                    put("systemInstruction", sysObj)
                }

                val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val respString = response.body?.string()
                        if (!respString.isNullOrEmpty()) {
                            val respJson = JSONObject(respString)
                            val candidates = respJson.optJSONArray("candidates")
                            if (candidates != null && candidates.length() > 0) {
                                val content = candidates.getJSONObject(0).optJSONObject("content")
                                val parts = content?.optJSONArray("parts")
                                if (parts != null && parts.length() > 0) {
                                    val reply = parts.getJSONObject(0).optString("text")
                                    if (reply.isNotBlank()) return@withContext reply
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to local intelligent assistant logic
            }
        }

        // Local Smart Domain Assistant Fallback
        val msg = userMessage.lowercase()
        when {
            msg.contains("withdraw") || msg.contains("cashout") -> {
                "Withdrawal Schedule on Prudenti:\n• Investment Daily Earnings can be withdrawn on **SUNDAYS**.\n• Referral Commissions can be withdrawn on **FRIDAYS**.\nMinimum withdrawal amount is GH₵ 20.00."
            }
            msg.contains("plan") || msg.contains("package") || msg.contains("starter") || msg.contains("premium") -> {
                "Prudenti Investment Packages:\n1. **Starter**: GH₵120 cost → GH₵10 daily return (Ref Bonus: GH₵60)\n2. **Standard**: GH₵220 cost → GH₵16 daily return (Ref Bonus: GH₵90)\n3. **Premium**: GH₵300 cost → GH₵22 daily return (Ref Bonus: GH₵130)"
            }
            msg.contains("coupon") || msg.contains("code") || msg.contains("vendor") -> {
                "Coupon Codes are required during registration. You can chat with our verified Vendors on the Registration or Vendors page to purchase your coupon code directly."
            }
            msg.contains("deposit") || msg.contains("pay") || msg.contains("maxwell") -> {
                "To Deposit:\n1. Send payment to Mobile Money Number: **0507861747** (Account Name: **Maxwell**).\n2. Navigate to the Deposit page.\n3. Enter the amount paid and upload your receipt screenshot.\n4. Admin will verify and credit your balance shortly!"
            }
            msg.contains("task") || msg.contains("earn") -> {
                "Daily Tasks allow you to earn extra income! Complete social engagements like liking Facebook pages or following TikTok accounts daily, then tap 'Claim Reward' to instantly credit your balance."
            }
            msg.contains("referral") || msg.contains("link") || msg.contains("invite") -> {
                "Your unique referral link is on your Dashboard. Share it with friends to earn up to GH₵ 130 per referral! Referral commissions are withdrawable every Friday."
            }
            else -> {
                "Hello! I am Alexa AI, your Prudenti Investment Guide. How can I assist you today with your investments, deposits, daily tasks, or withdrawals?"
            }
        }
    }
}
