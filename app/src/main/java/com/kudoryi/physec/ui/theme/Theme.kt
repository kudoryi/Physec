package com.kudoryi.physec.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = Color.White,
    secondary = Teal600,
    onSecondary = Color.White,
    tertiary = WarningAmber,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = NeutralSlateBg,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    secondary = Teal500,
    onSecondary = Color.White,
    tertiary = WarningAmberDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = NeutralSlateBgDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark
)

@Composable
fun PhySecTheme(
    forceDarkTheme: Boolean? = null,
    darkTheme: Boolean = forceDarkTheme ?: isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
