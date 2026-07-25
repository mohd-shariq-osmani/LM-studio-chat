package com.lmstudio.chat.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lmstudio.chat.domain.model.Message
import com.lmstudio.chat.domain.model.MessageRole
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.util.DateUtils
import com.lmstudio.chat.util.applePressEffect

@Composable
fun MessageBubble(
    message: Message,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onEdit: ((String) -> Unit)? = null,
    onRegenerate: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bg = if (isUser) UserBubble else AssistantBubble
    val textColor = if (isUser) UserBubbleText else AssistantBubbleText

    var isEditing by remember { mutableStateOf(false) }
    var editContent by remember { mutableStateOf(message.content) }

    val bubbleShape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = if (isUser) 20.dp else 6.dp,
        bottomEnd = if (isUser) 6.dp else 20.dp
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .animateContentSize(),
        horizontalAlignment = alignment
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(bg)
                    .border(
                        width = 0.5.dp,
                        color = if (isUser) Color(0x33FFFFFF) else GlassBorder.copy(alpha = 0.3f),
                        shape = bubbleShape
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .widthIn(max = 320.dp)
            ) {
                Column {
                    if (message.images.isNotEmpty()) {
                        message.images.forEach { imageUri ->
                            coil.compose.AsyncImage(
                                model = imageUri,
                                contentDescription = "Attached image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 220.dp)
                                    .padding(bottom = 8.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }

                    if (isEditing) {
                        OutlinedTextField(
                            value = editContent,
                            onValueChange = { editContent = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentPrimary,
                                unfocusedBorderColor = OutlineDefault
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { isEditing = false }) {
                                Text("Cancel", color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onEdit?.invoke(editContent)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
                            ) {
                                Text("Save", color = Background)
                            }
                        }
                    } else {
                        if (isUser) {
                            Text(
                                text = message.content,
                                color = textColor,
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp)
                            )
                        } else {
                            MarkdownText(text = message.content, textColor = textColor)
                        }
                    }
                }
            }
        }

        if (!isEditing && !message.isStreaming) {
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 6.dp, end = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateUtils.formatTime(message.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                if (message.tokenCount > 0) {
                    Text(
                        text = "${message.tokenCount} tokens",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                if (message.isEdited) {
                    Text(
                        text = "Edited",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = TextTertiary,
                    modifier = Modifier
                        .size(14.dp)
                        .applePressEffect(onClick = onCopy)
                )
                if (isUser && onEdit != null) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TextTertiary,
                        modifier = Modifier
                            .size(14.dp)
                            .applePressEffect(onClick = { isEditing = true })
                    )
                }
                if (!isUser && onRegenerate != null) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Regenerate",
                        tint = TextTertiary,
                        modifier = Modifier
                            .size(14.dp)
                            .applePressEffect(onClick = onRegenerate)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TextTertiary,
                    modifier = Modifier
                        .size(14.dp)
                        .applePressEffect(onClick = onDelete)
                )
            }
        }
    }
}

