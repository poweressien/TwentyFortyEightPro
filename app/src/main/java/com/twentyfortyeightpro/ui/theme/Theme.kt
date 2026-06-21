package com.twentyfortyeightpro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF2A1A06),
    secondary = Magenta,
    tertiary = Cyan,
    background = BgDeep,
    surface = BgPanel,
    surfaceVariant = BgPanelRaised,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Danger,
)

// MaterialTheme requires a light scheme too even though this app is dark-first;
// reuse the same tokens so a light theme toggle (matching the web app's
// "Light Mode") is just a future ColorScheme swap, not a redesign.
private val LightScheme = lightColorScheme(
    primary = GoldDeep,
    secondary = Magenta,
    tertiary = Cyan,
    background = Color(0xFFF4F1EA),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF221B35),
    onSurface = Color(0xFF221B35),
    error = Danger,
)

@Composable
fun TwentyFortyEightProTheme(
    useDarkTheme: Boolean = true, // dark-by-default like the web app; wire to a settings toggle later
    content: @Composable () -> Unit,
) {
    val colorScheme = if (useDarkTheme) DarkScheme else LightScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
