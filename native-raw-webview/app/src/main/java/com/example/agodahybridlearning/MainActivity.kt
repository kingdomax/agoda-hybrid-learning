package com.example.agodahybridlearning

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.agodahybridlearning.data.SettingsRepository
import com.example.agodahybridlearning.navigation.AppNavigation
import android.webkit.WebView

private const val LOG_TAG = "AgodaHybrid"

class MainActivity : ComponentActivity() {
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // In production, this should normally be enabled only for debug builds
        WebView.setWebContentsDebuggingEnabled(true)

        Log.d(LOG_TAG, "MainActivity onCreate")

        settingsRepository = SettingsRepository(applicationContext)

        setContent {
            MaterialTheme {
                AppNavigation(settingsRepository = settingsRepository)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "MainActivity onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "MainActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(LOG_TAG, "MainActivity onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LOG_TAG, "MainActivity onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "MainActivity onDestroy")
    }
}