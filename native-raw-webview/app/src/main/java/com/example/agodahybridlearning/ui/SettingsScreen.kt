package com.example.agodahybridlearning.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agodahybridlearning.data.SettingsRepository

private const val LOG_TAG = "AgodaHybrid"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onBack: () -> Unit
) {
    val savedUserId by settingsRepository.userIdFlow.collectAsState(initial = "")
    var inputUserId by remember { mutableStateOf("") }

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
private fun SaveUserIdButton(
    userId: String,
    settingsRepository: SettingsRepository
) {
    var saveCounter by remember { mutableIntStateOf(0) }

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