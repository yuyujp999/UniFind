package com.unifind.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// COLORES UNIFIND
// ==========================================================

private val Verde = Color(0xFF008F63)
private val VerdeOscuro = Color(0xFF006B4A)
private val Rojo = Color(0xFFE53935)

private val FondoPerdido = Color(0xFFFFE9E9)
private val FondoEncontrado = Color(0xFFE2F6EB)
private val FondoGris = Color(0xFFF3F5F5)

private val TextoPrincipal = Color(0xFF202124)
private val TextoSecundario = Color(0xFF5F6368)


// ==========================================================
// MODELO
// ==========================================================

private data class MiReporte(
    val titulo: String,
    val estado: String,        // Perdido | Encontrado | En recuperación | Recuperado
    val esPerdido: Boolean,    // true = reporté un objeto perdido, false = encontrado
    val tiempo: String,
    val contactos: Int,
    val emoji: String,         // se muestra mientras no haya foto
    val imagen: Int? = null    // R.drawable.foto_xxx cuando tengas fotos
)


// ==========================================================
// PANTALLA MIS REPORTES
// ==========================================================

@Composable
fun MisReportesScreen(
    onNavegar: (String) -> Unit = {}
) {

    // ------------------------------------------------------
    // DATOS DE EJEMPLO
    //
    // Para poner fotos, guarda tus imágenes en res/drawable
    // y agrega al final, por ejemplo:
    // MiReporte("Mochila negra", ..., "🎒", R.drawable.foto_mochila)
    // ------------------------------------------------------

    val reportes = listOf(
        MiReporte("Mochila negra", "Perdido", true, "Hace 3 días", 2, "🎒"),
        MiReporte("Llaves con llavero azul", "Encontrado", false, "Hace 1 semana", 1, "🔑"),
        MiReporte("Celular Samsung", "En recuperación", true, "Hace 2 semanas", 0, "📱"),
        MiReporte("Tarjeta universitaria", "Recuperado", false, "Hace 1 mes", 1, "💳")
    )


    // ------------------------------------------------------
    // PESTAÑA SELECCIONADA
    // ------------------------------------------------------

    var filtro by remember { mutableStateOf("Todas") }

    val visibles = when (filtro) {
        "Perdidos" -> reportes.filter { it.esPerdido }
        "Encontrados" -> reportes.filter { !it.esPerdido }
        else -> reportes
    }


    // ======================================================
    // SCAFFOLD
    // ======================================================

    Scaffold(

        containerColor = Color.White,

        bottomBar = {
            UniFindBottomBar(
                seleccionado = "Mis reportes",
                onSeleccionar = onNavegar
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .padding(
                    top = 16.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 24.dp
                )
        ) {

            // ==================================================
            // TÍTULO
            // ==================================================

            Text(
                text = "Mis publicaciones",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))


            // ==================================================
            // PESTAÑAS
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                listOf("Todas", "Perdidos", "Encontrados").forEach { opcion ->

                    PestanaReportes(
                        modifier = Modifier.weight(1f),
                        text = opcion,
                        selected = filtro == opcion,
                        onClick = { filtro = opcion }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            // ==================================================
            // LISTA
            // ==================================================

            if (visibles.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(text = "📭", fontSize = 44.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "No tienes reportes aquí",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )

                    Text(
                        text = "Cuando publiques un objeto aparecerá en esta lista",
                        fontSize = 14.sp,
                        color = TextoSecundario,
                        textAlign = TextAlign.Center
                    )
                }

            } else {

                visibles.forEach { reporte ->

                    ReporteCard(reporte = reporte)

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}


// ==========================================================
// PESTAÑA (TODAS / PERDIDOS / ENCONTRADOS)
// ==========================================================

@Composable
private fun PestanaReportes(
    modifier: Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) VerdeOscuro else FondoGris)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else TextoPrincipal,
            maxLines = 1
        )
    }
}


// ==========================================================
// TARJETA DE REPORTE
// ==========================================================

@Composable
private fun ReporteCard(
    reporte: MiReporte
) {

    val forma = RoundedCornerShape(20.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = forma,
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .background(Color.White, forma)
            .clip(forma)
            .clickable {
                // Después abriremos el detalle del reporte y sus contactos
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ==================================================
        // IMAGEN
        // ==================================================

        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFEDEDED)),
            contentAlignment = Alignment.Center
        ) {

            if (reporte.imagen != null) {

                Image(
                    painter = painterResource(id = reporte.imagen),
                    contentDescription = reporte.titulo,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

            } else {

                Text(text = reporte.emoji, fontSize = 40.sp)
            }
        }

        Spacer(modifier = Modifier.width(14.dp))


        // ==================================================
        // INFORMACIÓN
        // ==================================================

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = reporte.titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(7.dp))

            EstadoReporteChip(estado = reporte.estado)

            Spacer(modifier = Modifier.height(9.dp))

            // TIEMPO
            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(id = R.drawable.clock_solid),
                    contentDescription = "Tiempo",
                    modifier = Modifier.size(14.dp),
                    colorFilter = ColorFilter.tint(Verde)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = reporte.tiempo,
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // CONTACTOS
            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(id = R.drawable.user_solid),
                    contentDescription = "Contactos",
                    modifier = Modifier.size(14.dp),
                    colorFilter = ColorFilter.tint(Verde)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = if (reporte.contactos == 1) "1 contacto" else "${reporte.contactos} contactos",
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // FLECHA
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Ver detalle",
            tint = TextoSecundario,
            modifier = Modifier.size(26.dp)
        )
    }
}


// ==========================================================
// ETIQUETA DE ESTADO
// ==========================================================

@Composable
private fun EstadoReporteChip(estado: String) {

    val (fondo, color) = when (estado) {
        "Perdido" -> FondoPerdido to Rojo
        "Encontrado" -> FondoEncontrado to VerdeOscuro
        "En recuperación" -> Color(0xFFE3ECFF) to Color(0xFF2F5BD3)
        else -> Color(0xFFD3F0E0) to Color(0xFF0A7A4F) // Recuperado
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(fondo)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = estado,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}