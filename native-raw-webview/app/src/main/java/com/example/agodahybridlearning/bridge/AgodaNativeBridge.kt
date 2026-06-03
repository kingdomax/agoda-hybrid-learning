package com.example.agodahybridlearning.bridge

import android.os.Build
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.example.agodahybridlearning.session.NativeSessionStore
import org.json.JSONObject
import java.time.Instant

private const val LOG_TAG = "AgodaNativeBridge"

class AgodaNativeBridge(
    private val webViewProvider: () -> WebView?
) {
    // Receives JS → Native messages through postMessage(...)
    @JavascriptInterface
    fun postMessage(message: String) {
        Log.d(LOG_TAG, "JS → Native raw message: $message")

        try {
            val request = JSONObject(message)
            val id = request.optString("id", "")
            val type = request.optString("type", "")

            when (type) {
                "PING" -> {
                    sendToJs(
                        JSONObject()
                            .put("id", id)
                            .put("type", "PONG")
                            .put(
                                "payload",
                                JSONObject()
                                    .put("message", "Pong from Android native")
                                    .put("receivedAt", Instant.now().toString())
                            )
                    )
                }

                "GET_DEVICE_INFO" -> {
                    sendToJs(
                        JSONObject()
                            .put("id", id)
                            .put("type", "DEVICE_INFO")
                            .put(
                                "payload",
                                JSONObject()
                                    .put("platform", "android")
                                    .put("osVersion", Build.VERSION.RELEASE)
                                    .put("model", Build.MODEL)
                            )
                    )
                }

                "SAVE_TOKEN" -> {
                    val payload = request.optJSONObject("payload")
                    val token = payload?.optString("token", null)

                    NativeSessionStore.token = token

                    sendToJs(
                        JSONObject()
                            .put("id", id)
                            .put("type", "TOKEN_SAVED")
                            .put(
                                "payload",
                                JSONObject()
                                    .put("success", true)
                            )
                    )
                }

                "GET_TOKEN" -> {
                    sendToJs(
                        JSONObject()
                            .put("id", id)
                            .put("type", "TOKEN_VALUE")
                            .put(
                                "payload",
                                JSONObject()
                                    .put("token", NativeSessionStore.token)
                            )
                    )
                }

                else -> {
                    sendErrorToJs(
                        id = id,
                        message = "Unsupported native request type: $type"
                    )
                }
            }
        } catch (error: Exception) {
            Log.e(LOG_TAG, "Failed to handle JS message", error)

            sendErrorToJs(
                id = "",
                message = error.message ?: "Unknown native bridge error"
            )
        }
    }

    private fun sendErrorToJs(id: String, message: String) {
        sendToJs(
            JSONObject()
                .put("id", id)
                .put("type", "ERROR")
                .put(
                    "payload",
                    JSONObject()
                        .put("message", message)
                )
        )
    }

    // Sends Native → JS responses through webView.evaluateJavascript(...)
    private fun sendToJs(response: JSONObject) {
        val webView = webViewProvider()

        if (webView == null) {
            Log.e(LOG_TAG, "Cannot send Native → JS message because WebView is null")
            return
        }

        val responseJson = response.toString()
        val script = """
            window.AgodaNativeBridge?.onNativeMessage($responseJson);
        """.trimIndent()

        Log.d(LOG_TAG, "Native → JS script: $script")

        webView.post {
            webView.evaluateJavascript(script, null)
        }
    }
}