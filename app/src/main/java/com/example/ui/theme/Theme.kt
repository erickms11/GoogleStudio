package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EquilibriumColorScheme = darkColorScheme(
    primary = EmeraldHarmony,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = EmeraldLight,
    secondary = GoldPrimary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = GoldLight,
    tertiary = PillarReading,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF334155),
    error = OverloadRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Dark mode default as specified in PRD
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EquilibriumColorScheme,
        typography = Typography,
        content = content
    )
}
