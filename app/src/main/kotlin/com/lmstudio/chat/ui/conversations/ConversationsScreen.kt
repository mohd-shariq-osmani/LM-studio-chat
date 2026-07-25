package com.lmstudio.chat.ui.conversations

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
import com.lmstudio.chat.ui.components.ConversationItem
import com.lmstudio.chat.ui.components.ConversationShimmer
import com.lmstudio.chat.ui.components.LmTopBar
import com.lmstudio.chat.util.applePressEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsScreen(
    onNavigateToChat: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNewChat: () -> Unit,
    viewModel: ConversationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var conversationToRename by remember { mutableStateOf<Long?>(null) }
    var renameTitleInput by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewChat,
                modifier = Modifier
                    .navigationBarsPadding()
                    .applePressEffect(onClick = onNavigateToNewChat),
                containerColor = AccentPrimary,
                contentColor = androidx.compose.ui.graphics.Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, "New Chat")
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
            // Unified Screen Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
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
                    text = "Conversations",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    color = TextPrimary
                )
            }

            // Search Bar Capsule
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchConversations(it)
                },
                placeholder = { Text("Search conversations...", color = TextTertiary) },
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
                ConversationShimmer(modifier = Modifier.weight(1f))
            } else if (state.conversations.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No conversations", color = TextTertiary, style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.conversations, key = { it.id }) { chat ->
                        ConversationItem(
                            conversation = chat,
                            onClick = { onNavigateToChat(chat.id) },
                            onPin = { viewModel.pinConversation(chat.id, !chat.isPinned) },
                            onArchive = { viewModel.archiveConversation(chat.id, !chat.isArchived) },
                            onDelete = { viewModel.deleteConversation(chat.id) },
                            onRename = {
                                conversationToRename = chat.id
                                renameTitleInput = chat.title
                            }
                        )
                    }
                }
            }
        }

        // Rename Dialog
        if (conversationToRename != null) {
            AlertDialog(
                onDismissRequest = { conversationToRename = null },
                title = { Text("Rename Conversation", color = TextPrimary) },
                text = {
                    OutlinedTextField(
                        value = renameTitleInput,
                        onValueChange = { renameTitleInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentPrimary,
                            unfocusedBorderColor = OutlineDefault
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            conversationToRename?.let { id ->
                                if (renameTitleInput.isNotBlank()) {
                                    viewModel.renameConversation(id, renameTitleInput)
                                }
                            }
                            conversationToRename = null
                        }
                    ) {
                        Text("Rename", color = AccentPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { conversationToRename = null }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = SurfaceElevated
            )
        }
    }
}
