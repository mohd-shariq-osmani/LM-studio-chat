package com.lmstudio.chat.util

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lmstudio.chat.theme.GlassBorder
import com.lmstudio.chat.theme.GlassFill

/**
 * Apple Design Rule #1 & #4: Instant touch response on pointer-down with bounceless critically-damped spring physics.
 */
fun Modifier.applePressEffect(
    enabled: Boolean = true,
    targetScale: Float = 0.96f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Critically damped spring (dampingRatio = 1.0, stiffness = Spring.StiffnessMediumLow)
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) targetScale else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "apple_scale_spring"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(
            if (onClick != null && enabled) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null, // Custom scale physical feedback instead of rectangular ripple
                    onClick = onClick
                )
            } else Modifier
        )
}

/**
 * Apple Design Material: Translucent Glass & Specular Rim Highlight.
 */
fun Modifier.appleGlass(
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    backgroundColor: Color = GlassFill,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 0.5.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor, shape)
    .border(borderWidth, borderColor, shape)
