package com.lmstudio.chat.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmstudio.chat.domain.model.Message
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.ui.components.LmTopBar
import com.lmstudio.chat.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToChat: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            LmTopBar(
                title = "Search History",
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.search(it)
                },
                placeholder = { Text("Search messages...", color = TextTertiary) },
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
            } else if (state.results.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(if (query.isBlank()) "Type above to search" else "No results found", color = TextTertiary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.results) { message ->
                        SearchResultCard(
                            message = message,
                            onClick = { onNavigateToChat(message.conversationId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    message: Message,
    onClick: () -> Unit
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = message.role.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                Text(
                    text = DateUtils.formatTimestamp(message.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
