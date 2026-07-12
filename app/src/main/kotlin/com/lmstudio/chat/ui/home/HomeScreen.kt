package com.lmstudio.chat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmstudio.chat.domain.model.Conversation
import com.lmstudio.chat.domain.model.Persona
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.ui.components.ModelSelector
import com.lmstudio.chat.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToChat: (Long) -> Unit,
    onNavigateToNewChat: () -> Unit,
    onNavigateToConversations: () -> Unit,
    onNavigateToPersonas: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToPrompts: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = SurfaceContainer) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToConversations,
                    icon = { Icon(Icons.Default.Chat, null) },
                    label = { Text("Chats") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToPersonas,
                    icon = { Icon(Icons.Default.Face, null) },
                    label = { Text("Personas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title and Greeting
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = DateUtils.getGreeting(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "LM Studio Chat",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, "Search", tint = TextPrimary)
                    }
                }
            }

            // Model Selection
            item {
                Text(
                    text = "Active Model",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                ModelSelector(
                    models = state.models,
                    selectedModel = state.selectedModel,
                    onModelSelected = viewModel::selectModel,
                    isLoading = state.isLoadingModels
                )
                if (state.error.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.error,
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed
                    )
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.startNewChat(onNavigateToChat) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Background)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("New Chat", color = Background, style = MaterialTheme.typography.titleSmall)
                    }
                    OutlinedButton(
                        onClick = onNavigateToPrompts,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineDefault)
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = TextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Prompts", style = MaterialTheme.typography.titleSmall)
                    }
                }
            }

            // Recent chats title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Chats",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    TextButton(onClick = onNavigateToConversations) {
                        Text("See All", color = AccentPrimary)
                    }
                }
            }

            if (state.recentChats.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCard)
                            .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No recent chats. Start one above!",
                            color = TextTertiary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(state.recentChats.take(3)) { chat ->
                    HomeChatCard(chat = chat, onClick = { onNavigateToChat(chat.id) })
                }
            }

            // Pinned personas title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Favorite Personas",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    TextButton(onClick = onNavigateToPersonas) {
                        Text("See All", color = AccentPrimary)
                    }
                }
            }

            if (state.favoritePersonas.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCard)
                            .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No favorite personas yet.",
                            color = TextTertiary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(state.favoritePersonas.take(3)) { persona ->
                    HomePersonaCard(
                        persona = persona,
                        onClick = {
                            viewModel.startNewChat { id ->
                                onNavigateToChat(id)
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HomeChatCard(
    chat: Conversation,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.ChatBubbleOutline, null, tint = TextTertiary)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = chat.lastMessagePreview.ifBlank { "No messages yet" },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = TextTertiary)
        }
    }
}

@Composable
fun HomePersonaCard(
    persona: Persona,
    onClick: () -> Unit
) {
    val accentColor = try {
        Color(android.graphics.Color.parseColor(persona.color))
    } catch (_: Exception) {
        AccentPrimary
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = persona.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleSmall,
                    color = accentColor
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = persona.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = persona.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.ArrowForward, null, tint = TextTertiary, modifier = Modifier.size(16.dp))
        }
    }
}
