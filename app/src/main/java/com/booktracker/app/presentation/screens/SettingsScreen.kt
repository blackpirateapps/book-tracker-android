package com.booktracker.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NightsStay
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val ScreenBg = Color(0xFFF2F2F7)
private val CardBg = Color(0xFFFFFFFF)
private val SectionLabel = Color(0xFF8E8E93)
private val SubtleText = Color(0xFF8E8E93)
private val PrimaryBlue = Color(0xFF007AFF)
private val SuccessGreen = Color(0xFF34C759)
private val DividerColor = Color(0xFFE5E5EA)

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

    Scaffold(
        containerColor = ScreenBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.displayLarge,
                color = Color.Black
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabelText("API CONFIGURATION")
                CardContainer {
                    SettingRow(
                        icon = Icons.Default.Link,
                        title = "API Base URL",
                        subtitle = apiDomain.ifBlank { "https://notes.blackpiratex.com" }
                    )
                    RowDivider()
                    SettingRow(
                        icon = Icons.Default.Key,
                        title = "API Password",
                        subtitle = if (apiPassword.isNotBlank()) "••••••••" else "••••••••"
                    )
                    RowDivider()
                    SettingRow(
                        icon = Icons.Default.Language,
                        title = "Default Hostname",
                        subtitle = deriveHostname(apiDomain)
                    )
                }

                if (testResult == true) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen
                        )
                        Text(
                            text = "Connected",
                            color = SuccessGreen,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    ),
                    enabled = !isTesting && apiDomain.isNotBlank()
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Test Connection")
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabelText("APPEARANCE")
                CardContainer {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                            Text(
                                text = "Dark Mode",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Switch(
                            checked = isDarkModeEnabled,
                            onCheckedChange = onToggleDarkMode
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabelText("ABOUT")
                CardContainer {
                    InfoRow(label = "Version", value = "1.2.0")
                    RowDivider()
                    InfoRow(label = "Developer", value = "BlackPirate Apps")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CardContainer(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = CardBg,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
private fun SectionLabelText(text: String) {
    Text(
        text = text,
        color = SectionLabel,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SubtleText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = SubtleText)
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DividerColor)
    )
}

private fun deriveHostname(domain: String): String {
    val trimmed = domain.trim()
    if (trimmed.isBlank()) return ""
    val noScheme = trimmed.removePrefix("https://").removePrefix("http://")
    return noScheme.substringBefore("/")
}
