package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = NavyDark,
    primaryContainer = NavyDark,
    onPrimaryContainer = GoldBright,
    secondary = EmeraldGreen,
    onSecondary = Color.White,
    tertiary = GoldLight,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    error = CrimsonRed
)

private val LightColorScheme = lightColorScheme(
    primary = NavyDark,
    onPrimary = Color.White,
    primaryContainer = GoldLight,
    onPrimaryContainer = NavyDark,
    secondary = EmeraldGreen,
    onSecondary = Color.White,
    tertiary = GoldPrimary,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    error = CrimsonRed
)

@Composable
fun PrudentiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

