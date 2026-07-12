package com.lmstudio.chat.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentPrimary,
    onPrimary = Background,
    primaryContainer = AccentPrimaryDim,
    onPrimaryContainer = AccentPrimary,
    secondary = AccentSecondary,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = AccentSecondary,
    tertiary = AccentWarning,
    onTertiary = Background,
    error = AccentDanger,
    onError = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = SurfaceContainer,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = OutlineDefault,
    outlineVariant = OutlineSubtle,
    inverseSurface = TextPrimary,
    inverseOnSurface = Background,
    surfaceContainerLowest = Background,
    surfaceContainerLow = SurfaceContainer,
    surfaceContainer = SurfaceVariant,
    surfaceContainerHigh = SurfaceElevated,
    surfaceContainerHighest = SurfaceCard
)

@Composable
fun LmStudioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
