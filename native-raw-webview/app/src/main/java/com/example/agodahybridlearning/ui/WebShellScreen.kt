package com.example.agodahybridlearning.ui

import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import com.example.agodahybridlearning.bridge.AgodaNativeBridge

private const val LOG_TAG = "AgodaHybridWebView"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebShellScreen(
    url: String,
    onBack: () -> Unit
) {
    var webView: WebView? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(true) }
    var pageTitle by remember { mutableStateOf("Web Shell") }

    BackHandler {
        val currentWebView = webView

        if (currentWebView != null && currentWebView.canGoBack()) {
            currentWebView.goBack()
        } else {
            onBack()
        }
    }

    // This is function call but with passing lambda function as last parameter
    DisposableEffect(Unit) {
        onDispose {
            Log.d(LOG_TAG, "Disposing WebView")
            webView?.destroy()
            webView = null
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                LinearProgressIndicator()
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                factory = { context ->
                    WebView(context).apply {
                        // forward JS console logs to Android Logcat
                        webChromeClient = object : WebChromeClient() {
                            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                                Log.d(
                                    LOG_TAG,
                                    "JS console: ${consoleMessage.message()} -- ${consoleMessage.sourceId()}:${consoleMessage.lineNumber()}"
                                )
                                return true
                            }
                        }

                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT

                        // Expose this Kotlin object to JavaScript as window.AgodaNative
                        addJavascriptInterface(
                            AgodaNativeBridge(webViewProvider = { webView }),
                            "AgodaNative" // global JS object name
                        )

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?
                            ) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                                Log.d(LOG_TAG, "Page started: $url")
                            }

                            override fun onPageFinished(
                                view: WebView?,
                                url: String?
                            ) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                pageTitle = view?.title ?: "Web Shell"
                                Log.d(LOG_TAG, "Page finished: $url")
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)

                                if (request?.isForMainFrame == true) {
                                    isLoading = false
                                    Log.e(
                                        LOG_TAG,
                                        "Main frame error: ${error?.description}"
                                    )
                                }
                            }
                        }

                        loadUrl(url)
                        webView = this
                    }
                },
                update = { view ->
                    if (view.url == null) {
                        view.loadUrl(url)
                    }
                }
            )

            Button(
                onClick = onBack,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Back to Native")
            }
        }
    }
}