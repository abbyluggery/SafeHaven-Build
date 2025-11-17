package app.neurothrive.safehaven.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * SafeHaven Material Design 3 Theme
 * Purple color scheme for calm, supportive feel
 */

// Color Palette
val PurplePrimary = Color(0xFF6B4EE6)
val PurpleSecondary = Color(0xFF9C89F5)
val PurpleTertiary = Color(0xFF7D5260)

val SafeGreen = Color(0xFF4CAF50)
val DangerRed = Color(0xFFF44336)
val WarningOrange = Color(0xFFFF9800)

// Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    secondary = PurpleSecondary,
    tertiary = PurpleTertiary,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    error = DangerRed,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    onError = Color.White
)

// Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = PurpleSecondary,
    secondary = PurplePrimary,
    tertiary = PurpleTertiary,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    error = DangerRed,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    onError = Color.White
)

@Composable
fun SafeHavenTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
