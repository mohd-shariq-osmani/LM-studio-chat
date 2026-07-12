package com.lmstudio.chat.ui.personas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.ui.components.LmTopBar
import com.lmstudio.chat.ui.components.PersonaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonasScreen(
    onNavigateToPersonaDetail: (Long) -> Unit,
    onNavigateToCreatePersona: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PersonasViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var menuPersonaId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            LmTopBar(
                title = "Personas",
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreatePersona,
                containerColor = AccentPrimary,
                contentColor = Background
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchPersonas(it)
                },
                placeholder = { Text("Search personas...", color = TextTertiary) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceVariant,
                    unfocusedContainerColor = SurfaceVariant,
                    focusedBorderColor = OutlineBright,
                    unfocusedBorderColor = OutlineSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentPrimary)
                }
            } else if (state.personas.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No personas found", color = TextTertiary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.personas, key = { it.id }) { persona ->
                        PersonaCard(
                            persona = persona,
                            onClick = { onNavigateToPersonaDetail(persona.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(persona.id, !persona.isFavorite) },
                            onMenuClick = { menuPersonaId = persona.id }
                        )
                    }
                }
            }
        }

        // Action menu
        if (menuPersonaId != null) {
            val selectedId = menuPersonaId!!
            val selectedPersona = state.personas.find { it.id == selectedId }
            selectedPersona?.let { persona ->
                ModalBottomSheet(
                    onDismissRequest = { menuPersonaId = null },
                    containerColor = SurfaceElevated
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Text(
                            text = persona.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            modifier = Modifier.padding(16.dp)
                        )
                        HorizontalDivider(color = OutlineSubtle)
                        DropdownMenuItem(
                            text = { Text("Set as Default", color = TextPrimary) },
                            onClick = {
                                viewModel.setDefaultPersona(selectedId)
                                menuPersonaId = null
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Persona", color = TextPrimary) },
                            onClick = {
                                onNavigateToPersonaDetail(selectedId)
                                menuPersonaId = null
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duplicate Persona", color = TextPrimary) },
                            onClick = {
                                viewModel.duplicatePersona(selectedId)
                                menuPersonaId = null
                            }
                        )
                        if (!persona.isBuiltin) {
                            DropdownMenuItem(
                                text = { Text("Delete Persona", color = ErrorRed) },
                                onClick = {
                                    viewModel.deletePersona(selectedId)
                                    menuPersonaId = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
