package com.unifind.app

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unifind.app.data.Catalogo
import com.unifind.app.data.ContactoReporte
import com.unifind.app.data.EstadoReporte
import com.unifind.app.data.EventoHistorial
import com.unifind.app.data.Reporte
import com.unifind.app.data.UniFindRepository
import com.unifind.app.ui.Aparecer
import com.unifind.app.ui.BotonPrimario
import com.unifind.app.ui.BotonSecundario
import com.unifind.app.ui.ChipEstado
import com.unifind.app.ui.Esqueleto
import com.unifind.app.ui.EstadoVacio
import com.unifind.app.ui.FotoReporte
import com.unifind.app.ui.UF
import com.unifind.app.ui.abrirWhatsApp
import com.unifind.app.ui.alPresionar
import com.unifind.app.ui.formatearFechaHora
import com.unifind.app.ui.llamar
import com.unifind.app.ui.tiempoRelativo

// ==========================================================
// DETALLE DEL OBJETO
//
// F06 - Consultar detalles del objeto
// F07 - Seguimiento del estado (línea de progreso e historial)
// F08 - Contacto entre usuarios (WhatsApp / llamada)
// ==========================================================

@Composable
fun DetalleScreen(
    reporteId: String,
    onBack: () -> Unit
) {

    val contexto = LocalContext.current

    val reportes by UniFindRepository.reportes.collectAsState()
    val cargando by UniFindRepository.cargando.collectAsState()
    val nombres by UniFindRepository.nombres.collectAsState()

    val reporte = reportes.firstOrNull { it.id == reporteId }

    val miUid = UniFindRepository.miUid

    val esDueno = reporte != null && reporte.usuarioId == miUid
    val esContraparte = reporte != null && reporte.contraparteId == miUid

    var contactos by remember { mutableStateOf<List<ContactoReporte>>(emptyList()) }
    var historial by remember { mutableStateOf<List<EventoHistorial>>(emptyList()) }

    var trabajando by remember { mutableStateOf(false) }
    var dialogoAtender by remember { mutableStateOf(false) }
    var dialogoEliminar by remember { mutableStateOf(false) }

    // ------------------------------------------------------
    // ESCUCHAR HISTORIAL (todos) Y CONTACTOS (solo el dueño)
    // ------------------------------------------------------

    DisposableEffect(reporteId, esDueno) {

        val l1 = UniFindRepository.escucharHistorial(reporteId) {
            historial = it
        }

        val l2 = if (esDueno) {
            UniFindRepository.escucharContactos(reporteId) { contactos = it }
        } else {
            null
        }

        onDispose {
            l1.remove()
            l2?.remove()
        }
    }

    // Nombres de las personas involucradas
    LaunchedEffect(reporte?.usuarioId, contactos, historial) {
        reporte?.usuarioId?.let { UniFindRepository.pedirNombre(it) }
        contactos.forEach { UniFindRepository.pedirNombre(it.usuarioId) }
        historial.forEach { UniFindRepository.pedirNombre(it.usuarioId) }
    }

    fun nombreDe(uid: String): String =
        when {
            uid == miUid -> "Tú"
            else -> nombres[uid]?.takeIf { it.isNotBlank() } ?: "Usuario UniFind"
        }

    fun cambiar(nuevo: EstadoReporte, mensajeOk: String) {

        val r = reporte ?: return

        trabajando = true

        UniFindRepository.cambiarEstado(
            reporte = r,
            nuevo = nuevo,
            onExito = {
                trabajando = false
                Toast.makeText(contexto, mensajeOk, Toast.LENGTH_SHORT).show()
            },
            onError = {
                trabajando = false
                Toast.makeText(contexto, it, Toast.LENGTH_LONG).show()
            }
        )
    }

    // ======================================================
    // CONTENIDO
    // ======================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF9))
    ) {

        when {

            reporte == null && cargando -> CargandoDetalle()

            reporte == null -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    verticalArrangement = Arrangement.Center
                ) {
                    EstadoVacio(
                        emoji = "🔎",
                        titulo = "Este reporte ya no está disponible",
                        texto = "Pudo haber sido eliminado por quien lo publicó.",
                        textoBoton = "Volver",
                        onBoton = onBack
                    )
                }
            }

            else -> {

                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {

                        Galeria(reporte)

                        Column(
                            modifier = Modifier.padding(
                                horizontal = 20.dp,
                                vertical = 18.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {

                            Aparecer(0) { EncabezadoDetalle(reporte) }

                            Aparecer(1) {
                                DatosDetalle(
                                    reporte = reporte,
                                    publicadoPor =
                                        if (esDueno) "Tú" else nombreDe(reporte.usuarioId)
                                )
                            }

                            Aparecer(2) { DescripcionDetalle(reporte) }

                            Aparecer(3) { SeguimientoEstado(reporte) }

                            if (esDueno) {
                                Aparecer(4) {
                                    SeccionContactos(
                                        contactos = contactos,
                                        nombreDe = ::nombreDe,
                                        titulo = reporte.titulo,
                                        contexto = contexto
                                    )
                                }
                            }

                            if (historial.isNotEmpty()) {
                                Aparecer(5) {
                                    SeccionHistorial(historial, ::nombreDe)
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    BarraAcciones(
                        reporte = reporte,
                        esDueno = esDueno,
                        esContraparte = esContraparte,
                        trabajando = trabajando,
                        onAtender = { dialogoAtender = true },
                        onWhatsApp = {
                            val texto =
                                "Hola, vi tu reporte «${reporte.titulo}» en UniFind."
                            UniFindRepository.registrarContacto(reporte, texto)
                            abrirWhatsApp(contexto, reporte.whatsapp, texto)
                        },
                        onLlamar = { llamar(contexto, reporte.whatsapp) },
                        onRecuperado = {
                            cambiar(
                                EstadoReporte.RECUPERADO,
                                "¡Objeto recuperado! 🎉"
                            )
                        },
                        onReabrir = {
                            cambiar(
                                if (reporte.esPerdido) EstadoReporte.PERDIDO
                                else EstadoReporte.ENCONTRADO,
                                "Reporte reabierto"
                            )
                        },
                        onEliminar = { dialogoEliminar = true }
                    )
                }
            }
        }

        // BOTÓN ATRÁS (siempre visible, flota sobre la foto)
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 10.dp)
                .size(44.dp)
                .alPresionar(onBack)
                .shadow(6.dp, CircleShape)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "←",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = UF.VerdeOscuro
            )
        }
    }

    // ======================================================
    // DIÁLOGO: "LO ENCONTRÉ" / "ES MÍO"
    // ======================================================

    if (dialogoAtender && reporte != null) {

        DialogoAtender(
            reporte = reporte,
            contexto = contexto,
            onCancelar = { dialogoAtender = false },
            onConfirmar = { mensaje, telefono ->

                dialogoAtender = false
                trabajando = true

                UniFindRepository.atenderReporte(
                    reporte = reporte,
                    mensaje = mensaje,
                    telefono = telefono,
                    onExito = {

                        trabajando = false

                        contexto
                            .getSharedPreferences("unifind", Context.MODE_PRIVATE)
                            .edit()
                            .putString("whatsapp", telefono)
                            .apply()

                        UniFindRepository.guardarWhatsapp(telefono)

                        Toast.makeText(
                            contexto,
                            "Listo, avisamos a quien publicó el reporte",
                            Toast.LENGTH_LONG
                        ).show()

                        if (reporte.whatsapp.length == 10) {
                            abrirWhatsApp(contexto, reporte.whatsapp, mensaje)
                        }
                    },
                    onError = {
                        trabajando = false
                        Toast.makeText(contexto, it, Toast.LENGTH_LONG).show()
                    }
                )
            }
        )
    }

    // ======================================================
    // DIÁLOGO: ELIMINAR
    // ======================================================

    if (dialogoEliminar && reporte != null) {

        AlertDialog(
            onDismissRequest = { dialogoEliminar = false },
            containerColor = Color.White,
            title = { Text("¿Eliminar este reporte?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Se borrará «${reporte.titulo}» y ya no aparecerá en la app. " +
                        "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(onClick = {

                    dialogoEliminar = false

                    UniFindRepository.eliminarReporte(
                        reporteId = reporte.id,
                        onExito = {
                            Toast.makeText(
                                contexto,
                                "Reporte eliminado",
                                Toast.LENGTH_SHORT
                            ).show()
                            onBack()
                        },
                        onError = {
                            Toast.makeText(contexto, it, Toast.LENGTH_LONG).show()
                        }
                    )
                }) {
                    Text("Eliminar", color = UF.Rojo, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogoEliminar = false }) {
                    Text("Cancelar", color = UF.TextoSec)
                }
            }
        )
    }
}

// ==========================================================
// GALERÍA DE FOTOS
// ==========================================================

@Composable
private fun Galeria(reporte: Reporte) {

    val forma = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)

    val totalFotos = reporte.fotos.size.coerceAtLeast(1)

    val pager = rememberPagerState(pageCount = { totalFotos })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(forma)
    ) {

        HorizontalPager(
            state = pager,
            modifier = Modifier.fillMaxSize()
        ) { pagina ->

            FotoReporte(
                base64 = reporte.fotos.getOrNull(pagina),
                categoria = reporte.categoria,
                modifier = Modifier.fillMaxSize(),
                ladoMax = 900,
                tamanoEmoji = 96.sp
            )
        }

        // Degradado para que se lea el botón atrás
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x55000000), Color.Transparent)
                    )
                )
        )

        if (totalFotos > 1) {

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .background(Color(0x66000000), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                repeat(totalFotos) { i ->

                    val activo = pager.currentPage == i

                    val ancho by animateFloatAsState(
                        targetValue = if (activo) 18f else 7f,
                        animationSpec = tween(250),
                        label = "punto"
                    )

                    Box(
                        modifier = Modifier
                            .height(7.dp)
                            .width(ancho.dp)
                            .background(
                                if (activo) Color.White else Color(0x99FFFFFF),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

// ==========================================================
// ENCABEZADO: chips + título
// ==========================================================

@Composable
private fun EncabezadoDetalle(reporte: Reporte) {

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            ChipEstado(reporte.estado, largo = true)

            Text(
                text = reporte.tipoTexto,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = UF.TextoSec,
                modifier = Modifier
                    .background(UF.Gris, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }

        Text(
            text = reporte.titulo,
            fontSize = 26.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            color = UF.Texto
        )
    }
}

// ==========================================================
// DATOS: ubicación, fecha, categoría, autor
// ==========================================================

@Composable
private fun DatosDetalle(reporte: Reporte, publicadoPor: String) {

    val forma = RoundedCornerShape(20.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = forma,
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .background(Color.White, forma)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        FilaDato(
            icono = R.drawable.location_dot_solid,
            etiqueta = "Ubicación aproximada",
            valor = reporte.ubicacion
        )

        FilaDato(
            icono = R.drawable.clock_solid,
            etiqueta = if (reporte.esPerdido) "Fecha en que se reportó" else "Fecha del hallazgo",
            valor = "${formatearFechaHora(reporte.fechaMillis)} · ${tiempoRelativo(reporte.fechaMillis)}"
        )

        FilaDato(
            emoji = Catalogo.emoji(reporte.categoria),
            etiqueta = "Categoría",
            valor = reporte.categoria.ifBlank { "Otros" }
        )

        FilaDato(
            icono = R.drawable.user_solid,
            etiqueta = "Publicado por",
            valor = publicadoPor
        )
    }
}

@Composable
private fun FilaDato(
    etiqueta: String,
    valor: String,
    icono: Int? = null,
    emoji: String? = null
) {

    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(UF.Menta, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {

            if (icono != null) {
                Image(
                    painter = painterResource(id = icono),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(UF.VerdeOscuro)
                )
            } else {
                Text(text = emoji.orEmpty(), fontSize = 18.sp)
            }
        }

        Spacer(Modifier.width(12.dp))

        Column {

            Text(text = etiqueta, fontSize = 12.sp, color = UF.TextoSec)

            Text(
                text = valor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = UF.Texto
            )
        }
    }
}

@Composable
private fun DescripcionDetalle(reporte: Reporte) {

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Text(
            text = "Descripción",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = UF.Texto
        )

        Text(
            text = reporte.descripcion.ifBlank {
                "Quien publicó este reporte no agregó una descripción."
            },
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = if (reporte.descripcion.isBlank()) UF.TextoSec else UF.Texto
        )
    }
}

// ==========================================================
// F07 - LÍNEA DE SEGUIMIENTO
// ==========================================================

@Composable
private fun SeguimientoEstado(reporte: Reporte) {

    val paso = reporte.estado.orden

    val etiquetas = listOf(
        if (reporte.esPerdido) "Perdido" else "Encontrado",
        "En proceso",
        "Recuperado"
    )

    val forma = RoundedCornerShape(20.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = forma,
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .background(Color.White, forma)
            .padding(horizontal = 12.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Seguimiento",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = UF.Texto,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {

            etiquetas.forEachIndexed { i, etiqueta ->

                PasoSeguimiento(
                    etiqueta = etiqueta,
                    alcanzado = i <= paso,
                    actual = i == paso && reporte.estado != EstadoReporte.RECUPERADO
                )

                if (i < etiquetas.lastIndex) {

                    TramoSeguimiento(
                        lleno = i < paso,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Text(
            text = when (reporte.estado) {
                EstadoReporte.PERDIDO ->
                    "Se está buscando este objeto. Si lo viste, avísale a su dueño."
                EstadoReporte.ENCONTRADO ->
                    "Alguien encontró este objeto y espera a su dueño."
                EstadoReporte.EN_RECUPERACION ->
                    "Ya hay contacto entre las personas. Falta confirmar la entrega."
                EstadoReporte.RECUPERADO ->
                    "¡El objeto volvió a su dueño!"
            },
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = UF.TextoSec,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun PasoSeguimiento(
    etiqueta: String,
    alcanzado: Boolean,
    actual: Boolean
) {

    val pulso = rememberInfiniteTransition(label = "pulso")

    val escalaHalo by pulso.animateFloat(
        initialValue = 1f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo"
    )

    val escalaPaso by animateFloatAsState(
        targetValue = if (alcanzado) 1f else 0.85f,
        animationSpec = tween(400),
        label = "paso"
    )

    Column(
        modifier = Modifier.width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {

            if (actual) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .scale(escalaHalo)
                        .background(UF.Verde.copy(alpha = 0.18f), CircleShape)
                )
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .scale(escalaPaso)
                    .background(
                        if (alcanzado) UF.Verde else UF.Linea,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (alcanzado) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = etiqueta,
            fontSize = 12.sp,
            fontWeight = if (alcanzado) FontWeight.Bold else FontWeight.Normal,
            color = if (alcanzado) UF.VerdeOscuro else UF.TextoSec,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun TramoSeguimiento(lleno: Boolean, modifier: Modifier) {

    val progreso by animateFloatAsState(
        targetValue = if (lleno) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "tramo"
    )

    Box(
        modifier = modifier
            .padding(top = 18.dp)
            .height(4.dp)
            .background(UF.Linea, RoundedCornerShape(2.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progreso)
                .background(UF.Verde, RoundedCornerShape(2.dp))
        )
    }
}

// ==========================================================
// F08 - CONTACTOS RECIBIDOS (solo el dueño del reporte)
// ==========================================================

@Composable
private fun SeccionContactos(
    contactos: List<ContactoReporte>,
    nombreDe: (String) -> String,
    titulo: String,
    contexto: Context
) {

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Text(
            text = "Contactos recibidos (${contactos.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = UF.Texto
        )

        if (contactos.isEmpty()) {

            Text(
                text = "Todavía nadie se ha puesto en contacto. " +
                    "Cuando alguien lo haga, lo verás aquí.",
                fontSize = 14.sp,
                color = UF.TextoSec,
                lineHeight = 20.sp
            )

        } else {

            contactos.forEach { c ->

                val forma = RoundedCornerShape(18.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, forma)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            text = nombreDe(c.usuarioId),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = UF.Texto,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = tiempoRelativo(c.fechaMillis),
                            fontSize = 12.sp,
                            color = UF.TextoSec
                        )
                    }

                    Text(
                        text = when (c.tipo) {
                            "hallazgo" -> "Dice que encontró tu objeto"
                            "reclamo" -> "Dice que este objeto es suyo"
                            else -> "Te escribió por este reporte"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = UF.VerdeOscuro
                    )

                    if (c.mensaje.isNotBlank()) {
                        Text(
                            text = c.mensaje,
                            fontSize = 14.sp,
                            color = UF.TextoSec,
                            lineHeight = 20.sp
                        )
                    }

                    if (c.whatsapp.length == 10) {

                        Spacer(Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                            BotonPequeno(
                                texto = "WhatsApp",
                                modifier = Modifier.weight(1f)
                            ) {
                                abrirWhatsApp(
                                    contexto,
                                    c.whatsapp,
                                    "Hola, te escribo por el reporte «$titulo» en UniFind."
                                )
                            }

                            BotonPequeno(
                                texto = "Llamar",
                                modifier = Modifier.weight(1f)
                            ) {
                                llamar(contexto, c.whatsapp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BotonPequeno(
    texto: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .alPresionar(onClick)
            .background(UF.Menta, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = UF.VerdeOscuro
        )
    }
}

// ==========================================================
// HISTORIAL DE CAMBIOS
// ==========================================================

@Composable
private fun SeccionHistorial(
    historial: List<EventoHistorial>,
    nombreDe: (String) -> String
) {

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Text(
            text = "Actividad",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = UF.Texto
        )

        historial.take(8).forEach { e ->

            Row(verticalAlignment = Alignment.Top) {

                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(8.dp)
                        .background(UF.Verde, CircleShape)
                )

                Spacer(Modifier.width(12.dp))

                Column {

                    Text(
                        text = "${e.estadoAnterior} → ${e.estadoNuevo}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = UF.Texto
                    )

                    Text(
                        text = "${nombreDe(e.usuarioId)} · ${tiempoRelativo(e.fechaMillis)}",
                        fontSize = 12.sp,
                        color = UF.TextoSec
                    )
                }
            }
        }
    }
}

// ==========================================================
// BARRA DE ACCIONES (abajo)
// ==========================================================

@Composable
private fun BarraAcciones(
    reporte: Reporte,
    esDueno: Boolean,
    esContraparte: Boolean,
    trabajando: Boolean,
    onAtender: () -> Unit,
    onWhatsApp: () -> Unit,
    onLlamar: () -> Unit,
    onRecuperado: () -> Unit,
    onReabrir: () -> Unit,
    onEliminar: () -> Unit
) {

    val tieneTelefono = reporte.whatsapp.length == 10
    val estado = reporte.estado

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
            )
            .background(
                Color.White,
                RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        when {

            // ---------- DUEÑO ----------
            esDueno && estado == EstadoReporte.RECUPERADO -> {
                Aviso("🎉 ¡Objeto recuperado! Gracias por usar UniFind.")
                BotonSecundario(
                    texto = "Reabrir reporte",
                    onClick = onReabrir,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            esDueno && estado == EstadoReporte.EN_RECUPERACION -> {
                BotonPrimario(
                    texto = "Ya lo recuperé / entregué",
                    onClick = onRecuperado,
                    cargando = trabajando,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BotonSecundario(
                        texto = "Reabrir",
                        onClick = onReabrir,
                        modifier = Modifier.weight(1f)
                    )
                    BotonSecundario(
                        texto = "Eliminar",
                        onClick = onEliminar,
                        color = UF.Rojo,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            esDueno -> {
                BotonPrimario(
                    texto = if (reporte.esPerdido) "Ya recuperé mi objeto"
                    else "Ya entregué el objeto",
                    onClick = onRecuperado,
                    cargando = trabajando,
                    modifier = Modifier.fillMaxWidth()
                )
                BotonSecundario(
                    texto = "Eliminar reporte",
                    onClick = onEliminar,
                    color = UF.Rojo,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ---------- OTRA PERSONA ----------
            estado == EstadoReporte.RECUPERADO ->
                Aviso("✅ Este objeto ya fue recuperado.")

            estado == EstadoReporte.EN_RECUPERACION && esContraparte -> {

                if (tieneTelefono) {
                    BotonPrimario(
                        texto = "Escribir por WhatsApp",
                        onClick = onWhatsApp,
                        color = Color(0xFF1FA855),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                BotonSecundario(
                    texto = "Ya lo entregué / recibí",
                    onClick = onRecuperado,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            estado == EstadoReporte.EN_RECUPERACION ->
                Aviso("⏳ Alguien ya está atendiendo este reporte.")

            else -> {

                BotonPrimario(
                    texto = if (reporte.esPerdido) "¡Yo lo encontré!" else "¡Es mío!",
                    onClick = onAtender,
                    cargando = trabajando,
                    modifier = Modifier.fillMaxWidth()
                )

                if (tieneTelefono) {

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                        BotonSecundario(
                            texto = "WhatsApp",
                            onClick = onWhatsApp,
                            modifier = Modifier.weight(1f)
                        )

                        BotonSecundario(
                            texto = "Llamar",
                            onClick = onLlamar,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Aviso(texto: String) {

    Text(
        text = texto,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = UF.VerdeOscuro,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(UF.Menta, RoundedCornerShape(16.dp))
            .padding(14.dp)
    )
}

// ==========================================================
// DIÁLOGO PARA "LO ENCONTRÉ" / "ES MÍO"
// ==========================================================

@Composable
private fun DialogoAtender(
    reporte: Reporte,
    contexto: Context,
    onCancelar: () -> Unit,
    onConfirmar: (mensaje: String, telefono: String) -> Unit
) {

    var telefono by remember {
        mutableStateOf(
            contexto
                .getSharedPreferences("unifind", Context.MODE_PRIVATE)
                .getString("whatsapp", "")
                .orEmpty()
        )
    }

    var mensaje by remember {
        mutableStateOf(
            if (reporte.esPerdido)
                "Hola, creo que encontré tu «${reporte.titulo}»."
            else
                "Hola, creo que «${reporte.titulo}» es mío."
        )
    }

    val telefonoOk = telefono.length == 10

    val colores = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedBorderColor = UF.Verde,
        unfocusedBorderColor = UF.Linea,
        cursorColor = UF.Verde,
        focusedLabelColor = UF.VerdeOscuro
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = Color.White,
        title = {
            Text(
                text = if (reporte.esPerdido) "¿Encontraste este objeto?"
                else "¿Este objeto es tuyo?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Text(
                    text = "Dejaremos tu número para que la otra persona " +
                        "pueda contactarte por WhatsApp o llamada.",
                    fontSize = 14.sp,
                    color = UF.TextoSec
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = {
                        telefono = it.filter { c -> c.isDigit() }.take(10)
                    },
                    label = { Text("Tu WhatsApp (10 dígitos)") },
                    singleLine = true,
                    isError = telefono.isNotEmpty() && !telefonoOk,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = colores,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it.take(200) },
                    label = { Text("Mensaje") },
                    colors = colores,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = telefonoOk && mensaje.isNotBlank(),
                onClick = { onConfirmar(mensaje.trim(), telefono) }
            ) {
                Text("Enviar", color = UF.Verde, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = UF.TextoSec)
            }
        }
    )
}

// ==========================================================
// ESQUELETO MIENTRAS CARGA
// ==========================================================

@Composable
private fun CargandoDetalle() {

    Column(modifier = Modifier.fillMaxSize()) {

        Esqueleto(
            Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
        )

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Esqueleto(Modifier.fillMaxWidth(0.4f).height(24.dp).clip(RoundedCornerShape(12.dp)))
            Esqueleto(Modifier.fillMaxWidth(0.8f).height(30.dp).clip(RoundedCornerShape(12.dp)))
            Esqueleto(Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(20.dp)))
        }
    }
}
