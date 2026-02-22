package com.booktracker.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SectionHeader("API CONFIGURATION")
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingRow(
                        icon = Icons.Default.Link,
                        title = "API Base URL",
                        subtitle = apiDomain.ifBlank { "https://notes.blackpiratex.com" }
                    )
                    OutlinedTextField(
                        value = apiDomain,
                        onValueChange = {
                            onApiDomainChanged(it)
                            testResult = null
                        },
                        placeholder = { Text("https://notes.blackpiratex.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    SettingRow(
                        icon = Icons.Default.Lock,
                        title = "API Password",
                        subtitle = if (apiPassword.isNotBlank()) "••••••••" else "Not set"
                    )
                    OutlinedTextField(
                        value = apiPassword,
                        onValueChange = {
                            onApiPasswordChanged(it)
                            testResult = null
                        },
                        placeholder = { Text("********") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (testResult != null) {
                        val isOk = testResult == true
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = if (isOk) Color(0xFF1DB954) else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = if (isOk) "Connected" else "Connection failed",
                                color = if (isOk) Color(0xFF1DB954) else MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

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

                    OutlinedButton(
                        onClick = onForceRefresh,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = apiDomain.isNotBlank()
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Force Refresh")
                    }
                }
            }

            SectionHeader("APPEARANCE")
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                    }

                    Switch(
                        checked = isDarkModeEnabled,
                        onCheckedChange = onToggleDarkMode
                    )
                }
            }

            SectionHeader("DEBUG")
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
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
                        enabled = !isFetchingRaw && apiDomain.isNotBlank()
                    ) {
                        if (isFetchingRaw) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
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
                            color = MaterialTheme.colorScheme.surfaceVariant,
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

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
