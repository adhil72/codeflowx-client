import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.screens.Splash
import ui.screens.home.Home


enum class Screen {
    Splash, Home
}

val CompositionNavigate = compositionLocalOf<(Screen) -> Unit> { {} }

val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFD0BCFF),
    secondary = androidx.compose.ui.graphics.Color(0xFFCCC2DC),
    tertiary = androidx.compose.ui.graphics.Color(0xFFEFB8C8),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF000000),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF000000),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF000000),
    background = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    onBackground = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    surface = androidx.compose.ui.graphics.Color(0xFF2E2E2E),
    onSurface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
)

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Companion") {
        androidx.compose.material3.MaterialTheme(
            colorScheme = DarkColorScheme
        ) {
            var currentScreen by remember { mutableStateOf(Screen.Splash) }

            fun navigate(screen: Screen) {
                currentScreen = screen
            }

            CompositionLocalProvider(
                CompositionNavigate provides ::navigate,
            ) {
                when (currentScreen) {
                    Screen.Splash -> Splash()
                    Screen.Home -> Home()
                }
            }
        }
    }
}

