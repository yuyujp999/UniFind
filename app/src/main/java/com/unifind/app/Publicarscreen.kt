package com.unifind.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.Normalizer

// ==========================================================
// COLORES UNIFIND
// ==========================================================

private val Verde = Color(0xFF008F63)
private val VerdeOscuro = Color(0xFF006B4A)
private val VerdeClaro = Color(0xFFE2F6EB)
private val VerdeBanner = Color(0xFF004D3A)

private val Rojo = Color(0xFFE53935)
private val RojoOscuro = Color(0xFFC62828)
private val RojoClaro = Color(0xFFFFE9E9)

private val FondoPrincipal = Color(0xFFE8F7EF)
private val FondoCampo = Color(0xFFF6F8F8)
private val FondoGris = Color(0xFFF0F3F3)

private val BordeCampo = Color(0xFFE0E5E3)

private val TextoPrincipal = Color(0xFF202124)
private val TextoSecundario = Color(0xFF5F6368)
private val TextoClaro = Color(0xFF8A9094)

private const val LIMITE_DESCRIPCION = 200

// Ponlo en true SOLO para probar con una sola cuenta
// (permite reclamar tus propios reportes).
private const val PERMITIR_RECLAMAR_PROPIOS = false

// ==========================================================
// OPCIONES
// ==========================================================

private val categoriasFormulario = listOf(
    "Mochilas",
    "Electrónicos",
    "Llaves",
    "Ropa",
    "Accesorios",
    "Documentos",
    "Libros",
    "Otros"
)

private val emojisCategoria = mapOf(
    "Mochilas" to "🎒",
    "Electrónicos" to "📱",
    "Llaves" to "🔑",
    "Ropa" to "👕",
    "Accesorios" to "👓",
    "Documentos" to "📄",
    "Libros" to "📚",
    "Otros" to "📦"
)

private val ubicacionesFormulario = listOf(
    "Biblioteca",
    "Cafetería",
    "Edificio A",
    "Edificio B",
    "Aulas",
    "Gimnasio",
    "Cancha",
    "Estacionamiento",
    "Otro lugar"
)

// ==========================================================
// PANTALLA PUBLICAR
// Un solo formulario para "Perdí algo" y "Encontré algo".
// Mientras escribes, sugiere reportes parecidos de la otra
// parte para no duplicar publicaciones.
//
// Diseño: banner de fondo (publicar_banner), campos que van
// apareciendo conforme se llenan los anteriores y animaciones.
// ==========================================================

@Composable
fun PublicarScreen(
    onNavegar: (String) -> Unit = {},
    onTerminar: () -> Unit = {}
) {

    val contexto = LocalContext.current

    val miUid =
        FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val tecladoAbierto =
        WindowInsets.ime.getBottom(LocalDensity.current) > 0

    var tipo by remember {
        mutableStateOf("perdido")
    }

    val esPerdido = tipo == "perdido"

    // Colores que cambian según el tipo (rojo = perdido, verde = encontrado)
    val colorPrincipal by animateColorAsState(
        targetValue = if (esPerdido) Rojo else Verde,
        animationSpec = tween(450),
        label = "colorPrincipal"
    )

    val colorSecundario by animateColorAsState(
        targetValue = if (esPerdido) RojoOscuro else VerdeOscuro,
        animationSpec = tween(450),
        label = "colorSecundario"
    )

    val colorClaro = if (esPerdido) RojoClaro else VerdeClaro

    val ejemploNombre =
        if (esPerdido) {
            "Ej. Mochila negra"
        } else {
            "Ej. Llaves con llavero azul"
        }

    // La otra parte: si perdí algo busco entre "Encontrado" y viceversa
    val estadoOpuesto =
        if (esPerdido) "Encontrado" else "Perdido"

    // ======================================================
    // ESTADOS DEL FORMULARIO
    // ======================================================

    val fotos = remember {
        mutableStateListOf<Uri>()
    }

    var nombre by remember {
        mutableStateOf("")
    }

    var categoria by remember {
        mutableStateOf<String?>(null)
    }

    var descripcion by remember {
        mutableStateOf("")
    }

    var ubicacion by remember {
        mutableStateOf<String?>(null)
    }

    var whatsapp by remember {
        mutableStateOf(
            contexto
                .getSharedPreferences("unifind", Context.MODE_PRIVATE)
                .getString("whatsapp", "")
                .orEmpty()
        )
    }

    var intentoPublicar by remember {
        mutableStateOf(false)
    }

    var publicando by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    // ======================================================
    // APARICIÓN PROGRESIVA DE CAMPOS
    // nombre -> categoría -> descripción + ubicación -> WhatsApp
    // Una vez que aparece un bloque ya no se oculta.
    // ======================================================

    val scrollState = rememberScrollState()

    var entro by remember {
        mutableStateOf(false)
    }

    var verCategoria by remember {
        mutableStateOf(false)
    }

    var verDetalles by remember {
        mutableStateOf(false)
    }

    var verContacto by remember {
        mutableStateOf(false)
    }

    val nombreOk = nombre.isNotBlank()
    val categoriaOk = categoria != null
    val ubicacionOk = ubicacion != null
    val whatsappOk = whatsapp.length == 10

    val completados =
        listOf(nombreOk, categoriaOk, ubicacionOk, whatsappOk)
            .count { it }

    LaunchedEffect(Unit) {
        entro = true
    }

    LaunchedEffect(nombreOk) {
        if (nombreOk) verCategoria = true
    }

    LaunchedEffect(categoriaOk) {
        if (categoriaOk) verDetalles = true
    }

    LaunchedEffect(ubicacionOk) {
        if (ubicacionOk) verContacto = true
    }

    val reveladas =
        listOf(verCategoria, verDetalles, verContacto)
            .count { it }

    LaunchedEffect(reveladas) {
        if (reveladas > 0) {
            delay(380)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    // ======================================================
    // COINCIDENCIAS
    // ======================================================

    var reportesOpuestos by remember {
        mutableStateOf<List<ReporteExistente>>(emptyList())
    }

    var seleccionado by remember {
        mutableStateOf<ReporteExistente?>(null)
    }

    var enviando by remember {
        mutableStateOf(false)
    }

    DisposableEffect(estadoOpuesto) {

        val registro =
            FirebaseFirestore.getInstance()
                .collection("reportes")
                .whereEqualTo("estado", estadoOpuesto)
                .addSnapshotListener { snap, error ->

                    if (error != null) {
                        return@addSnapshotListener
                    }

                    reportesOpuestos =
                        snap?.documents.orEmpty().map {
                            documentoAReporte(it)
                        }
                }

        onDispose {
            registro.remove()
        }
    }

    val sugerencias =
        remember(reportesOpuestos, nombre, categoria, ubicacion) {

            buscarCoincidencias(
                reportes = reportesOpuestos,
                nombre = nombre,
                categoria = categoria,
                ubicacion = ubicacion,
                miUid = miUid
            )
        }

    // ======================================================
    // GALERÍA
    // ======================================================

    val lanzadorFotos =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(3)
        ) { uris ->

            uris.forEach { uri ->

                if (
                    fotos.size < 3 &&
                    !fotos.contains(uri)
                ) {
                    fotos.add(uri)
                }
            }
        }

    val abrirGaleria: () -> Unit = {

        if (fotos.size >= 3) {

            Toast.makeText(
                contexto,
                "Máximo 3 fotos",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            lanzadorFotos.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }

    // ======================================================
    // PUBLICAR (crea un reporte nuevo en Firestore)
    // ======================================================

    val publicar: () -> Unit = {

        if (!publicando) {

            intentoPublicar = true

            when {

                nombre.isBlank() -> {

                    Toast.makeText(
                        contexto,
                        "Escribe el nombre del objeto",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                categoria == null -> {

                    Toast.makeText(
                        contexto,
                        "Selecciona una categoría",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                ubicacion == null -> {

                    Toast.makeText(
                        contexto,
                        "Selecciona la ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                whatsapp.length != 10 -> {

                    Toast.makeText(
                        contexto,
                        "Escribe tu WhatsApp a 10 dígitos",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {

                    publicando = true

                    val uris = fotos.toList()

                    scope.launch {

                        val fotosBase64 =
                            withContext(Dispatchers.IO) {
                                uris.mapNotNull {
                                    uriABase64(contexto, it)
                                }
                            }

                        val usuarioId =
                            FirebaseAuth.getInstance()
                                .currentUser
                                ?.uid
                                ?: "anonimo"

                        val estadoTexto =
                            if (esPerdido) "Perdido" else "Encontrado"

                        val reporte = hashMapOf<String, Any>(
                            "titulo" to nombre.trim(),
                            "descripcion" to descripcion.trim(),
                            "categoria" to (categoria ?: ""),
                            "ubicacionAprox" to (ubicacion ?: ""),
                            "estado" to estadoTexto,
                            "fecha" to FieldValue.serverTimestamp(),
                            "imagenUrl" to "",
                            "fotosBase64" to fotosBase64,
                            "usuarioId" to usuarioId,
                            "numContactos" to 0,
                            "whatsapp" to whatsapp
                        )

                        FirebaseFirestore.getInstance()
                            .collection("reportes")
                            .add(reporte)
                            .addOnSuccessListener {

                                publicando = false

                                // Recordar el WhatsApp para la próxima vez
                                contexto
                                    .getSharedPreferences(
                                        "unifind",
                                        Context.MODE_PRIVATE
                                    )
                                    .edit()
                                    .putString("whatsapp", whatsapp)
                                    .apply()

                                // Limpiar el formulario
                                nombre = ""
                                descripcion = ""
                                categoria = null
                                ubicacion = null
                                intentoPublicar = false
                                verCategoria = false
                                verDetalles = false
                                verContacto = false
                                fotos.clear()

                                Toast.makeText(
                                    contexto,
                                    "Reporte publicado",
                                    Toast.LENGTH_SHORT
                                ).show()

                                onTerminar()
                            }
                            .addOnFailureListener { e ->

                                publicando = false

                                Toast.makeText(
                                    contexto,
                                    "No se pudo publicar: ${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }
            }
        }
    }

    // ======================================================
    // PANTALLA
    // ======================================================

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal)
    ) {

        // Espacio para que el logo "Publicar" del banner se vea completo
        val espacioLogo = maxHeight * 0.125f

        // Fondo: banner
        Image(
            painter = painterResource(id = R.drawable.publicar_banner),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        Scaffold(
            containerColor = Color.Transparent,

            bottomBar = {

                if (!tecladoAbierto) {

                    UniFindBottomBar(
                        seleccionado = "Publicar",
                        onSeleccionar = onNavegar
                    )
                }
            }

        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = paddingValues.calculateBottomPadding()
                    )
                    .imePadding()
            ) {

                // ==============================================
                // CONTENIDO (encabezado + formulario)
                // ==============================================

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp)
                ) {

                    Spacer(modifier = Modifier.height(espacioLogo))

                    Aparece(visible = entro, retraso = 60) {
                        SubtituloBanner(esPerdido = esPerdido)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Aparece(visible = entro, retraso = 160) {
                        SelectorTipoPublicacion(
                            tipo = tipo,
                            colorIndicador = colorPrincipal,
                            onCambio = { tipo = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Aparece(visible = entro, retraso = 260) {
                        ProgresoFormulario(
                            completados = completados,
                            total = 4,
                            color = colorPrincipal
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Aparece(visible = entro, retraso = 360) {

                        Column {

                            FormularioSecciones(
                                acento = colorPrincipal,
                                acentoClaro = colorClaro,
                                fotos = fotos,
                                abrirGaleria = abrirGaleria,
                                quitarFoto = { uri ->
                                    fotos.remove(uri)
                                },
                                nombre = nombre,
                                onNombre = { nombre = it },
                                ejemploNombre = ejemploNombre,
                                categoria = categoria,
                                onCategoria = { categoria = it },
                                descripcion = descripcion,
                                onDescripcion = { descripcion = it },
                                ubicacion = ubicacion,
                                onUbicacion = { ubicacion = it },
                                whatsapp = whatsapp,
                                onWhatsapp = {
                                    whatsapp = it.filter { c -> c.isDigit() }.take(10)
                                },
                                intentoPublicar = intentoPublicar,
                                mostrarCategoria = verCategoria || intentoPublicar,
                                mostrarDetalles = verDetalles || intentoPublicar,
                                mostrarContacto = verContacto || intentoPublicar
                            )

                            // ======================================
                            // SUGERENCIAS: ¿YA LO REPORTARON?
                            // ======================================

                            Revela(visible = sugerencias.isNotEmpty()) {

                                Column {

                                    Spacer(modifier = Modifier.height(14.dp))

                                    TarjetaSeccionFormulario {

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(CircleShape)
                                                    .background(VerdeClaro),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "🔎",
                                                    fontSize = 18.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "¿Ya lo reportaron?",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextoPrincipal
                                                )

                                                Spacer(modifier = Modifier.height(2.dp))

                                                Text(
                                                    text = if (esPerdido) {
                                                        "Encontramos reportes parecidos. Revisa si alguno es tu objeto."
                                                    } else {
                                                        "Encontramos objetos parecidos reportados como perdidos."
                                                    },
                                                    fontSize = 12.sp,
                                                    lineHeight = 17.sp,
                                                    color = TextoSecundario
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        sugerencias.forEachIndexed { index, reporte ->

                                            TarjetaCoincidencia(
                                                reporte = reporte,
                                                textoBoton = if (esPerdido) {
                                                    "Es mío"
                                                } else {
                                                    "Lo encontré yo"
                                                },
                                                onReclamar = {
                                                    seleccionado = reporte
                                                }
                                            )

                                            if (index < sugerencias.lastIndex) {
                                                Spacer(modifier = Modifier.height(9.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "* Campos obligatorios",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = VerdeBanner,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                // ==============================================
                // BARRA INFERIOR CON EL BOTÓN PUBLICAR
                // ==============================================

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(BordeCampo)
                    )

                    Box(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        )
                    ) {

                        BotonPublicarAnimado(
                            texto = if (esPerdido) {
                                "Publicar objeto perdido"
                            } else {
                                "Publicar objeto encontrado"
                            },
                            cargando = publicando,
                            listo = completados == 4,
                            colorInicio = colorSecundario,
                            colorFin = colorPrincipal,
                            onClick = publicar
                        )
                    }
                }
            }
        }

        // ======================================================
        // DIÁLOGO: CONFIRMAR COINCIDENCIA (teléfono + mensaje)
        // ======================================================

        val reporteElegido = seleccionado

        if (reporteElegido != null) {

            DialogoCoincidencia(
                reporte = reporteElegido,
                usuarioPerdio = esPerdido,
                enviando = enviando,
                onCancelar = {
                    seleccionado = null
                },
                onEnviar = { mensaje ->

                    enviando = true

                    registrarCoincidencia(
                        reporteId = reporteElegido.id,
                        estadoEsperado = estadoOpuesto,
                        usuarioPerdio = esPerdido,
                        mensaje = mensaje,
                        onExito = {

                            enviando = false
                            seleccionado = null

                            val saludo =
                                if (esPerdido) {
                                    "Hola, vi tu reporte del objeto encontrado \"${reporteElegido.titulo}\" en UniFind y creo que es mío."
                                } else {
                                    "Hola, vi tu reporte del objeto perdido \"${reporteElegido.titulo}\" en UniFind y creo que lo encontré."
                                }

                            abrirWhatsApp(
                                contexto,
                                reporteElegido.whatsapp,
                                "$saludo $mensaje"
                            )

                            onTerminar()
                        },
                        onError = { motivo ->

                            enviando = false

                            Toast.makeText(
                                contexto,
                                motivo,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            )
        }
    }
}

// ==========================================================
// ANIMACIONES DE APARICIÓN
// ==========================================================

// Entrada escalonada (al abrir la pantalla)
@Composable
private fun Aparece(
    visible: Boolean,
    retraso: Int,
    contenido: @Composable () -> Unit
) {

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            tween(480, delayMillis = retraso)
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = 520,
                delayMillis = retraso,
                easing = FastOutSlowInEasing
            )
        ) { it / 4 }
    ) {
        contenido()
    }
}

// Aparición de un campo cuando se llena el anterior
@Composable
private fun Revela(
    visible: Boolean,
    contenido: @Composable () -> Unit
) {

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(420)) +
                expandVertically(
                    spring(stiffness = Spring.StiffnessMediumLow)
                ) +
                slideInVertically(tween(450)) { it / 6 },
        exit = fadeOut(tween(200)) + shrinkVertically()
    ) {
        contenido()
    }
}

// ==========================================================
// SUBTÍTULO SOBRE EL BANNER
// (el título "Publicar" ya viene dibujado en el banner)
// ==========================================================

@Composable
private fun SubtituloBanner(
    esPerdido: Boolean
) {

    AnimatedContent(
        targetState = esPerdido,
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(200))
        },
        label = "subtituloBanner"
    ) { perdido ->

        Text(
            text = if (perdido) {
                "Cuéntanos qué perdiste y te ayudamos a encontrarlo."
            } else {
                "Describe lo que encontraste para ayudar a devolverlo."
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium,
            color = VerdeBanner
        )
    }
}

// ==========================================================
// BARRA DE PROGRESO DEL FORMULARIO
// ==========================================================

@Composable
private fun ProgresoFormulario(
    completados: Int,
    total: Int,
    color: Color
) {

    val progreso by animateFloatAsState(
        targetValue = completados.toFloat() / total,
        animationSpec = tween(500),
        label = "progresoFormulario"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.75f))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(progreso)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = if (completados == total) {
                "¡Listo!"
            } else {
                "$completados de $total"
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeBanner
        )
    }
}

// ==========================================================
// SELECTOR "PERDÍ / ENCONTRÉ" (indicador que se desliza)
// ==========================================================

@Composable
private fun SelectorTipoPublicacion(
    tipo: String,
    colorIndicador: Color,
    onCambio: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val forma = RoundedCornerShape(22.dp)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .shadow(
                elevation = 8.dp,
                shape = forma,
                ambientColor = Color(0x30000000),
                spotColor = Color(0x30000000)
            )
            .background(Color.White, forma)
            .padding(5.dp)
    ) {

        val anchoOpcion = maxWidth / 2

        val desplazamiento by animateDpAsState(
            targetValue = if (tipo == "perdido") 0.dp else anchoOpcion,
            animationSpec = spring(
                dampingRatio = 0.72f,
                stiffness = Spring.StiffnessMedium
            ),
            label = "indicadorTipo"
        )

        Box(
            modifier = Modifier
                .offset(x = desplazamiento)
                .width(anchoOpcion)
                .fillMaxHeight()
                .clip(RoundedCornerShape(17.dp))
                .background(colorIndicador)
        )

        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            OpcionTipo(
                texto = "🔎  Perdí algo",
                seleccionado = tipo == "perdido",
                modifier = Modifier.weight(1f),
                onClick = {
                    onCambio("perdido")
                }
            )

            OpcionTipo(
                texto = "🎁  Encontré algo",
                seleccionado = tipo == "encontrado",
                modifier = Modifier.weight(1f),
                onClick = {
                    onCambio("encontrado")
                }
            )
        }
    }
}

@Composable
private fun OpcionTipo(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val presionado by interactionSource.collectIsPressedAsState()

    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "escalaOpcionTipo"
    )

    val colorTexto by animateColorAsState(
        targetValue = if (seleccionado) Color.White else TextoSecundario,
        animationSpec = tween(300),
        label = "colorTextoTipo"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .scale(escala)
            .clip(RoundedCornerShape(17.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = texto,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = colorTexto
        )
    }
}

// ==========================================================
// BOTÓN PRINCIPAL ANIMADO
// ==========================================================

@Composable
private fun BotonPublicarAnimado(
    texto: String,
    cargando: Boolean,
    listo: Boolean,
    colorInicio: Color,
    colorFin: Color,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val presionado by interactionSource.collectIsPressedAsState()

    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.975f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "escalaBotonPublicar"
    )

    // Pequeño "salto" cuando ya están todos los datos
    val salto = remember {
        Animatable(1f)
    }

    LaunchedEffect(listo) {

        if (listo) {

            salto.animateTo(1.05f, tween(130))

            salto.animateTo(
                1f,
                spring(
                    dampingRatio = 0.4f,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(escala * salto.value)
            .shadow(
                elevation = if (presionado) 2.dp else 6.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = colorFin,
                spotColor = colorFin
            )
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        colorInicio,
                        colorFin
                    )
                )
            )
            .clickable(
                enabled = !cargando,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {

        if (cargando) {

            CircularProgressIndicator(
                modifier = Modifier.size(23.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )

        } else {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.20f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = texto,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ==========================================================
// FORMULARIO: 4 SECCIONES NUMERADAS
// Los campos aparecen conforme se llenan los anteriores.
// ==========================================================

@Composable
private fun FormularioSecciones(
    acento: Color,
    acentoClaro: Color,
    fotos: List<Uri>,
    abrirGaleria: () -> Unit,
    quitarFoto: (Uri) -> Unit,
    nombre: String,
    onNombre: (String) -> Unit,
    ejemploNombre: String,
    categoria: String?,
    onCategoria: (String) -> Unit,
    descripcion: String,
    onDescripcion: (String) -> Unit,
    ubicacion: String?,
    onUbicacion: (String) -> Unit,
    whatsapp: String,
    onWhatsapp: (String) -> Unit,
    intentoPublicar: Boolean,
    mostrarCategoria: Boolean,
    mostrarDetalles: Boolean,
    mostrarContacto: Boolean
) {

    // ---------------------- 1. FOTOS ----------------------

    TarjetaSeccionNumerada(
        numero = 1,
        titulo = "Fotos",
        subtitulo = if (fotos.isEmpty()) {
            "Opcional, pero ayudan mucho a identificarlo"
        } else {
            "${fotos.size}/3 fotos agregadas"
        },
        acento = acento
    ) {

        if (fotos.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(acentoClaro.copy(alpha = 0.45f))
                    .drawBehind {
                        drawRoundRect(
                            color = acento.copy(alpha = 0.55f),
                            cornerRadius = CornerRadius(18.dp.toPx()),
                            style = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(18f, 14f)
                                )
                            )
                        )
                    }
                    .clickable(onClick = abrirGaleria),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(acento),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar fotos",
                            tint = Color.White,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "Toca para agregar fotos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )

                    Text(
                        text = "Hasta 3 imágenes",
                        fontSize = 11.sp,
                        color = TextoSecundario
                    )
                }
            }

        } else {

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                fotos.forEach { uri ->
                    MiniFoto(
                        uri = uri,
                        onQuitar = { quitarFoto(uri) }
                    )
                }

                if (fotos.size < 3) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(acentoClaro.copy(alpha = 0.45f))
                            .drawBehind {
                                drawRoundRect(
                                    color = acento.copy(alpha = 0.55f),
                                    cornerRadius = CornerRadius(16.dp.toPx()),
                                    style = Stroke(
                                        width = 1.5.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(16f, 12f)
                                        )
                                    )
                                )
                            }
                            .clickable(onClick = abrirGaleria),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar otra foto",
                            tint = acento,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // ---------------------- 2. DETALLES ----------------------

    TarjetaSeccionNumerada(
        numero = 2,
        titulo = "Detalles del objeto",
        subtitulo = "Qué es y cómo es",
        acento = acento
    ) {

        Etiqueta(
            texto = "Nombre del objeto",
            obligatorio = true
        )

        CampoTexto(
            valor = nombre,
            onCambio = onNombre,
            placeholder = ejemploNombre,
            modifier = Modifier.height(54.dp),
            singleLine = true,
            error = intentoPublicar && nombre.isBlank(),
            acento = acento
        )

        // Aparece al escribir el nombre
        Revela(visible = mostrarCategoria) {

            Column {

                Spacer(modifier = Modifier.height(16.dp))

                Etiqueta(
                    texto = "Categoría",
                    obligatorio = true
                )

                CategoriasChips(
                    seleccion = categoria,
                    error = intentoPublicar && categoria == null,
                    acento = acento,
                    onSeleccion = onCategoria
                )
            }
        }

        // Aparece al elegir la categoría
        Revela(visible = mostrarDetalles) {

            Column {

                Spacer(modifier = Modifier.height(8.dp))

                Etiqueta(
                    texto = "Descripción",
                    obligatorio = false
                )

                CampoTexto(
                    valor = descripcion,
                    onCambio = {
                        if (it.length <= LIMITE_DESCRIPCION) {
                            onDescripcion(it)
                        }
                    },
                    placeholder = "Marcas, color, contenido o detalles importantes...",
                    modifier = Modifier.height(110.dp),
                    singleLine = false,
                    error = false,
                    acento = acento
                )

                Text(
                    text = "${descripcion.length}/$LIMITE_DESCRIPCION",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    fontSize = 11.sp,
                    color = TextoClaro,
                    textAlign = TextAlign.End
                )
            }
        }
    }

    // ---------------------- 3. UBICACIÓN ----------------------
    // Aparece al elegir la categoría

    Revela(visible = mostrarDetalles) {

        Column {

            Spacer(modifier = Modifier.height(14.dp))

            TarjetaSeccionNumerada(
                numero = 3,
                titulo = "Ubicación",
                subtitulo = "Dónde ocurrió o dónde lo encontraste",
                acento = acento
            ) {

                Etiqueta(
                    texto = "Ubicación aproximada",
                    obligatorio = true
                )

                CampoDesplegable(
                    placeholder = "Selecciona la ubicación",
                    seleccion = ubicacion,
                    opciones = ubicacionesFormulario,
                    iconoInicio = R.drawable.location_dot_solid,
                    flechaAbajo = false,
                    error = intentoPublicar && ubicacion == null,
                    acento = acento,
                    onSeleccion = onUbicacion
                )
            }
        }
    }

    // ---------------------- 4. CONTACTO ----------------------
    // Aparece al elegir la ubicación

    Revela(visible = mostrarContacto) {

        Column {

            Spacer(modifier = Modifier.height(14.dp))

            TarjetaSeccionNumerada(
                numero = 4,
                titulo = "Contacto por WhatsApp",
                subtitulo = "Para que puedan escribirte directamente",
                acento = acento
            ) {

                Etiqueta(
                    texto = "Tu WhatsApp",
                    obligatorio = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8F8EE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                id = R.drawable.square_whatsapp_brands_solid
                            ),
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(29.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(9.dp))

                    CampoTexto(
                        valor = whatsapp,
                        onCambio = onWhatsapp,
                        placeholder = "10 dígitos, ej. 8112345678",
                        modifier = Modifier
                            .height(54.dp)
                            .weight(1f),
                        singleLine = true,
                        error = intentoPublicar && whatsapp.length != 10,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        ocuparAncho = false,
                        acento = acento
                    )
                }
            }
        }
    }
}

// ==========================================================
// TARJETA DE SECCIÓN NUMERADA
// ==========================================================

@Composable
private fun TarjetaSeccionNumerada(
    numero: Int,
    titulo: String,
    subtitulo: String,
    acento: Color,
    contenido: @Composable ColumnScope.() -> Unit
) {

    val forma = RoundedCornerShape(24.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = forma,
                ambientColor = Color(0x22004D3A),
                spotColor = Color(0x22004D3A)
            )
            .background(Color.White, forma)
            .border(1.dp, BordeCampo.copy(alpha = 0.7f), forma)
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(acento),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = numero.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column {
                Text(
                    text = titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
                Text(
                    text = subtitulo,
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        contenido()
    }
}

// ==========================================================
// TARJETA DE SECCIÓN SIMPLE (para sugerencias)
// ==========================================================

@Composable
private fun TarjetaSeccionFormulario(
    contenido: @Composable () -> Unit
) {

    val forma = RoundedCornerShape(24.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = forma,
                ambientColor = Color(0x22004D3A),
                spotColor = Color(0x22004D3A)
            )
            .background(Color.White, forma)
            .border(1.dp, BordeCampo.copy(alpha = 0.7f), forma)
            .padding(16.dp)
    ) {

        contenido()
    }
}

// ==========================================================
// CATEGORÍAS EN CHIPS (3 por fila)
// ==========================================================

@Composable
private fun CategoriasChips(
    seleccion: String?,
    error: Boolean,
    acento: Color,
    onSeleccion: (String) -> Unit
) {

    Column {

        categoriasFormulario.chunked(3).forEach { fila ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                fila.forEach { opcion ->

                    ChipCategoria(
                        opcion = opcion,
                        activa = seleccion == opcion,
                        errorSinSeleccion = error && seleccion == null,
                        acento = acento,
                        modifier = Modifier.weight(1f),
                        onClick = { onSeleccion(opcion) }
                    )
                }

                repeat(3 - fila.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (error && seleccion == null) {

            Text(
                text = "Selecciona una categoría",
                fontSize = 12.sp,
                color = Rojo
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun ChipCategoria(
    opcion: String,
    activa: Boolean,
    errorSinSeleccion: Boolean,
    acento: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {

    val forma = RoundedCornerShape(16.dp)

    val escala by animateFloatAsState(
        targetValue = if (activa) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = 0.45f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "escalaChip"
    )

    val fondo by animateColorAsState(
        targetValue = if (activa) acento else FondoCampo,
        animationSpec = tween(250),
        label = "fondoChip"
    )

    val borde by animateColorAsState(
        targetValue = when {
            activa -> acento
            errorSinSeleccion -> Rojo
            else -> BordeCampo
        },
        animationSpec = tween(250),
        label = "bordeChip"
    )

    val colorTexto by animateColorAsState(
        targetValue = if (activa) Color.White else TextoPrincipal,
        animationSpec = tween(250),
        label = "textoChip"
    )

    Column(
        modifier = modifier
            .height(66.dp)
            .scale(escala)
            .clip(forma)
            .background(fondo)
            .border(
                width = if (errorSinSeleccion) 1.5.dp else 1.dp,
                color = borde,
                shape = forma
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = emojisCategoria[opcion] ?: "📦",
            fontSize = 21.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = opcion,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colorTexto,
            maxLines = 1
        )
    }
}

// ==========================================================
// ETIQUETA
// ==========================================================

@Composable
private fun Etiqueta(
    texto: String,
    obligatorio: Boolean
) {

    Row {

        Text(
            text = texto,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )

        if (obligatorio) {

            Text(
                text = " *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Rojo
            )
        }
    }

    Spacer(
        modifier = Modifier.height(8.dp)
    )
}

// ==========================================================
// CAMPO DE TEXTO
// ==========================================================

@Composable
private fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean,
    error: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    ocuparAncho: Boolean = true,
    acento: Color = Verde
) {

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = if (ocuparAncho) {
            modifier.fillMaxWidth()
        } else {
            modifier
        },
        singleLine = singleLine,
        isError = error,
        keyboardOptions = keyboardOptions,

        placeholder = {

            Text(
                text = placeholder,
                fontSize = 14.sp,
                color = TextoClaro
            )
        },

        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 15.sp
        ),

        shape = RoundedCornerShape(14.dp),

        colors =
            OutlinedTextFieldDefaults.colors(

                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorTextColor = Color.Black,

                focusedBorderColor = acento,
                unfocusedBorderColor = BordeCampo,
                errorBorderColor = Rojo,

                cursorColor = acento,
                errorCursorColor = Rojo,

                focusedContainerColor = Color.White,
                unfocusedContainerColor = FondoCampo,
                errorContainerColor = FondoCampo
            )
    )
}

// ==========================================================
// CAMPO DESPLEGABLE
// ==========================================================

@Composable
private fun CampoDesplegable(
    placeholder: String,
    seleccion: String?,
    opciones: List<String>,
    iconoInicio: Int? = null,
    flechaAbajo: Boolean = true,
    error: Boolean = false,
    acento: Color = VerdeOscuro,
    onSeleccion: (String) -> Unit
) {

    var abierto by remember {
        mutableStateOf(false)
    }

    val forma = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(forma)
                .background(FondoCampo)
                .border(
                    width = if (error) 1.5.dp else 1.dp,
                    color = if (error) Rojo else BordeCampo,
                    shape = forma
                )
                .clickable {
                    abierto = true
                }
                .padding(
                    horizontal = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (iconoInicio != null) {

                Image(
                    painter = painterResource(
                        id = iconoInicio
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(acento)
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )
            }

            Text(
                text = seleccion ?: placeholder,
                modifier = Modifier.weight(1f),
                fontSize = 15.sp,
                color =
                    if (seleccion == null) {
                        TextoClaro
                    } else {
                        Color.Black
                    },
                maxLines = 1
            )

            Icon(
                imageVector =
                    if (flechaAbajo) {
                        Icons.Default.KeyboardArrowDown
                    } else {
                        Icons.AutoMirrored.Filled.KeyboardArrowRight
                    },
                contentDescription = null,
                tint = TextoSecundario,
                modifier = Modifier.size(24.dp)
            )
        }

        DropdownMenu(
            expanded = abierto,
            onDismissRequest = {
                abierto = false
            },
            modifier = Modifier.background(
                Color.White
            )
        ) {

            opciones.forEach { opcion ->

                DropdownMenuItem(

                    text = {

                        Text(
                            text = opcion,
                            fontSize = 15.sp,
                            fontWeight =
                                if (seleccion == opcion) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                            color =
                                if (seleccion == opcion) {
                                    acento
                                } else {
                                    TextoPrincipal
                                }
                        )
                    },

                    onClick = {

                        onSeleccion(opcion)

                        abierto = false
                    }
                )
            }
        }
    }
}

// ==========================================================
// MINI FOTO
// ==========================================================

@Composable
private fun MiniFoto(
    uri: Uri,
    onQuitar: () -> Unit
) {

    val contexto = LocalContext.current

    val bitmap = remember(uri) {

        runCatching {

            val opciones =
                BitmapFactory.Options().apply {
                    inSampleSize = 4
                }

            contexto.contentResolver
                .openInputStream(uri)
                ?.use {

                    BitmapFactory.decodeStream(
                        it,
                        null,
                        opciones
                    )
                }

        }.getOrNull()
    }

    // Entrada con rebote
    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        visible = true
    }

    val escalaEntrada by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "escalaMiniFoto"
    )

    Box(
        modifier = Modifier
            .size(84.dp)
            .scale(escalaEntrada)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEDEDED))
    ) {

        if (bitmap != null) {

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto del objeto",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // BOTÓN QUITAR
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(22.dp)
                .background(
                    Color(0xAA000000),
                    CircleShape
                )
                .clip(CircleShape)
                .clickable {
                    onQuitar()
                },
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Quitar foto",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

// ==========================================================
// COMPRIMIR FOTO A BASE64 (para guardarla dentro de Firestore)
// ==========================================================

private fun uriABase64(
    contexto: Context,
    uri: Uri
): String? {

    return runCatching {

        // 1) Leer solo las medidas para no cargar la foto completa
        val medidas =
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

        contexto.contentResolver
            .openInputStream(uri)
            ?.use {
                BitmapFactory.decodeStream(it, null, medidas)
            }

        var muestra = 1

        while (
            medidas.outWidth / muestra > 1600 ||
            medidas.outHeight / muestra > 1600
        ) {
            muestra *= 2
        }

        // 2) Decodificar reducida
        val opciones =
            BitmapFactory.Options().apply {
                inSampleSize = muestra
            }

        val original =
            contexto.contentResolver
                .openInputStream(uri)
                ?.use {
                    BitmapFactory.decodeStream(it, null, opciones)
                }
                ?: error("No se pudo leer la imagen")

        // 3) Ajustar a 800 px máximo por lado
        val escala =
            minOf(
                1f,
                800f / maxOf(original.width, original.height)
            )

        val reducida =
            if (escala < 1f) {
                Bitmap.createScaledBitmap(
                    original,
                    (original.width * escala).toInt(),
                    (original.height * escala).toInt(),
                    true
                )
            } else {
                original
            }

        // 4) JPEG calidad 60 -> Base64
        val salida = ByteArrayOutputStream()

        reducida.compress(
            Bitmap.CompressFormat.JPEG,
            60,
            salida
        )

        Base64.encodeToString(
            salida.toByteArray(),
            Base64.NO_WRAP
        )

    }.getOrNull()
}

// ==========================================================
// MODELO: REPORTE EXISTENTE (para coincidencias)
// ==========================================================

private data class ReporteExistente(
    val id: String,
    val titulo: String,
    val categoria: String,
    val descripcion: String,
    val ubicacion: String,
    val usuarioId: String,
    val fotoBase64: String?,
    val fechaMillis: Long,
    val whatsapp: String
)

// ==========================================================
// DOCUMENTO DE FIRESTORE -> ReporteExistente
// ==========================================================

private fun documentoAReporte(
    doc: DocumentSnapshot
): ReporteExistente {

    val fotos = doc.get("fotosBase64") as? List<*>

    return ReporteExistente(
        id = doc.id,
        titulo = doc.getString("titulo").orEmpty(),
        categoria = doc.getString("categoria").orEmpty(),
        descripcion = doc.getString("descripcion").orEmpty(),
        ubicacion = doc.getString("ubicacionAprox").orEmpty(),
        usuarioId = doc.getString("usuarioId").orEmpty(),
        fotoBase64 = fotos?.firstOrNull() as? String,
        whatsapp = doc.getString("whatsapp").orEmpty(),
        fechaMillis =
            doc.getTimestamp("fecha")
                ?.toDate()
                ?.time
                ?: System.currentTimeMillis()
    )
}

// ==========================================================
// BUSCAR COINCIDENCIAS
//
// Reglas:
//  - Solo se sugiere si el nombre que escribes comparte al
//    menos una palabra con el título del otro reporte.
//  - Misma categoría y misma ubicación suben la puntuación.
//  - Máximo 3 sugerencias, las más parecidas primero.
//  - Tus propios reportes no se sugieren.
// ==========================================================

private val palabrasIgnoradas = setOf(
    "con", "del", "los", "las", "una", "uno",
    "por", "para", "que", "mis", "sus"
)

private fun normalizar(texto: String): String {

    return Normalizer
        .normalize(texto.lowercase(), Normalizer.Form.NFD)
        .replace(
            Regex("\\p{InCombiningDiacriticalMarks}+"),
            ""
        )
}

private fun palabrasClave(texto: String): Set<String> {

    return normalizar(texto)
        .split(Regex("[^a-z0-9]+"))
        .filter {
            it.length >= 3 && it !in palabrasIgnoradas
        }
        .toSet()
}

private fun buscarCoincidencias(
    reportes: List<ReporteExistente>,
    nombre: String,
    categoria: String?,
    ubicacion: String?,
    miUid: String
): List<ReporteExistente> {

    val palabras = palabrasClave(nombre)

    if (palabras.isEmpty()) {
        return emptyList()
    }

    return reportes
        .filter {
            it.whatsapp.length == 10 &&
                    (PERMITIR_RECLAMAR_PROPIOS || it.usuarioId != miUid)
        }
        .mapNotNull { reporte ->

            val enComun =
                palabras
                    .intersect(palabrasClave(reporte.titulo))
                    .size

            if (enComun == 0) {

                null

            } else {

                var puntos = enComun * 2

                if (
                    categoria != null &&
                    reporte.categoria == categoria
                ) {
                    puntos += 1
                }

                if (
                    ubicacion != null &&
                    reporte.ubicacion == ubicacion
                ) {
                    puntos += 1
                }

                Pair(reporte, puntos)
            }
        }
        .sortedWith(
            compareByDescending<Pair<ReporteExistente, Int>> {
                it.second
            }.thenByDescending {
                it.first.fechaMillis
            }
        )
        .take(3)
        .map { it.first }
}

// ==========================================================
// TARJETA DE REPORTE SUGERIDO
// ==========================================================

@Composable
private fun TarjetaCoincidencia(
    reporte: ReporteExistente,
    textoBoton: String,
    onReclamar: () -> Unit
) {

    val forma = RoundedCornerShape(18.dp)

    val bitmap =
        remember(reporte.fotoBase64) {

            reporte.fotoBase64?.let { texto ->

                runCatching {

                    val bytes =
                        Base64.decode(texto, Base64.DEFAULT)

                    BitmapFactory.decodeByteArray(
                        bytes,
                        0,
                        bytes.size,
                        BitmapFactory.Options().apply {
                            inSampleSize = 2
                        }
                    )

                }.getOrNull()
            }
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                FondoCampo,
                forma
            )
            .border(
                1.dp,
                BordeCampo,
                forma
            )
            .padding(12.dp)
    ) {

        Row(
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(FondoGris),
                contentAlignment = Alignment.Center
            ) {

                if (bitmap != null) {

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = reporte.titulo,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Text(
                        text = "📦",
                        fontSize = 26.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = reporte.titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text =
                        listOf(
                            reporte.categoria,
                            reporte.ubicacion
                        ).filter {
                            it.isNotBlank()
                        }.joinToString(" • "),
                    fontSize = 12.sp,
                    color = VerdeOscuro,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (reporte.descripcion.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = reporte.descripcion,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = TextoSecundario,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = tiempoRelativo(reporte.fechaMillis),
                    fontSize = 11.sp,
                    color = TextoClaro
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        BotonCoincidenciaAnimado(
            texto = textoBoton,
            onClick = onReclamar
        )
    }
}

@Composable
private fun BotonCoincidenciaAnimado(
    texto: String,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val presionado by interactionSource.collectIsPressedAsState()

    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "escalaCoincidencia"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .scale(escala)
            .clip(RoundedCornerShape(13.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(VerdeOscuro, Verde)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// ==========================================================
// DIÁLOGO: MENSAJE PARA QUIEN PUBLICÓ EL REPORTE
// Al enviar, se registra la coincidencia y se abre WhatsApp
// directo al número del reporte con este mensaje listo.
// ==========================================================

@Composable
private fun DialogoCoincidencia(
    reporte: ReporteExistente,
    usuarioPerdio: Boolean,
    enviando: Boolean,
    onCancelar: () -> Unit,
    onEnviar: (String) -> Unit
) {

    var mensaje by remember {
        mutableStateOf("")
    }

    var intento by remember {
        mutableStateOf(false)
    }

    val mensajeInvalido =
        mensaje.trim().length < 5

    AlertDialog(

        onDismissRequest = {

            if (!enviando) {
                onCancelar()
            }
        },

        containerColor = Color.White,

        title = {

            Text(
                text =
                    if (usuarioPerdio) {
                        "Confirmar que es tuyo"
                    } else {
                        "Confirmar que lo encontraste"
                    },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
        },

        text = {

            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                )
            ) {

                Text(
                    text = reporte.titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Etiqueta(
                    texto =
                        if (usuarioPerdio) {
                            "¿Por qué es tuyo?"
                        } else {
                            "¿Dónde lo tienes?"
                        },
                    obligatorio = true
                )

                CampoTexto(
                    valor = mensaje,
                    onCambio = {

                        if (it.length <= LIMITE_DESCRIPCION) {
                            mensaje = it
                        }
                    },
                    placeholder =
                        if (usuarioPerdio) {
                            "Algo que solo el dueño sabría (marcas, contenido...)"
                        } else {
                            "Ej. Lo tengo en la biblioteca, mostrador"
                        },
                    modifier = Modifier.height(100.dp),
                    singleLine = false,
                    error = intento && mensajeInvalido
                )

                if (intento && mensajeInvalido) {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Escribe al menos 5 caracteres",
                        fontSize = 12.sp,
                        color = Rojo
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "💬 Se abrirá WhatsApp con tu mensaje listo para enviarlo a quien publicó el reporte.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = TextoSecundario
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    intento = true

                    if (!mensajeInvalido && !enviando) {
                        onEnviar(mensaje.trim())
                    }
                },
                enabled = !enviando
            ) {

                Text(
                    text =
                        if (enviando) {
                            "Enviando..."
                        } else {
                            "Abrir WhatsApp"
                        },
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onCancelar,
                enabled = !enviando
            ) {

                Text(
                    text = "Cancelar",
                    color = TextoSecundario
                )
            }
        }
    )
}

// ==========================================================
// GUARDAR LA COINCIDENCIA EN FIRESTORE
//
// Transacción para que dos personas no reclamen el mismo
// reporte a la vez. Actualiza el reporte original:
//   - estado -> "En proceso de recuperación"
//   - reclamadoPor / encontradoPor + fecha
//   - numContactos + 1
// y guarda el mensaje en reportes/{id}/contactos/{auto}.
// ==========================================================

private fun registrarCoincidencia(
    reporteId: String,
    estadoEsperado: String,
    usuarioPerdio: Boolean,
    mensaje: String,
    onExito: () -> Unit,
    onError: (String) -> Unit
) {

    val usuarioId =
        FirebaseAuth.getInstance().currentUser?.uid

    if (usuarioId == null) {

        onError("Debes iniciar sesión para continuar")

        return
    }

    val db = FirebaseFirestore.getInstance()

    val reporteRef =
        db.collection("reportes").document(reporteId)

    val contactoRef =
        reporteRef.collection("contactos").document()

    val tipoContacto =
        if (usuarioPerdio) "reclamo" else "hallazgo"

    val campos: Map<String, Any> =
        if (usuarioPerdio) {

            mapOf(
                "estado" to "En proceso de recuperación",
                "reclamadoPor" to usuarioId,
                "fechaReclamo" to FieldValue.serverTimestamp(),
                "numContactos" to FieldValue.increment(1)
            )

        } else {

            mapOf(
                "estado" to "En proceso de recuperación",
                "encontradoPor" to usuarioId,
                "fechaHallazgo" to FieldValue.serverTimestamp(),
                "numContactos" to FieldValue.increment(1)
            )
        }

    db.runTransaction { transaction ->

        val actual = transaction.get(reporteRef)

        if (
            !actual.exists() ||
            actual.getString("estado") != estadoEsperado
        ) {

            throw FirebaseFirestoreException(
                "Este reporte ya fue atendido por alguien más",
                FirebaseFirestoreException.Code.FAILED_PRECONDITION
            )
        }

        transaction.update(reporteRef, campos)

        transaction.set(
            contactoRef,
            mapOf(
                "usuarioId" to usuarioId,
                "tipo" to tipoContacto,
                "mensaje" to mensaje,
                "fecha" to FieldValue.serverTimestamp()
            )
        )

        null

    }.addOnSuccessListener {

        onExito()

    }.addOnFailureListener { e ->

        onError(
            e.localizedMessage
                ?: "No se pudo completar la acción"
        )
    }
}

// ==========================================================
// ABRIR WHATSAPP DIRECTO CON UN MENSAJE
// Número de México: 52 + 10 dígitos.
// ==========================================================

private fun abrirWhatsApp(
    contexto: Context,
    telefono: String,
    texto: String
) {

    val intent =
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "https://wa.me/52$telefono?text=${Uri.encode(texto)}"
            )
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

// ==========================================================
// TIEMPO RELATIVO ("Hace 5 min")
// ==========================================================

private fun tiempoRelativo(millis: Long): String {

    val minutos =
        (System.currentTimeMillis() - millis) / 60000

    return when {

        minutos < 1 -> "Hace un momento"

        minutos < 60 -> "Hace $minutos min"

        minutos < 1440 -> "Hace ${minutos / 60} h"

        else -> "Hace ${minutos / 1440} d"
    }
}