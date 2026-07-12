package com.lmstudio.chat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lmstudio.chat.domain.model.Conversation
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.util.DateUtils

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit,
    onPin: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = conversation.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (conversation.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = AccentPrimary,
                        modifier = Modifier.size(12.dp).padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = DateUtils.formatTimestamp(conversation.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = conversation.lastMessagePreview.ifBlank { "No messages yet" },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = TextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(SurfaceElevated)
            ) {
                DropdownMenuItem(
                    text = { Text(if (conversation.isPinned) "Unpin" else "Pin", color = TextPrimary) },
                    onClick = { onPin(); showMenu = false },
                    leadingIcon = { Icon(Icons.Default.PushPin, null, tint = TextSecondary) }
                )
                DropdownMenuItem(
                    text = { Text("Rename", color = TextPrimary) },
                    onClick = { onRename(); showMenu = false },
                    leadingIcon = { Icon(Icons.Default.Edit, null, tint = TextSecondary) }
                )
                DropdownMenuItem(
                    text = { Text("Archive", color = TextPrimary) },
                    onClick = { onArchive(); showMenu = false },
                    leadingIcon = { Icon(Icons.Default.Archive, null, tint = TextSecondary) }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = ErrorRed) },
                    onClick = { onDelete(); showMenu = false },
                    leadingIcon = { Icon(Icons.Default.Delete, null, tint = ErrorRed) }
                )
            }
        }
    }
}
