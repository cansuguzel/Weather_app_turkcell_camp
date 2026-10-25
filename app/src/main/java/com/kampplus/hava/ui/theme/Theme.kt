package com.kampplus.hava.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NightColors = darkColorScheme(
    primary = AccentYellow,
    onPrimary = Color(0xFF241A00),
    primaryContainer = Color(0xFF594B24),
    onPrimaryContainer = AccentYellow,
    secondary = Color(0xFFC9BFFF),
    onSecondary = Color(0xFF241A00),
    background = NightBackground,
    onBackground = MainText,
    surface = NightBackground,
    onSurface = MainText,
    surfaceVariant = Color(0xFF352F5B),
    onSurfaceVariant = SecondaryText,
    surfaceContainer = Color(0xFF1F1945),
    surfaceContainerHigh = Color(0xFF2A2450),
    surfaceContainerHighest = Color(0xFF352F5B),
    error = ErrorAccent,
    outline = GlassBorder
)

@Composable
@Suppress("UNUSED_PARAMETER")
fun HavaTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NightColors,
        typography = HavaTypography,
        content = content
    )
}
