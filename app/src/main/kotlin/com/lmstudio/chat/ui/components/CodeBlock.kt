package com.lmstudio.chat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lmstudio.chat.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CodeBlock(
    code: String,
    language: String = "",
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CodeBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceElevated)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language.ifBlank { "code" },
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(code))
                    scope.launch {
                        copied = true
                        delay(2000)
                        copied = false
                    }
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (copied) Icons.Default.Done else Icons.Default.ContentCopy,
                    contentDescription = "Copy code",
                    tint = if (copied) AccentPrimary else TextTertiary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                ),
                color = CodeText
            )
        }
    }
}
