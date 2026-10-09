package com.unifind.app.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.LruCache
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unifind.app.R
import com.unifind.app.data.Catalogo
import com.unifind.app.data.EstadoReporte
import com.unifind.app.data.Reporte
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================================
// SISTEMA DE DISEÑO UNIFIND
// Colores, componentes y animaciones compartidos por todas
// las pantallas para que la app se vea como un solo producto.
// ==========================================================

object UF {

    val Verde = Color(0xFF008F63)
    val VerdeOscuro = Color(0xFF006B4A)
    val VerdeProfundo = Color(0xFF004D3A)
    val Menta = Color(0xFFE2F6EB)

    val Rojo = Color(0xFFE53935)
    val RojoSuave = Color(0xFFFFE9E9)

    val Ambar = Color(0xFFB26A00)
    val AmbarSuave = Color(0xFFFFF1D1)

    val Azul = Color(0xFF1E6FD9)
    val AzulSuave = Color(0xFFE3EFFF)

    val Gris = Color(0xFFF3F5F5)
    val Linea = Color(0xFFE3E8E7)
    val Texto = Color(0xFF202124)
    val TextoSec = Color(0xFF5F6368)
}

// ==========================================================
// ESTILO DE CADA ESTADO
// ==========================================================

data class EstiloEstado(
    val fondo: Color,
    val texto: Color,
    val acento: Color
)

fun EstadoReporte.estilo(): EstiloEstado = when (this) {
    EstadoReporte.PERDIDO ->
        EstiloEstado(UF.RojoSuave, UF.Rojo, UF.Rojo)
    EstadoReporte.ENCONTRADO ->
        EstiloEstado(UF.Menta, UF.VerdeOscuro, UF.Verde)
    EstadoReporte.EN_RECUPERACION ->
        EstiloEstado(UF.AmbarSuave, UF.Ambar, Color(0xFFF59E0B))
    EstadoReporte.RECUPERADO ->
        EstiloEstado(UF.AzulSuave, UF.Azul, UF.Azul)
}

// ==========================================================
// ANIMACIONES
// ==========================================================

/** Efecto de "rebote" al tocar. Úsalo en lugar de clickable. */
@Composable
fun Modifier.alPresionar(onClick: () -> Unit): Modifier {

    val fuente = remember { MutableInteractionSource() }
    val presionado by fuente.collectIsPressedAsState()

    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "escala"
    )

    return this
        .graphicsLayer {
            scaleX = escala
            scaleY = escala
        }
        .clickable(
            interactionSource = fuente,
            indication = null,
            onClick = onClick
        )
}

/** Aparece con fundido + deslizamiento. [indice] escalona la entrada. */
@Composable
fun Aparecer(
    indice: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    var visible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!visible) {
            delay((indice * 50L).coerceAtMost(350L))
            visible = true
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(300)) +
                slideInVertically(
                    animationSpec = tween(
                        durationMillis = 360,
                        easing = FastOutSlowInEasing
                    ),
                    initialOffsetY = { it / 5 }
                )
    ) {
        content()
    }
}

/** Número que sube hasta su valor (contadores del Inicio y Perfil). */
@Composable
fun NumeroAnimado(
    valor: Int,
    color: Color,
    tamano: TextUnit = 26.sp
) {

    val animado by animateIntAsState(
        targetValue = valor,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "numero"
    )

    Text(
        text = animado.toString(),
        fontSize = tamano,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

/** Bloque gris con brillo mientras carga. */
@Composable
fun Esqueleto(modifier: Modifier = Modifier) {

    val transicion = rememberInfiniteTransition(label = "brillo")

    val x by transicion.animateFloat(
        initialValue = -300f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "x"
    )

    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFFE6EDEB),
                    Color(0xFFF6F9F8),
                    Color(0xFFE6EDEB)
                ),
                start = Offset(x, 0f),
                end = Offset(x + 300f, 0f)
            )
        )
    )
}

@Composable
fun EsqueletoTarjeta() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Esqueleto(Modifier.size(78.dp).clip(RoundedCornerShape(16.dp)))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Esqueleto(Modifier.fillMaxWidth(0.7f).height(16.dp).clip(RoundedCornerShape(8.dp)))
            Esqueleto(Modifier.fillMaxWidth(0.5f).height(12.dp).clip(RoundedCornerShape(8.dp)))
            Esqueleto(Modifier.fillMaxWidth(0.3f).height(22.dp).clip(RoundedCornerShape(11.dp)))
        }
    }
}

// ==========================================================
// FOTO DE UN REPORTE (las fotos se guardan en Base64)
// ==========================================================

private val cacheFotos = object : LruCache<String, Bitmap>(24) {}

private fun decodificarFoto(base64: String, ladoMax: Int): Bitmap? {

    val clave = "${base64.hashCode()}_${base64.length}_$ladoMax"

    cacheFotos.get(clave)?.let { return it }

    return try {

        val bytes = Base64.decode(
            base64.substringAfter("base64,"),
            Base64.DEFAULT
        )

        val opciones = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opciones)

        var muestra = 1

        while (opciones.outWidth / muestra > ladoMax * 2 ||
            opciones.outHeight / muestra > ladoMax * 2
        ) {
            muestra *= 2
        }

        val real = BitmapFactory.Options().apply { inSampleSize = muestra }

        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, real)
            ?.also { cacheFotos.put(clave, it) }

    } catch (e: Exception) {
        null
    }
}

@Composable
fun FotoReporte(
    base64: String?,
    categoria: String,
    modifier: Modifier = Modifier,
    ladoMax: Int = 300,
    tamanoEmoji: TextUnit = 30.sp
) {

    val imagen by produceState<ImageBitmap?>(null, base64) {
        value = if (base64.isNullOrBlank()) {
            null
        } else {
            withContext(Dispatchers.Default) {
                decodificarFoto(base64, ladoMax)?.asImageBitmap()
            }
        }
    }

    val bitmap = imagen

    if (bitmap != null) {

        Image(
            bitmap = bitmap,
            contentDescription = "Foto del objeto",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )

    } else {

        Box(
            modifier = modifier.background(
                Brush.linearGradient(listOf(UF.Menta, Color(0xFFCFEFE0)))
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = Catalogo.emoji(categoria),
                fontSize = tamanoEmoji
            )
        }
    }
}

// ==========================================================
// ENCABEZADO CON OLA (banner_pantallas)
// ==========================================================

@Composable
fun EncabezadoOla(
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    alto: Dp = 168.dp,
    contenidoDerecha: @Composable () -> Unit = {}
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(alto)
    ) {

        Image(
            painter = painterResource(id = R.drawable.banner_pantallas),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x66004D3A), Color.Transparent)
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 22.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = titulo,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = subtitulo,
                    fontSize = 14.sp,
                    color = Color(0xFFE6FFF4)
                )
            }

            contenidoDerecha()
        }
    }
}

// ==========================================================
// CHIP DE ESTADO
// ==========================================================

@Composable
fun ChipEstado(
    estado: EstadoReporte,
    modifier: Modifier = Modifier,
    largo: Boolean = false
) {

    val estilo = estado.estilo()

    Row(
        modifier = modifier
            .background(estilo.fondo, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(7.dp)
                .background(estilo.acento, CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = if (largo) estado.etiqueta else estado.corto,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = estilo.texto,
            maxLines = 1
        )
    }
}

/** Chip de selección para filtros. */
@Composable
fun ChipFiltro(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val fondo by androidx.compose.animation.animateColorAsState(
        targetValue = if (seleccionado) UF.Verde else Color.White,
        animationSpec = tween(220),
        label = "fondoChip"
    )

    val colorTexto by androidx.compose.animation.animateColorAsState(
        targetValue = if (seleccionado) Color.White else UF.TextoSec,
        animationSpec = tween(220),
        label = "textoChip"
    )

    Box(
        modifier = modifier
            .alPresionar(onClick)
            .shadow(
                elevation = if (seleccionado) 0.dp else 1.dp,
                shape = RoundedCornerShape(50),
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 14.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
            color = colorTexto,
            maxLines = 1
        )
    }
}

// ==========================================================
// TARJETA DE REPORTE (Inicio, Buscar y Mis reportes)
// ==========================================================

@Composable
fun TarjetaReporte(
    reporte: Reporte,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    mostrarContactos: Boolean = false
) {

    val forma = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .alPresionar(onClick)
            .shadow(
                elevation = 3.dp,
                shape = forma,
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .background(Color.White, forma)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        FotoReporte(
            base64 = reporte.fotos.firstOrNull(),
            categoria = reporte.categoria,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            Text(
                text = reporte.titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = UF.Texto,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(id = R.drawable.location_dot_solid),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    colorFilter = ColorFilter.tint(UF.TextoSec)
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    text = reporte.ubicacion,
                    fontSize = 13.sp,
                    color = UF.TextoSec,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                ChipEstado(reporte.estado)

                Text(
                    text = Catalogo.emoji(reporte.categoria) + " " +
                            reporte.categoria.ifBlank { "Otros" },
                    fontSize = 12.sp,
                    color = UF.TextoSec,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {

            Text(
                text = tiempoRelativo(reporte.fechaMillis),
                fontSize = 12.sp,
                color = UF.TextoSec
            )

            if (mostrarContactos) {

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .background(UF.Gris, RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.envelope_solid),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(UF.VerdeOscuro)
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = reporte.numContactos.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = UF.VerdeOscuro
                    )
                }
            }
        }
    }
}

// ==========================================================
// ESTADO VACÍO / ERROR
// ==========================================================

@Composable
fun EstadoVacio(
    emoji: String,
    titulo: String,
    texto: String,
    modifier: Modifier = Modifier,
    textoBoton: String? = null,
    onBoton: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(88.dp)
                .background(UF.Menta, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 40.sp)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = titulo,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = UF.Texto,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = texto,
            fontSize = 14.sp,
            color = UF.TextoSec,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        if (textoBoton != null) {

            Spacer(Modifier.height(18.dp))

            BotonPrimario(
                texto = textoBoton,
                onClick = onBoton,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
}

// ==========================================================
// BOTONES
// ==========================================================

@Composable
fun BotonPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = UF.Verde,
    cargando: Boolean = false,
    habilitado: Boolean = true
) {

    val activo = habilitado && !cargando

    Box(
        modifier = modifier
            .height(52.dp)
            .alPresionar { if (activo) onClick() }
            .shadow(
                elevation = if (activo) 6.dp else 0.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = color,
                spotColor = color
            )
            .background(
                if (activo) color else color.copy(alpha = 0.45f),
                RoundedCornerShape(26.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        if (cargando) {

            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )

        } else {

            Text(
                text = texto,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = UF.VerdeOscuro
) {

    Box(
        modifier = modifier
            .height(52.dp)
            .alPresionar(onClick)
            .background(Color.White, RoundedCornerShape(26.dp))
            .border(1.5.dp, color, RoundedCornerShape(26.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// ==========================================================
// TEXTO DE SECCIÓN
// ==========================================================

@Composable
fun TituloSeccion(
    texto: String,
    modifier: Modifier = Modifier,
    estilo: TextStyle = TextStyle(
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        color = UF.Texto
    )
) {
    Text(text = texto, modifier = modifier, style = estilo)
}

// ==========================================================
// UTILIDADES
// ==========================================================

fun tiempoRelativo(millis: Long): String {

    val minutos = (System.currentTimeMillis() - millis) / 60000

    return when {
        minutos < 1 -> "Ahora"
        minutos < 60 -> "Hace $minutos min"
        minutos < 1440 -> "Hace ${minutos / 60} h"
        minutos < 43200 -> "Hace ${minutos / 1440} d"
        else -> formatearFecha(millis)
    }
}

fun formatearFecha(millis: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale("es", "MX"))
        .format(Date(millis))

fun formatearFechaHora(millis: Long): String =
    SimpleDateFormat("d MMM yyyy, h:mm a", Locale("es", "MX"))
        .format(Date(millis))

/** Abre el chat de WhatsApp (México: 52 + 10 dígitos). */
fun abrirWhatsApp(contexto: Context, telefono: String, texto: String) {

    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://wa.me/52$telefono?text=${Uri.encode(texto)}")
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    runCatching {
        contexto.startActivity(intent)
    }.onFailure {
        Toast.makeText(
            contexto,
            "No se pudo abrir WhatsApp",
            Toast.LENGTH_LONG
        ).show()
    }
}

/** Llamada telefónica (abre el marcador, no requiere permisos). */
fun llamar(contexto: Context, telefono: String) {

    runCatching {
        contexto.startActivity(
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefono"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}


fun normalizar(texto: String): String =
    java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .lowercase()
        .trim()