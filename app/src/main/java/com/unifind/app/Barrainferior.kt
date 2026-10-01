package com.unifind.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// COLORES
// ==========================================================

private val Verde = Color(0xFF008F63)
private val VerdeOscuro = Color(0xFF006B4A)

private val FondoEncontrado = Color(0xFFE2F6EB)


// ==========================================================
// CONTENEDOR PRINCIPAL
//
// En MainActivity llama PantallaPrincipal().
// ==========================================================

@Composable
fun PantallaPrincipal(
    onCerrarSesion: () -> Unit = {}
) {

    var seccion by remember { mutableStateOf("Inicio") }

    val navegar: (String) -> Unit = { destino ->
        if (destino == "Inicio" ||
            destino == "Buscar" ||
            destino == "Publicar" ||
            destino == "Mis reportes" ||
            destino == "Perfil"
        ) {
            seccion = destino
        }
    }

    // Botón "atrás" del teléfono: regresa a Inicio
    BackHandler(enabled = seccion != "Inicio") {
        seccion = "Inicio"
    }

    when (seccion) {
        "Buscar" -> BuscarScreen(onNavegar = navegar)

        "Publicar" -> PublicarScreen(
            onNavegar = navegar,
            onTerminar = { seccion = "Inicio" }
        )

        "Mis reportes" -> MisReportesScreen(onNavegar = navegar)

        "Perfil" -> PerfilScreen(
            onNavegar = navegar,
            onCerrarSesion = {
                seccion = "Inicio"
                onCerrarSesion()
            }
        )

        else -> HomeScreen(onNavegar = navegar)
    }
}


// ==========================================================
// BARRA INFERIOR COMPARTIDA
// ==========================================================

@Composable
fun UniFindBottomBar(
    seleccionado: String,
    onSeleccionar: (String) -> Unit
) {

    val formaSuperior = RoundedCornerShape(
        topStart = 26.dp,
        topEnd = 26.dp
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = formaSuperior,
                ambientColor = Color(0x33000000),
                spotColor = Color(0x33000000)
            )
            .background(Color.White, formaSuperior)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        BottomNavigationItem(
            modifier = Modifier.weight(1f),
            icon = R.drawable.house_solid,
            text = "Inicio",
            selected = seleccionado == "Inicio",
            onClick = { onSeleccionar("Inicio") }
        )

        BottomNavigationItem(
            modifier = Modifier.weight(1f),
            icon = R.drawable.magnifying_glass_solid,
            text = "Buscar",
            selected = seleccionado == "Buscar",
            onClick = { onSeleccionar("Buscar") }
        )


        // ==================================================
        // BOTÓN PUBLICAR
        // ==================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onSeleccionar("Publicar") },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .shadow(8.dp, CircleShape, ambientColor = Verde, spotColor = Verde)
                    .background(Verde, CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(id = R.drawable.plus_solid),
                    contentDescription = "Publicar",
                    modifier = Modifier.size(26.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Publicar",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro,
                maxLines = 1
            )
        }

        BottomNavigationItem(
            modifier = Modifier.weight(1f),
            icon = R.drawable.file_solid,
            text = "Mis reportes",
            selected = seleccionado == "Mis reportes",
            onClick = { onSeleccionar("Mis reportes") }
        )

        BottomNavigationItem(
            modifier = Modifier.weight(1f),
            icon = R.drawable.user_solid,
            text = "Perfil",
            selected = seleccionado == "Perfil",
            onClick = { onSeleccionar("Perfil") }
        )
    }
}


// ==========================================================
// ITEM DE LA BARRA INFERIOR
// ==========================================================

@Composable
fun BottomNavigationItem(
    modifier: Modifier = Modifier,
    icon: Int,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val color = if (selected) VerdeOscuro else Color(0xFF7C8585)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // PÍLDORA DE FONDO CUANDO ESTÁ SELECCIONADO
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (selected) FondoEncontrado else Color.Transparent)
                .padding(horizontal = 18.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(color)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = color,
            maxLines = 1,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}