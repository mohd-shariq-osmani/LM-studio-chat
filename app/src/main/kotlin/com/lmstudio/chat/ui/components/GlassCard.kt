package com.lmstudio.chat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lmstudio.chat.theme.GlassBorder
import com.lmstudio.chat.theme.GlassFill
import com.lmstudio.chat.theme.SurfaceCard
import com.lmstudio.chat.util.applePressEffect

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .applePressEffect(enabled = onClick != null, onClick = onClick)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SurfaceCard.copy(alpha = 0.85f),
                        Color(0xFF141416).copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(GlassBorder, Color(0x1AFFFFFF))
                ),
                shape = shape
            ),
        content = content
    )
}

