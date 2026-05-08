package com.aiub.esportsclub

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// We renamed this to AIService but kept the file name the same
// so we don't need to change any other file
class GeminiChatService {

    private val systemPrompt = """
        You are a helpful assistant for the AIUB eSports Club mobile app.
        Your name is eSports Bot.
        Help users with events, registration, players, profile and passwords.
        The app sidebar has: My Profile, View Events, Register Now,
        Our Players, View All Registrations.
        Keep answers short and friendly.
        If question is unrelated to the app or eSports, politely say so.
        Reply in the same language the user writes in.
    """.trimIndent()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // ===== GROQ API URL =====
    // Groq uses the same format as OpenAI API
    // so it's very simple and well documented
    private val apiUrl = "https://api.groq.com/openai/v1/chat/completions"

    suspend fun sendMessage(userMessage: String): String {

        val key = BuildConfig.GEMINI_API_KEY
        if (key.isNullOrEmpty() || key.isBlank()) {
            return "⚠️ Assistant not configured. Please contact the developer."
        }

        return withContext(Dispatchers.IO) {
            try {

                // ===== BUILD REQUEST BODY =====
                // Groq uses OpenAI format:
                // "messages" array with "role" and "content"
                // role "system" = instructions for the AI
                // role "user"   = what the user typed
                val requestJson = JSONObject().apply {
                    put("model", "llama-3.3-70b-versatile")// Free and fast Llama 3 model
                    put("messages", JSONArray().apply {

                        // System message — tells the AI how to behave
                        put(JSONObject().apply {
                            put("role", "system")
                            put("content", systemPrompt)
                        })

                        // User message — what the user actually asked
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", userMessage)
                        })
                    })
                    put("max_tokens", 500)
                    put("temperature", 0.7)
                }

                // ===== BUILD HTTP REQUEST =====
                val requestBody = requestJson.toString()
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(apiUrl)
                    .post(requestBody)
                    // Groq uses Bearer token authentication
                    // "Bearer" + your API key = permission to use the API
                    .addHeader("Authorization", "Bearer $key")
                    .addHeader("Content-Type", "application/json")
                    .build()

                // ===== SEND REQUEST =====
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                android.util.Log.d("GROQ_RESPONSE", "Code: ${response.code}")

                if (response.isSuccessful) {

                    // ===== PARSE RESPONSE =====
                    // Groq returns OpenAI format:
                    // choices[0].message.content = the AI's reply text
                    val jsonResponse = JSONObject(responseBody)
                    val choices     = jsonResponse.getJSONArray("choices")
                    val message     = choices.getJSONObject(0)
                        .getJSONObject("message")
                    val text        = message.getString("content")
                    text.trim()

                } else {
                    // Log the full error so we can see it
                    android.util.Log.e("GROQ_ERROR", "HTTP ${response.code}: $responseBody")
                    when (response.code) {
                        401 -> "⚠️ Invalid API key. Please contact the developer."
                        429 -> "⏳ Too many requests. Please wait a moment and try again."
                        503 -> "🔧 Service unavailable. Please try again in a moment."
                        else -> "❌ HTTP Error ${response.code}. Please try again."
                    }
                }


            } catch (e: Exception) {
                android.util.Log.e("GROQ_ERROR", "Error: ${e.message}", e)
                when {
                    e.message?.contains("network")           == true ||
                            e.message?.contains("Unable to resolve") == true ||
                            e.message?.contains("timeout")           == true ->
                        "📶 No internet connection. Please check your network."
                    else ->
                        "❌ Something went wrong. Please try again."
                }
            }
        }
    }
}