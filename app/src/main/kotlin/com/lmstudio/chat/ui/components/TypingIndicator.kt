package com.lmstudio.chat.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.lmstudio.chat.theme.AssistantBubble
import com.lmstudio.chat.theme.TextTertiary

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    @Composable
    fun animatedDot(delayMs: Int): Float {
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, delayMillis = delayMs, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot_scale_$delayMs"
        )
        return scale
    }

    Row(
        modifier = modifier
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AssistantBubble)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf(0, 200, 400).forEach { delay ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(animatedDot(delay))
                    .clip(CircleShape)
                    .background(TextTertiary)
            )
        }
    }
}
