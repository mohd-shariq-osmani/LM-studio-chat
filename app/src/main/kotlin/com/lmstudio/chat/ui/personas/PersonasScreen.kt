package com.lmstudio.chat.ui.personas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.ui.components.PersonaCard
import com.lmstudio.chat.util.applePressEffect

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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(SurfaceVariant)
                            .border(0.5.dp, GlassBorder, androidx.compose.foundation.shape.CircleShape)
                            .applePressEffect(onClick = onNavigateBack)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Personas",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = TextPrimary
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreatePersona,
                modifier = Modifier
                    .navigationBarsPadding()
                    .applePressEffect(onClick = onNavigateToCreatePersona),
                containerColor = AccentPrimary,
                contentColor = androidx.compose.ui.graphics.Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, "New Persona")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            // Search Bar Capsule
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
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceVariant.copy(alpha = 0.85f),
                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.85f),
                    focusedBorderColor = GlassBorder,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

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
