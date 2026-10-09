package com.unifind.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// UniFind usa siempre su identidad verde (sin colores dinámicos del sistema
// y sin modo oscuro, porque todas las pantallas están diseñadas sobre fondos claros).
private val EsquemaUniFind = lightColorScheme(
    primary = VerdeUniFind,
    onPrimary = Color.White,
    primaryContainer = MentaUniFind,
    onPrimaryContainer = VerdeOscuroUniFind,
    secondary = VerdeOscuroUniFind,
    onSecondary = Color.White,
    tertiary = VerdeProfundoUniFind,
    background = Color.White,
    onBackground = TextoUniFind,
    surface = Color.White,
    onSurface = TextoUniFind,
    onSurfaceVariant = TextoSecundarioUniFind,
    error = RojoUniFind
)

@Composable
fun UniFindTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = false,
    @Suppress("UNUSED_PARAMETER") dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaUniFind,
        typography = Typography,
        content = content
    )
}
