package com.vinoviss.bitcoindic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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

// Dark color scheme with Bitcoin orange
private val DarkColorScheme = darkColorScheme(
    primary = BitcoinOrange,
    onPrimary = Color.Black,
    primaryContainer = BitcoinOrangeDark,
    onPrimaryContainer = Color(0xFFFFEAD0),
    secondary = BitcoinOrange,
    onSecondary = Color.Black,
    secondaryContainer = BitcoinOrangeDark,
    onSecondaryContainer = Color(0xFFFFEAD0),
    tertiary = BitcoinOrangeLight,
    onTertiary = Color.Black,
    tertiaryContainer = BitcoinOrangeDark,
    onTertiaryContainer = Color(0xFFFFEAD0),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color.Black,
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F)
)

@Composable
fun BitcoinDicTheme(
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