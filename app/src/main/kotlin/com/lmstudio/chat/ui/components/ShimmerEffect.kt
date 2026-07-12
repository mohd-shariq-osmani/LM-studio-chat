package com.lmstudio.chat.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lmstudio.chat.theme.SurfaceCard
import com.lmstudio.chat.theme.SurfaceElevated

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Float = 8f
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            SurfaceCard,
            SurfaceElevated,
            Color(0xFF2A2A2A),
            SurfaceElevated,
            SurfaceCard
        ),
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim + 200f, 0f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

@Composable
fun ConversationShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerBox(modifier = Modifier.size(44.dp), cornerRadius = 22f)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp))
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.9f).height(12.dp))
                }
            }
        }
    }
}
