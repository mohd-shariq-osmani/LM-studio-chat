package com.lmstudio.chat.ui.personas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmstudio.chat.domain.model.Persona
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.ui.components.LmTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaDetailScreen(
    personaId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: PersonasViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf(0.7f) }
    var topP by remember { mutableStateOf(0.9f) }
    var maxTokens by remember { mutableStateOf(2048) }
    var contextLength by remember { mutableStateOf(4096) }
    var color by remember { mutableStateOf("#19C37D") }

    val isEditMode = personaId != null

    // Load existing persona details if editing
    LaunchedEffect(personaId, state.personas) {
        if (personaId != null && state.personas.isNotEmpty()) {
            val persona = state.personas.find { it.id == personaId }
            persona?.let {
                name = it.name
                description = it.description
                systemPrompt = it.systemPrompt
                temperature = it.temperature
                topP = it.topP
                maxTokens = it.maxTokens
                contextLength = it.contextLength
                color = it.color
            }
        }
    }

    Scaffold(
        topBar = {
            LmTopBar(
                title = if (isEditMode) "Edit Persona" else "Create Persona",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                scope.launch {
                                    val persona = Persona(
                                        id = personaId ?: 0,
                                        name = name,
                                        description = description,
                                        systemPrompt = systemPrompt,
                                        temperature = temperature,
                                        topP = topP,
                                        maxTokens = maxTokens,
                                        contextLength = contextLength,
                                        color = color,
                                        isBuiltin = false
                                    )
                                    if (isEditMode) {
                                        viewModel.personaRepository.updatePersona(persona)
                                    } else {
                                        viewModel.personaRepository.createPersona(persona)
                                    }
                                    onNavigateBack()
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Save, "Save", tint = AccentPrimary)
                    }
                }
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = OutlineDefault,
                    focusedLabelColor = AccentPrimary,
                    unfocusedLabelColor = TextSecondary
                )
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = OutlineDefault,
                    focusedLabelColor = AccentPrimary,
                    unfocusedLabelColor = TextSecondary
                )
            )

            OutlinedTextField(
                value = systemPrompt,
                onValueChange = { systemPrompt = it },
                label = { Text("System Prompt") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = OutlineDefault,
                    focusedLabelColor = AccentPrimary,
                    unfocusedLabelColor = TextSecondary
                ),
                maxLines = 10
            )

            // Temperature Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Temperature", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                    Text(String.format("%.2f", temperature), color = AccentPrimary, style = MaterialTheme.typography.bodyMedium)
                }
                Slider(
                    value = temperature,
                    onValueChange = { temperature = it },
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
                    Text("Top P", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                    Text(String.format("%.2f", topP), color = AccentPrimary, style = MaterialTheme.typography.bodyMedium)
                }
                Slider(
                    value = topP,
                    onValueChange = { topP = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = AccentPrimary,
                        inactiveTrackColor = OutlineSubtle,
                        thumbColor = AccentPrimary
                    )
                )
            }

            // Color Selection
            Column {
                Text("Persona Accent Color", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("#19C37D", "#3DDC84", "#7F52FF", "#4FC3F7", "#F89820", "#E53E3E").forEach { hex ->
                        val circleColor = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(circleColor)
                                .clickable { color = hex }
                                .border(
                                    width = if (color == hex) 3.dp else 0.dp,
                                    color = TextPrimary,
                                    shape = RoundedCornerShape(18.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}
