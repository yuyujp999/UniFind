package com.unifind.app

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unifind.app.data.EstadoReporte
import com.unifind.app.data.UniFindRepository
import com.unifind.app.ui.Aparecer
import com.unifind.app.ui.EncabezadoOla
import com.unifind.app.ui.EsqueletoTarjeta
import com.unifind.app.ui.EstadoVacio
import com.unifind.app.ui.NumeroAnimado
import com.unifind.app.ui.TarjetaReporte
import com.unifind.app.ui.UF
import com.unifind.app.ui.alPresionar
import com.unifind.app.ui.estilo

// ==========================================================
// MIS REPORTES
//
// Las publicaciones del usuario con su estado, tiempo y
// número de contactos. Al tocar una se abre el detalle,
// donde se puede cambiar el estado (F07).
// ==========================================================

@Composable
fun MisReportesScreen(
    onNavegar: (String) -> Unit = {},
    onAbrirReporte: (String) -> Unit = {}
) {

    val reportes by UniFindRepository.reportes.collectAsState()
    val cargando by UniFindRepository.cargando.collectAsState()
    val error by UniFindRepository.error.collectAsState()

    val miUid = UniFindRepository.miUid

    var pestana by rememberSaveable { mutableStateOf("Todas") }

    // Mis publicaciones + objetos que yo ayudé a recuperar / reclamé
    val mios = remember(reportes, miUid) {
        reportes.filter { it.usuarioId == miUid }
    }

    val atendidos = remember(reportes, miUid) {
        reportes.filter {
            it.usuarioId != miUid && it.contraparteId == miUid
        }
    }

    val perdidos = mios.count { it.esPerdido }
    val encontrados = mios.count { !it.esPerdido }
    val enProceso = mios.count { it.estado == EstadoReporte.EN_RECUPERACION }
    val recuperados = mios.count { it.estado == EstadoReporte.RECUPERADO }

    val visibles = remember(mios, atendidos, pestana) {
        when (pestana) {
            "Perdidos" -> mios.filter { it.esPerdido }
            "Encontrados" -> mios.filter { !it.esPerdido }
            "En proceso" -> (mios + atendidos).filter {
                it.estado == EstadoReporte.EN_RECUPERACION
            }
            else -> mios
        }.sortedByDescending { it.fechaMillis }
    }

    Scaffold(
        containerColor = Color(0xFFF7FAF9),
        bottomBar = {
            UniFindBottomBar(
                seleccionado = "Mis reportes",
                onSeleccionar = onNavegar
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                EncabezadoOla(
                    titulo = "Mis publicaciones",
                    subtitulo = "Da seguimiento a tus reportes"
                )
            }

            // ---------- RESUMEN ----------
            item {
                Aparecer(0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Resumen(Modifier.weight(1f), mios.size, "Total", UF.VerdeOscuro)
                        Resumen(
                            Modifier.weight(1f), enProceso, "En proceso",
                            EstadoReporte.EN_RECUPERACION.estilo().texto
                        )
                        Resumen(
                            Modifier.weight(1f), recuperados, "Recuperados",
                            EstadoReporte.RECUPERADO.estilo().texto
                        )
                    }
                }
            }

            // ---------- PESTAÑAS ----------
            item {
                Aparecer(1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .background(UF.Gris, RoundedCornerShape(50))
                            .padding(4.dp)
                    ) {
                        listOf(
                            "Todas" to mios.size,
                            "Perdidos" to perdidos,
                            "Encontrados" to encontrados,
                            "En proceso" to (mios + atendidos).count {
                                it.estado == EstadoReporte.EN_RECUPERACION
                            }
                        ).forEach { (nombre, total) ->
                            Pestana(
                                texto = nombre,
                                total = total,
                                seleccionada = pestana == nombre,
                                modifier = Modifier.weight(1f),
                                onClick = { pestana = nombre }
                            )
                        }
                    }
                }
            }

            // ---------- LISTA ----------
            when {

                cargando -> items(3) {
                    Box(Modifier.padding(horizontal = 20.dp)) { EsqueletoTarjeta() }
                }

                error != null -> item {
                    EstadoVacio(
                        emoji = "📡",
                        titulo = "Sin conexión",
                        texto = error.orEmpty(),
                        textoBoton = "Reintentar",
                        onBoton = { UniFindRepository.reintentar() }
                    )
                }

                visibles.isEmpty() -> item {
                    EstadoVacio(
                        emoji = if (mios.isEmpty()) "📝" else "🗂️",
                        titulo = if (mios.isEmpty()) "Aún no has publicado nada"
                        else "Nada en esta pestaña",
                        texto = if (mios.isEmpty())
                            "Cuando reportes un objeto perdido o encontrado, " +
                                "aparecerá aquí para que le des seguimiento."
                        else
                            "Cambia de pestaña para ver el resto de tus reportes.",
                        textoBoton = if (mios.isEmpty()) "Publicar ahora" else null,
                        onBoton = { onNavegar("Publicar") }
                    )
                }

                else -> items(visibles, key = { it.id }) { reporte ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .animateItem()
                    ) {
                        TarjetaReporte(
                            reporte = reporte,
                            mostrarContactos = true,
                            onClick = { onAbrirReporte(reporte.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Resumen(
    modifier: Modifier,
    valor: Int,
    texto: String,
    color: Color
) {

    val forma = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .shadow(
                elevation = 3.dp,
                shape = forma,
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .background(Color.White, forma)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        NumeroAnimado(valor = valor, color = color, tamano = 24.sp)

        Spacer(Modifier.height(2.dp))

        Text(
            text = texto,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = UF.TextoSec,
            maxLines = 1
        )
    }
}

@Composable
private fun Pestana(
    texto: String,
    total: Int,
    seleccionada: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {

    val fondo by androidx.compose.animation.animateColorAsState(
        targetValue = if (seleccionada) Color.White else Color.Transparent,
        label = "fondoPestana"
    )

    Row(
        modifier = modifier
            .alPresionar(onClick)
            .then(
                if (seleccionada) Modifier.shadow(2.dp, RoundedCornerShape(50))
                else Modifier
            )
            .background(fondo, RoundedCornerShape(50))
            .padding(vertical = 9.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = texto,
            fontSize = 12.sp,
            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium,
            color = if (seleccionada) UF.VerdeOscuro else UF.TextoSec,
            maxLines = 1
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = total.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (seleccionada) UF.Verde else UF.TextoSec
        )
    }
}
