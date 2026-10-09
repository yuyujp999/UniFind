package com.unifind.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.saveable.rememberSaveable
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

    var seccion by rememberSaveable { mutableStateOf("Inicio") }

    // Id del reporte abierto (pantalla de detalle). null = ninguno.
    var detalleId by rememberSaveable { mutableStateOf<String?>(null) }

    val navegar: (String) -> Unit = { destino ->
        if (destino == "Inicio" ||
            destino == "Buscar" ||
            destino == "Publicar" ||
            destino == "Mis reportes" ||
            destino == "Perfil"
        ) {
            detalleId = null
            seccion = destino
        }
    }

    val abrirReporte: (String) -> Unit = { id -> detalleId = id }

    // Botón "atrás" del teléfono:
    // 1) cierra el detalle  2) regresa a Inicio
    BackHandler(enabled = detalleId != null) {
        detalleId = null
    }

    BackHandler(enabled = detalleId == null && seccion != "Inicio") {
        seccion = "Inicio"
    }

    val destinoActual = detalleId?.let { "detalle:$it" } ?: seccion

    AnimatedContent(
        targetState = destinoActual,
        transitionSpec = {

            val entraDetalle = targetState.startsWith("detalle:")
            val saleDetalle = initialState.startsWith("detalle:")

            when {

                entraDetalle ->
                    (slideInHorizontally(tween(320)) { it } + fadeIn(tween(220)))
                        .togetherWith(
                            slideOutHorizontally(tween(320)) { -it / 4 } +
                                fadeOut(tween(200))
                        )

                saleDetalle ->
                    (slideInHorizontally(tween(320)) { -it / 4 } + fadeIn(tween(220)))
                        .togetherWith(
                            slideOutHorizontally(tween(320)) { it } +
                                fadeOut(tween(200))
                        )

                else ->
                    fadeIn(tween(220)).togetherWith(fadeOut(tween(160)))
            }
        },
        label = "navegacion"
    ) { destino ->

        if (destino.startsWith("detalle:")) {

            DetalleScreen(
                reporteId = destino.removePrefix("detalle:"),
                onBack = { detalleId = null }
            )

        } else {

            when (destino) {

                "Buscar" -> BuscarScreen(
                    onNavegar = navegar,
                    onAbrirReporte = abrirReporte
                )

                "Publicar" -> PublicarScreen(
                    onNavegar = navegar,
                    onTerminar = { seccion = "Mis reportes" }
                )

                "Mis reportes" -> MisReportesScreen(
                    onNavegar = navegar,
                    onAbrirReporte = abrirReporte
                )

                "Perfil" -> PerfilScreen(
                    onNavegar = navegar,
                    onCerrarSesion = {
                        seccion = "Inicio"
                        onCerrarSesion()
                    }
                )

                else -> HomeScreen(
                    onNavegar = navegar,
                    onAbrirReporte = abrirReporte
                )
            }
        }
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