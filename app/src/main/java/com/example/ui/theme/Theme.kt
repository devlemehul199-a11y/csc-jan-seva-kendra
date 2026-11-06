package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CscGold,
    onPrimary = Slate900,
    primaryContainer = CscNavyDark,
    onPrimaryContainer = Color.White,
    secondary = CscGoldLight,
    onSecondary = Slate900,
    background = Color(0xFF0A111C),
    surface = Color(0xFF131F33),
    onBackground = Slate100,
    onSurface = Slate100,
    surfaceVariant = Color(0xFF1E2E4A),
    onSurfaceVariant = Slate400,
    outline = Slate600
)

private val LightColorScheme = lightColorScheme(
    primary = CscNavyDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2EDF8),
    onPrimaryContainer = CscNavyDark,
    secondary = CscGold,
    onSecondary = Color.White,
    secondaryContainer = CscGoldLight,
    onSecondaryContainer = Color(0xFF8A5300),
    background = Color(0xFFF6F8FB),
    surface = Color.White,
    onBackground = Slate900,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded CSC colors consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
