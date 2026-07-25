package com.lmstudio.chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmstudio.chat.domain.model.Message
import com.lmstudio.chat.domain.model.ModelInfo
import com.lmstudio.chat.domain.model.Persona
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.ui.components.LmTopBar
import com.lmstudio.chat.ui.components.MessageBubble
import com.lmstudio.chat.ui.components.TypingIndicator
import com.lmstudio.chat.util.applePressEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: Long?,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPersonas: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var textInput by remember { mutableStateOf("") }
    var attachedImages = remember { mutableStateListOf<String>() }
    val imageLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { attachedImages.add(it.toString()) }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var isPersonaSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect(conversationId) {
        viewModel.setConversation(conversationId)
    }

    // Auto-scroll to bottom on new messages
    LaunchedEffect(state.messages.size, state.isGenerating) {
        if (state.messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(state.messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            LmTopBar(
                title = state.selectedPersona?.name ?: "Chat",
                subtitle = "Tap to change persona • ${state.selectedModel.substringAfterLast("/")}",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { isPersonaSheetOpen = true }) {
                        Icon(Icons.Default.Face, "Change Persona", tint = TextPrimary)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, null, tint = TextPrimary)
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
                .imePadding()
        ) {
            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.messages) { message ->
                    MessageBubble(
                        message = message,
                        onCopy = { clipboardManager.setText(AnnotatedString(message.content)) },
                        onDelete = { viewModel.deleteMessage(message.id) },
                        onEdit = { viewModel.editMessage(message.id, it) },
                        onRegenerate = { viewModel.regenerateResponse() }
                    )
                }

                if (state.isGenerating && state.messages.none { it.isStreaming }) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // Preview selected images
            if (attachedImages.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    attachedImages.forEach { uri ->
                        Box(modifier = Modifier.size(60.dp)) {
                            coil.compose.AsyncImage(
                                model = uri,
                                contentDescription = "Attached preview",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove attachment",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(9.dp))
                                    .align(Alignment.TopEnd)
                                    .clickable { attachedImages.remove(uri) }
                            )
                        }
                    }
                }
            }

            // Error message display
            if (state.error.isNotBlank()) {
                Text(
                    text = state.error,
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorRed,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Input Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(SurfaceVariant.copy(alpha = 0.9f))
                        .border(0.5.dp, GlassBorder.copy(alpha = 0.3f), RoundedCornerShape(26.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { imageLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(40.dp)
                            .applePressEffect(onClick = { imageLauncher.launch("image/*") })
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Attach image",
                            tint = AccentPrimary
                        )
                    }

                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Message", color = TextTertiary) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 5
                    )

                    if (state.isGenerating) {
                        IconButton(
                            onClick = viewModel::stopGeneration,
                            modifier = Modifier
                                .size(36.dp)
                                .applePressEffect(onClick = viewModel::stopGeneration)
                                .clip(RoundedCornerShape(18.dp))
                                .background(AccentDanger)
                        ) {
                            Icon(Icons.Default.Stop, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        val isSendActive = textInput.isNotBlank() || attachedImages.isNotEmpty()
                        IconButton(
                            onClick = {
                                if (isSendActive) {
                                    viewModel.sendMessage(textInput, attachedImages.toList())
                                    textInput = ""
                                    attachedImages.clear()
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .applePressEffect(enabled = isSendActive, onClick = {
                                    if (isSendActive) {
                                        viewModel.sendMessage(textInput, attachedImages.toList())
                                        textInput = ""
                                        attachedImages.clear()
                                    }
                                })
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSendActive) AccentPrimary else OutlineSubtle)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (isSendActive) Color.White else TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Persona selector sheet (Apple Material Sheet)
        if (isPersonaSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isPersonaSheetOpen = false },
                containerColor = SurfaceElevated
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Choose Persona",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                    HorizontalDivider(color = OutlineSubtle)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                    ) {
                        items(state.personas) { persona ->
                            val accentColor = try {
                                Color(android.graphics.Color.parseColor(persona.color))
                            } catch (_: java.lang.Exception) {
                                AccentPrimary
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .applePressEffect(onClick = {
                                        viewModel.selectPersona(persona)
                                        isPersonaSheetOpen = false
                                    })
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(accentColor.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = persona.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = accentColor
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = persona.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = persona.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
