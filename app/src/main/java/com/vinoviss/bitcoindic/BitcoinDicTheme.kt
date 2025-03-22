package com.vinoviss.bitcoindic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Bitcoin Orange color
val BitcoinOrange = Color(0xFFF7931A)
val BitcoinOrangeLight = Color(0xFFFFB74D)
val BitcoinOrangeDark = Color(0xFFCC7700)

// Light color scheme with Bitcoin orange
private val LightColorScheme = lightColorScheme(
    primary = BitcoinOrange,
    onPrimary = Color.White,
    primaryContainer = BitcoinOrangeLight,
    onPrimaryContainer = Color(0xFF261900),
    secondary = BitcoinOrange,
    onSecondary = Color.White,
    secondaryContainer = BitcoinOrangeLight,
    onSecondaryContainer = Color(0xFF261900),
    tertiary = BitcoinOrangeDark,
    onTertiary = Color.White,
    tertiaryContainer = BitcoinOrangeLight,
    onTertiaryContainer = Color(0xFF261900),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = Color.White,
    onBackground = Color(0xFF201A17),
    surface = Color.White,
    onSurface = Color(0xFF201A17),
    surfaceVariant = Color(0xFFF3DFD2),
    onSurfaceVariant = Color(0xFF52443D),
    outline = Color(0xFF85746B),
    outlineVariant = Color(0xFFD7C2B9)
)

@Composable
fun BitcoinDicTheme(
    content: @Composable () -> Unit
) {
    // Always use light theme, dark theme is disabled
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}