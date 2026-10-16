package com.unifind.app

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.unifind.app.ui.EsqueletoTarjeta
import com.unifind.app.ui.EstadoVacio
import com.unifind.app.ui.NumeroAnimado
import com.unifind.app.ui.TarjetaReporte
import com.unifind.app.ui.UF
import com.unifind.app.ui.alPresionar
import com.unifind.app.ui.estilo

// ==========================================================
// MIS REPORTES
// Publicaciones propias y reportes que ayudaste a atender.
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

    val mios = remember(reportes, miUid) {
        reportes.filter { it.usuarioId == miUid }
    }

    val atendidos = remember(reportes, miUid) {
        reportes.filter { it.usuarioId != miUid && it.contraparteId == miUid }
    }

    val perdidos = mios.count { it.esPerdido }
    val encontrados = mios.count { !it.esPerdido }
    val enProceso = mios.count { it.estado == EstadoReporte.EN_RECUPERACION }
    val recuperados = mios.count { it.estado == EstadoReporte.RECUPERADO }
    val totalEnProceso = (mios + atendidos).count {
        it.estado == EstadoReporte.EN_RECUPERACION
    }

    val visibles = remember(mios, atendidos, pestana) {
        when (pestana) {
            "Perdidos" -> mios.filter { it.esPerdido }
            "Encontrados" -> mios.filter { !it.esPerdido }
            "En proceso" -> (mios + atendidos).filter {
                it.estado == EstadoReporte.EN_RECUPERACION
            }
            else -> mios
        }.distinctBy { it.id }.sortedByDescending { it.fechaMillis }
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Encabezado sencillo, alineado como las pantallas Buscar y Publicar.
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, top = 54.dp, bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = "Mis reportes",
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF17231F)
                    )
                    Text(
                        text = "Da seguimiento a tus publicaciones",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = UF.TextoSec
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(42.dp)
                                .height(4.dp)
                                .background(Color(0xFF16845B), RoundedCornerShape(50))
                        )
                        Spacer(Modifier.width(7.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFFDCE7E1))
                        )
                    }
                }
            }

            item {
                Aparecer(0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Resumen(
                            modifier = Modifier.weight(1f),
                            valor = mios.size,
                            texto = "Total",
                            color = UF.VerdeOscuro
                        )
                        Resumen(
                            modifier = Modifier.weight(1f),
                            valor = enProceso,
                            texto = "En proceso",
                            color = EstadoReporte.EN_RECUPERACION.estilo().texto
                        )
                        Resumen(
                            modifier = Modifier.weight(1f),
                            valor = recuperados,
                            texto = "Recuperados",
                            color = EstadoReporte.RECUPERADO.estilo().texto
                        )
                    }
                }
            }

            item {
                Aparecer(1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .background(Color(0xFFE8F0EC), RoundedCornerShape(50))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            "Todas" to mios.size,
                            "Perdidos" to perdidos,
                            "Encontrados" to encontrados,
                            "En proceso" to totalEnProceso
                        ).forEach { (nombre, total) ->
                            val colorPestana = when (nombre) {
                                "Todas" -> Color(0xFF16845B)
                                "Perdidos" -> Color(0xFFE58A20)
                                "Encontrados" -> Color(0xFF22966F)
                                else -> Color(0xFFEA982C)
                            }
                            Pestana(
                                texto = nombre,
                                total = total,
                                seleccionada = pestana == nombre,
                                colorAcento = colorPestana,
                                modifier = Modifier.weight(1f),
                                onClick = { pestana = nombre }
                            )
                        }
                    }
                }
            }

            when {
                cargando -> items(3) {
                    Box(Modifier.padding(horizontal = 20.dp)) {
                        EsqueletoTarjeta()
                    }
                }

                error != null -> item {
                    Box(Modifier.padding(horizontal = 20.dp)) {
                        EstadoVacio(
                            emoji = "📡",
                            titulo = "Sin conexión",
                            texto = error.orEmpty(),
                            textoBoton = "Reintentar",
                            onBoton = { UniFindRepository.reintentar() }
                        )
                    }
                }

                visibles.isEmpty() -> item {
                    Box(Modifier.padding(horizontal = 20.dp)) {
                        EstadoVacio(
                            emoji = if (mios.isEmpty()) "📝" else "🗂️",
                            titulo = if (mios.isEmpty()) {
                                "Aún no has publicado nada"
                            } else {
                                "Nada en esta pestaña"
                            },
                            texto = if (mios.isEmpty()) {
                                "Cuando reportes un objeto perdido o encontrado, aparecerá aquí para que le des seguimiento."
                            } else {
                                "Cambia de pestaña para ver el resto de tus reportes."
                            },
                            textoBoton = if (mios.isEmpty()) "Publicar ahora" else null,
                            onBoton = { onNavegar("Publicar") }
                        )
                    }
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
                elevation = 4.dp,
                shape = forma,
                ambientColor = Color(0x18000000),
                spotColor = Color(0x18000000)
            )
            .background(Color.White, forma)
            .padding(horizontal = 4.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 7.dp)
                .width(30.dp)
                .height(4.dp)
                .background(color, RoundedCornerShape(50))
        )
        NumeroAnimado(valor = valor, color = color, tamano = 24.sp)
        Spacer(Modifier.height(3.dp))
        Text(
            text = texto,
            fontSize = 11.sp,
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
    colorAcento: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val fondo by animateColorAsState(
        targetValue = if (seleccionada) colorAcento else Color.Transparent,
        label = "fondoPestana"
    )

    Row(
        modifier = modifier
            .alPresionar(onClick)
            .then(
                if (seleccionada) Modifier.shadow(4.dp, RoundedCornerShape(50))
                else Modifier
            )
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 3.dp, vertical = 11.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = texto,
            fontSize = 11.sp,
            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium,
            color = if (seleccionada) Color.White else UF.TextoSec,
            maxLines = 1
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text = total.toString(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (seleccionada) Color.White else colorAcento,
            maxLines = 1
        )
    }
}
