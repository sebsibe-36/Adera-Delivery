package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreenPrimary,
    onPrimary = PureWhite,
    primaryContainer = SoftGreenContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = LightGreenSecondary,
    onSecondary = PureWhite,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = GoldAccent,
    onTertiary = DarkGrayText,
    tertiaryContainer = Color(0xFFFFF8E1),
    onTertiaryContainer = Color(0xFF5D4037),
    background = PureWhite,
    onBackground = DarkGrayText,
    surface = PureWhite,
    onSurface = DarkGrayText,
    surfaceVariant = OffWhiteSurface,
    onSurfaceVariant = MediumGrayText,
    outline = LightGrayBorder,
    outlineVariant = Color(0xFFEEEEEE)
)

private val DarkColorScheme = darkColorScheme(
    primary = LightGreenSecondary,
    onPrimary = Color(0xFF00390E),
    primaryContainer = EmeraldGreenDark,
    onPrimaryContainer = SoftGreenContainer,
    secondary = LightGreenSecondary,
    onSecondary = Color(0xFF00390E),
    tertiary = GoldAccent,
    onTertiary = DarkGrayText,
    background = Color(0xFF121412),
    onBackground = Color(0xFFE2E3E1),
    surface = Color(0xFF1A1C1A),
    onSurface = Color(0xFFE2E3E1),
    surfaceVariant = Color(0xFF242824),
    onSurfaceVariant = Color(0xFFC2C8C2),
    outline = Color(0xFF404640)
)

@Composable
fun AderaTheme(
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
