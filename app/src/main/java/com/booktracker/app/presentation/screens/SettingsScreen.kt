package com.booktracker.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    isDarkModeEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    apiDomain: String = "",
    onApiDomainChanged: (String) -> Unit = {},
    apiPassword: String = "",
    onApiPasswordChanged: (String) -> Unit = {},
    onTestConnection: suspend () -> Result<Boolean> = { Result.success(true) },
    onForceRefresh: () -> Unit = {},
    onFetchRawPublic: suspend (Int, Int) -> Result<String> = { _, _ -> Result.success("[]") },
    onFetchRawBooks: suspend () -> Result<String> = { Result.success("[]") }
) {
    val scope = rememberCoroutineScope()
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<Boolean?>(null) }
    var isFetchingRaw by remember { mutableStateOf(false) }
    var rawResult by remember { mutableStateOf<String?>(null) }
    var rawError by remember { mutableStateOf<String?>(null) }
    var rawTitle by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Appearance Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dark Mode",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Toggle the application theme",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = isDarkModeEnabled,
                            onCheckedChange = onToggleDarkMode
                        )
                    }
                }
            }

            // Backend Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Backend",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = apiDomain,
                            onValueChange = { 
                                onApiDomainChanged(it)
                                testResult = null
                            },
                            label = { Text("API Domain") },
                            placeholder = { Text("https://api.example.com") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = apiPassword,
                            onValueChange = { 
                                onApiPasswordChanged(it)
                                testResult = null
                            },
                            label = { Text("API Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                isTesting = true
                                testResult = null
                                scope.launch {
                                    val result = onTestConnection()
                                    testResult = result.isSuccess
                                    isTesting = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isTesting && apiDomain.isNotBlank()
                        ) {
                            if (isTesting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Test Connection")
                            }
                        }

                        if (testResult != null) {
                            val color = if (testResult == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            val text = if (testResult == true) "Connection successful!" else "Connection failed. Check URL or password."
                            Text(text = text, color = color, style = MaterialTheme.typography.bodyMedium)
                        }

                        OutlinedButton(
                            onClick = onForceRefresh,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = apiDomain.isNotBlank()
                        ) {
                            Text("Force Refresh")
                        }
                    }
                }
            }

            // Debug Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Debug",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                isFetchingRaw = true
                                rawResult = null
                                rawError = null
                                rawTitle = "/api/public?limit=3&offset=0"
                                scope.launch {
                                    val result = onFetchRawPublic(3, 0)
                                    if (result.isSuccess) {
                                        rawResult = result.getOrNull()
                                    } else {
                                        rawError = result.exceptionOrNull()?.message ?: "Unknown error"
                                    }
                                    isFetchingRaw = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isFetchingRaw && apiDomain.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            if (isFetchingRaw) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Fetch Raw /api/public")
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                isFetchingRaw = true
                                rawResult = null
                                rawError = null
                                rawTitle = "/api/books"
                                scope.launch {
                                    val result = onFetchRawBooks()
                                    if (result.isSuccess) {
                                        rawResult = result.getOrNull()
                                    } else {
                                        rawError = result.exceptionOrNull()?.message ?: "Unknown error"
                                    }
                                    isFetchingRaw = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isFetchingRaw && apiDomain.isNotBlank()
                        ) {
                            Text("Fetch Raw /api/books")
                        }

                        if (rawError != null) {
                            Text(
                                text = rawError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (rawResult != null) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (rawTitle != null) {
                                        Text(
                                            text = rawTitle ?: "",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = rawResult ?: "",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 100.dp, max = 220.dp)
                                            .verticalScroll(rememberScrollState()),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
