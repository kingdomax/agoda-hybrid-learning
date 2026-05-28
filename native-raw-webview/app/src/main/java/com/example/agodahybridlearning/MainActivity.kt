package com.example.agodahybridlearning

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
// import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val LOG_TAG = "AgodaHybrid"

private val Context.dataStore by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(LOG_TAG, "MainActivity onCreate")

        settingsRepository = SettingsRepository(applicationContext)

        setContent {
            MaterialTheme {
                AgodaHybridApp(settingsRepository)
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

class SettingsRepository(private val context: Context) {
    private val userIdKey = stringPreferencesKey("user_id")

    val userIdFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[userIdKey] ?: ""
        }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[userIdKey] = userId
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Settings : Screen("settings", "Settings")
    data object WebShell : Screen("web-shell", "Web Shell")
}

@Composable
fun AgodaHybridApp(settingsRepository: SettingsRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onOpenSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onOpenWebShell = {
                    navController.navigate(Screen.WebShell.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settingsRepository = settingsRepository,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.WebShell.route) {
            WebShellScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    onOpenWebShell: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Agoda Hybrid Learning") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Native Android Home",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "This is the native shell. Later, we will open a React app inside WebView."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Settings")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOpenWebShell,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Web Shell")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onBack: () -> Unit
) {
    val savedUserId by settingsRepository.userIdFlow.collectAsState(initial = "")
    var inputUserId by remember { mutableStateOf("") }

    /*  DataStore saveUserId is suspend.
        Button onClick itself is not suspend.
        LaunchedEffect lets us run suspend work from Compose.
        This is not the only way. Later, a better architecture uses:
        viewModelScope.launch { ... }

        In production, I would prefer:
        Composable → ViewModel → Repository → DataStore/native API */
    LaunchedEffect(savedUserId) {
        inputUserId = savedUserId
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Local Storage with DataStore",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputUserId,
                onValueChange = { inputUserId = it },
                label = { Text("User ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Log.d(LOG_TAG, "Save clicked with userId=$inputUserId")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log User ID Only")
            }

            Spacer(modifier = Modifier.height(8.dp))

            SaveUserIdButton(
                userId = inputUserId,
                settingsRepository = settingsRepository
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Saved User ID: $savedUserId")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
fun SaveUserIdButton(
    userId: String,
    settingsRepository: SettingsRepository
) {
    var saveCounter by remember { mutableStateOf(0) }

    LaunchedEffect(saveCounter) {
        if (saveCounter > 0) {
            settingsRepository.saveUserId(userId)
            Log.d(LOG_TAG, "Saved userId=$userId to DataStore")
        }
    }

    Button(
        onClick = {
            saveCounter++
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Save User ID to DataStore")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebShellScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Web Shell") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "WebView Placeholder",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "In Day 4, this screen will host a WebView that loads your local React app."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}