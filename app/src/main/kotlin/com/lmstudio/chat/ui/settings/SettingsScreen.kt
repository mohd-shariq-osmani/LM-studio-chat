package com.lmstudio.chat.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.ui.components.LmTopBar
import com.lmstudio.chat.util.applePressEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val scope = rememberCoroutineScope()

    var baseUrlInput by remember(settings.baseUrl) { mutableStateOf(settings.baseUrl) }
    var apiKeyInput by remember(settings.apiKey) { mutableStateOf(settings.apiKey) }
    var tempInput by remember(settings.temperature) { mutableStateOf(settings.temperature) }
    var topPInput by remember(settings.topP) { mutableStateOf(settings.topP) }

    var isResetDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LmTopBar(
                title = "Settings",
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("LM STUDIO CONNECTION", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextSecondary)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, GlassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = baseUrlInput,
                        onValueChange = { baseUrlInput = it },
                        label = { Text("Base URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentPrimary,
                            unfocusedBorderColor = OutlineDefault,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("API Key (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentPrimary,
                            unfocusedBorderColor = OutlineDefault,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Button(
                        onClick = {
                            viewModel.updateBaseUrl(baseUrlInput)
                            viewModel.updateApiKey(apiKeyInput)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .applePressEffect(onClick = {
                                viewModel.updateBaseUrl(baseUrlInput)
                                viewModel.updateApiKey(apiKeyInput)
                            }),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Connection Settings", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.titleSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("PARAMETERS OVERRIDE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextSecondary)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, GlassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Temperature Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Default Temperature", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                            Text(String.format("%.2f", tempInput), color = AccentPrimary, style = MaterialTheme.typography.labelLarge)
                        }
                        Slider(
                            value = tempInput,
                            onValueChange = {
                                tempInput = it
                                viewModel.updateTemperature(it)
                            },
                            valueRange = 0f..2f,
                            colors = SliderDefaults.colors(
                                activeTrackColor = AccentPrimary,
                                inactiveTrackColor = OutlineSubtle,
                                thumbColor = AccentPrimary
                            )
                        )
                    }

                    // Top P Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Default Top P", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                            Text(String.format("%.2f", topPInput), color = AccentPrimary, style = MaterialTheme.typography.labelLarge)
                        }
                        Slider(
                            value = topPInput,
                            onValueChange = {
                                topPInput = it
                                viewModel.updateTopP(it)
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                activeTrackColor = AccentPrimary,
                                inactiveTrackColor = OutlineSubtle,
                                thumbColor = AccentPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("SECURITY", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextSecondary)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Biometric App Lock", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                        Text("Require fingerprint scan to open the app", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Switch(
                        checked = settings.appLockEnabled,
                        onCheckedChange = { viewModel.updateAppLockEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                            checkedTrackColor = AccentPrimary,
                            uncheckedTrackColor = SurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("MAINTENANCE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = ErrorRed)

            Button(
                onClick = { isResetDialogOpen = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .applePressEffect(onClick = { isResetDialogOpen = true }),
                colors = ButtonDefaults.buttonColors(containerColor = AccentDanger),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.DeleteForever, null, tint = androidx.compose.ui.graphics.Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset All App Data", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.titleSmall)
            }
        }

        if (isResetDialogOpen) {
            AlertDialog(
                onDismissRequest = { isResetDialogOpen = false },
                title = { Text("Reset Application Data?", color = TextPrimary) },
                text = { Text("This will permanently delete all chat history, custom personas, and settings overrides.", color = TextSecondary) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearDatabase()
                            isResetDialogOpen = false
                            onNavigateBack()
                        }
                    ) {
                        Text("Reset Everything", color = ErrorRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isResetDialogOpen = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = SurfaceElevated
            )
        }
    }
}
