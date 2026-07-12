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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.ui.components.LmTopBar
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
            Text("LM Studio Connection", style = MaterialTheme.typography.titleMedium, color = AccentPrimary)

            OutlinedTextField(
                value = baseUrlInput,
                onValueChange = { baseUrlInput = it },
                label = { Text("Base URL") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = OutlineDefault
                )
            )

            OutlinedTextField(
                value = apiKeyInput,
                onValueChange = { apiKeyInput = it },
                label = { Text("API Key (optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = OutlineDefault
                )
            )

            Button(
                onClick = {
                    viewModel.updateBaseUrl(baseUrlInput)
                    viewModel.updateApiKey(apiKeyInput)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Connection Settings", color = Background)
            }

            HorizontalDivider(color = OutlineSubtle, modifier = Modifier.padding(vertical = 8.dp))

            Text("Parameters Override", style = MaterialTheme.typography.titleMedium, color = AccentPrimary)

            // Temperature Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Default Temperature", color = TextPrimary)
                    Text(String.format("%.2f", tempInput), color = AccentPrimary)
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
                    Text("Default Top P", color = TextPrimary)
                    Text(String.format("%.2f", topPInput), color = AccentPrimary)
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

            HorizontalDivider(color = OutlineSubtle, modifier = Modifier.padding(vertical = 8.dp))

            Text("Security Settings", style = MaterialTheme.typography.titleMedium, color = AccentPrimary)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Biometric App Lock", color = TextPrimary)
                    Text("Require fingerprint scan to open the app", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Switch(
                    checked = settings.appLockEnabled,
                    onCheckedChange = { viewModel.updateAppLockEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AccentPrimary,
                        checkedTrackColor = AccentPrimaryDim
                    )
                )
            }

            HorizontalDivider(color = OutlineSubtle, modifier = Modifier.padding(vertical = 8.dp))

            Text("App Maintenance", style = MaterialTheme.typography.titleMedium, color = ErrorRed)

            Button(
                onClick = { isResetDialogOpen = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentDanger),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.DeleteForever, null, tint = TextPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset All App Data", color = TextPrimary)
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
