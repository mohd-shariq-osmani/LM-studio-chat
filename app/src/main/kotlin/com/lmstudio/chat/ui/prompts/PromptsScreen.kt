package com.lmstudio.chat.ui.prompts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmstudio.chat.domain.model.Prompt
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.ui.components.LmTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PromptsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isCreateDialogOpen by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            LmTopBar(
                title = "Prompt Library",
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isCreateDialogOpen = true },
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
                    viewModel.searchPrompts(it)
                },
                placeholder = { Text("Search prompts...", color = TextTertiary) },
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
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentPrimary)
                }
            } else if (state.prompts.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No prompts found", color = TextTertiary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.prompts) { prompt ->
                        PromptCard(
                            prompt = prompt,
                            onClick = { clipboardManager.setText(AnnotatedString(prompt.content)) },
                            onFavoriteToggle = { viewModel.toggleFavorite(prompt.id, !prompt.isFavorite) },
                            onDelete = { viewModel.deletePrompt(prompt.id) }
                        )
                    }
                }
            }
        }

        if (isCreateDialogOpen) {
            AlertDialog(
                onDismissRequest = { isCreateDialogOpen = false },
                title = { Text("Add New Prompt", color = TextPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            placeholder = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = contentInput,
                            onValueChange = { contentInput = it },
                            placeholder = { Text("Content") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            maxLines = 6
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (titleInput.isNotBlank() && contentInput.isNotBlank()) {
                                viewModel.savePrompt(titleInput, contentInput)
                                titleInput = ""
                                contentInput = ""
                                isCreateDialogOpen = false
                            }
                        }
                    ) {
                        Text("Add", color = AccentPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isCreateDialogOpen = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = SurfaceElevated
            )
        }
    }
}

@Composable
fun PromptCard(
    prompt: Prompt,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prompt.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onFavoriteToggle, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (prompt.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (prompt.isFavorite) AccentDanger else TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = prompt.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
