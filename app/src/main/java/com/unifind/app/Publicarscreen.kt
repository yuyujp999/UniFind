package com.unifind.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.speech.RecognizerIntent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.core.content.ContextCompat
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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
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
import java.util.Locale

// ==========================================================
// UniFind - Pantalla Publicar
// Nota: AndroidManifest.xml debe declarar ACCESS_FINE_LOCATION y
// ACCESS_COARSE_LOCATION. La app solicita el permiso en tiempo de ejecución.
// ==========================================================

// ==========================================================
// COLORES UNIFIND
// ==========================================================

private val Verde = Color(0xFF008F63)
private val VerdeOscuro = Color(0xFF006B4A)
private val VerdeClaro = Color(0xFFE2F6EB)
private val VerdeBanner = Color(0xFF004D3A)

private val Rojo = Color(0xFFF28C28)
private val RojoOscuro = Color(0xFFD96B0B)
private val RojoClaro = Color(0xFFFFF1E2)

private val FondoPrincipal = Color(0xFFE8F7EF)
private val FondoCampo = Color(0xFFF6F8F8)
private val FondoGris = Color(0xFFF0F3F3)

private val BordeCampo = Color(0xFFE0E5E3)

private val TextoPrincipal = Color(0xFF202124)
private val TextoSecundario = Color(0xFF5F6368)
private val TextoClaro = Color(0xFF8A9094)

private const val LIMITE_DESCRIPCION = 200

private val FondoModal = Color(0x66000000)

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

private fun obtenerUbicacionActual(
    contexto: Context,
    scope: kotlinx.coroutines.CoroutineScope,
    onCargando: (Boolean) -> Unit,
    onUbicacion: (direccion: String, latitud: Double, longitud: Double) -> Unit,
    onError: (String) -> Unit
) {
    val tienePermisoFino = ContextCompat.checkSelfPermission(
        contexto, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val tienePermisoAproximado = ContextCompat.checkSelfPermission(
        contexto, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!tienePermisoFino && !tienePermisoAproximado) {
        onError("Concede el permiso de ubicación para detectar dónde estás.")
        return
    }

    val locationManager =
        contexto.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    onCargando(true)

    fun procesarUbicacion(location: android.location.Location?) {
        if (location == null) {
            onCargando(false)
            onError("No se obtuvo una posición GPS. Activa la ubicación, sal a un lugar con mejor señal e inténtalo de nuevo.")
            return
        }

        scope.launch {
            val direccion = withContext(Dispatchers.IO) {
                runCatching {
                    if (!Geocoder.isPresent()) return@runCatching null
                    @Suppress("DEPRECATION")
                    Geocoder(contexto, Locale("es", "MX"))
                        .getFromLocation(location.latitude, location.longitude, 1)
                        ?.firstOrNull()
                        ?.getAddressLine(0)
                }.getOrNull()
            }

            val textoUbicacion = direccion?.takeIf { it.isNotBlank() }
                ?: "Ubicación GPS (${String.format(Locale.US, "%.5f", location.latitude)}, ${String.format(Locale.US, "%.5f", location.longitude)})"

            onUbicacion(textoUbicacion, location.latitude, location.longitude)
            onCargando(false)
        }
    }

    try {
        val proveedores = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        ).filter { proveedor ->
            runCatching { locationManager.isProviderEnabled(proveedor) }
                .getOrDefault(false)
        }

        if (proveedores.isEmpty()) {
            onCargando(false)
            onError("Activa la ubicación del teléfono desde los ajustes rápidos e inténtalo de nuevo.")
            return
        }

        // En Android 11 o posterior solicita una posición actual y, si tarda o falla,
        // intenta recuperar la última posición conocida disponible.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val proveedorActual = proveedores.firstOrNull {
                it == LocationManager.GPS_PROVIDER
            } ?: proveedores.first()

            val ultimaConocida = proveedores.mapNotNull { proveedor ->
                runCatching { locationManager.getLastKnownLocation(proveedor) }.getOrNull()
            }.maxByOrNull { it.time }

            locationManager.getCurrentLocation(
                proveedorActual,
                null,
                contexto.mainExecutor
            ) { location ->
                procesarUbicacion(location ?: ultimaConocida)
            }
        } else {
            val location = proveedores.mapNotNull { proveedor ->
                runCatching { locationManager.getLastKnownLocation(proveedor) }.getOrNull()
            }.maxByOrNull { it.time }

            procesarUbicacion(location)
        }
    } catch (_: SecurityException) {
        onCargando(false)
        onError("Android no permitió acceder a la ubicación. Revisa los permisos de UniFind.")
    } catch (_: Exception) {
        onCargando(false)
        onError("No se pudo obtener la ubicación. Comprueba que el GPS esté activado.")
    }
}


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

    var latitud by remember { mutableStateOf<Double?>(null) }
    var longitud by remember { mutableStateOf<Double?>(null) }
    var ubicacionCargando by remember { mutableStateOf(false) }

    // El teléfono se obtiene del perfil de la cuenta autenticada, nunca de
    // SharedPreferences compartidas entre cuentas. Si el perfil no tiene teléfono,
    // el campo permanece vacío.
    var whatsapp by remember(miUid) { mutableStateOf("") }

    LaunchedEffect(miUid) {
        // Vaciar inmediatamente el valor anterior al cambiar de cuenta.
        whatsapp = ""
        if (miUid.isNotBlank()) {
            runCatching {
                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(miUid)
                    .get()
                    .addOnSuccessListener { documento ->
                        // Acepta nombres habituales de campo para adaptarse al perfil existente.
                        val telefonoPerfil = sequenceOf(
                            documento.getString("telefono"),
                            documento.getString("numeroTelefono"),
                            documento.getString("telefonoContacto"),
                            documento.getString("whatsapp"),
                            documento.getString("numeroWhatsApp"),
                            documento.getString("phone")
                        ).firstOrNull { !it.isNullOrBlank() }.orEmpty()

                        val digitos = telefonoPerfil.filter(Char::isDigit)
                        // Si se guardó con +52, quita el prefijo para dejar solo el número local.
                        val numeroLocal = when {
                            digitos.length == 12 && digitos.startsWith("52") -> digitos.drop(2)
                            digitos.length == 13 && digitos.startsWith("521") -> digitos.drop(3)
                            digitos.length > 10 -> digitos.takeLast(10)
                            else -> digitos
                        }
                        whatsapp = numeroLocal.take(10)
                    }
                    .addOnFailureListener {
                        // Si no se puede leer el perfil, no reutilizar el número de otra cuenta.
                        whatsapp = ""
                    }
            }.onFailure {
                whatsapp = ""
            }
        }
    }

    var intentoPublicar by remember {
        mutableStateOf(false)
    }

    var publicando by remember {
        mutableStateOf(false)
    }

    var avisoPublicacion by remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()

    val lanzadorPermisosUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (concedido) {
            obtenerUbicacionActual(
                contexto = contexto,
                scope = scope,
                onCargando = { ubicacionCargando = it },
                onUbicacion = { direccion, lat, lon ->
                    ubicacion = direccion
                    latitud = lat
                    longitud = lon
                },
                onError = { mensaje ->
                    Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show()
                }
            )
        } else {
            Toast.makeText(
                contexto,
                "Necesitamos permiso de ubicación para detectar dónde estabas.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val solicitarUbicacion: () -> Unit = {
        val tieneUbicacion =
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        contexto,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

        if (tieneUbicacion) {
            obtenerUbicacionActual(
                contexto = contexto,
                scope = scope,
                onCargando = { ubicacionCargando = it },
                onUbicacion = { direccion, lat, lon ->
                    ubicacion = direccion
                    latitud = lat
                    longitud = lon
                },
                onError = { mensaje ->
                    Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show()
                }
            )
        } else {
            lanzadorPermisosUbicacion.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val abrirMapa: () -> Unit = {
        val lat = latitud
        val lon = longitud
        val destino = if (lat != null && lon != null) {
            "$lat,$lon"
        } else {
            ubicacion?.takeIf { it.isNotBlank() } ?: "mi ubicación"
        }

        // Primero intenta abrir una app de mapas con geo:, después Google Maps web.
        val geoUri = if (lat != null && lon != null) {
            Uri.parse("geo:$lat,$lon?q=$lat,$lon(UniFind)")
        } else {
            Uri.parse("geo:0,0?q=${Uri.encode(destino)}")
        }
        val intentGeo = Intent(Intent.ACTION_VIEW, geoUri)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        runCatching {
            contexto.startActivity(intentGeo)
        }.onFailure {
            runCatching {
                val query = if (lat != null && lon != null) "$lat,$lon" else destino
                val urlMapa = Uri.parse(
                    "https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}"
                )
                contexto.startActivity(
                    Intent(Intent.ACTION_VIEW, urlMapa)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }.onFailure {
                Toast.makeText(
                    contexto,
                    "No se pudo abrir el mapa. Comprueba tu conexión e intenta de nuevo.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    LaunchedEffect(avisoPublicacion) {
        if (avisoPublicacion != null) {
            delay(3500)
            avisoPublicacion = null
        }
    }

    // Todos los campos se muestran desde el inicio; el desplazamiento ocurre únicamente dentro del formulario.
    val entro = true
    val nombreOk = nombre.isNotBlank()
    val categoriaOk = categoria != null
    val ubicacionOk = ubicacion != null
    val whatsappOk = whatsapp.length == 10
    val completados = listOf(nombreOk, categoriaOk, ubicacionOk, whatsappOk).count { it }

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

    // Cámara: captura una foto y la guarda en caché para usar el mismo flujo Base64.
    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            if (fotos.size >= 3) {
                Toast.makeText(contexto, "Máximo 3 fotos", Toast.LENGTH_SHORT).show()
            } else {
                runCatching {
                    val archivo = java.io.File.createTempFile("unifind_camera_", ".jpg", contexto.cacheDir)
                    archivo.outputStream().use { salida ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 88, salida)
                    }
                    fotos.add(Uri.fromFile(archivo))
                }.onFailure {
                    Toast.makeText(contexto, "No se pudo guardar la foto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Dictado: el reconocimiento de voz del dispositivo transcribe la descripción.
    val lanzadorVoz = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == android.app.Activity.RESULT_OK) {
            val textoVoz = resultado.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!textoVoz.isNullOrBlank()) {
                descripcion = listOf(descripcion.trim(), textoVoz.trim())
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                    .take(LIMITE_DESCRIPCION)
            }
        }
    }

    val dictarDescripcion: () -> Unit = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe el objeto")
        }
        runCatching {
            lanzadorVoz.launch(intent)
        }.onFailure {
            Toast.makeText(contexto, "El dispositivo no tiene reconocimiento de voz disponible", Toast.LENGTH_LONG).show()
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
                            "latitud" to (latitud ?: 0.0),
                            "longitud" to (longitud ?: 0.0),
                            "estado" to estadoTexto,
                            "fecha" to FieldValue.serverTimestamp(),
                            "imagenUrl" to "",
                            "fotosBase64" to fotosBase64,
                            "usuarioId" to usuarioId,
                            "numContactos" to 0,
                            // El teléfono se guarda solo con los 10 dígitos del número.
                            "whatsapp" to whatsapp.filter { it.isDigit() }.take(10),
                            // La lada se guarda por reporte para no depender de la selección global posterior.
                            "whatsappLada" to (contexto.getSharedPreferences("unifind", Context.MODE_PRIVATE)
                                .getString("whatsapp_lada", "+52") ?: "+52")
                        )

                        FirebaseFirestore.getInstance()
                            .collection("reportes")
                            .add(reporte)
                            .addOnSuccessListener {

                                publicando = false

                                // Limpiar el formulario
                                nombre = ""
                                descripcion = ""
                                categoria = null
                                ubicacion = null
                                latitud = null
                                longitud = null
                                intentoPublicar = false
                                fotos.clear()

                                // Aviso visual dentro de UniFind; no usa notificaciones Android.
                                avisoPublicacion = if (esPerdido) {
                                    "Tu reporte de objeto perdido se publicó correctamente."
                                } else {
                                    "Tu reporte de objeto encontrado se publicó correctamente."
                                }

                                scope.launch {
                                    delay(2200)
                                    onTerminar()
                                }
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal)
    ) {

        // Espaciado superior del formulario.
        val espacioLogo = 54.dp

        // Fondo limpio: se elimina el banner que tapaba y ensuciaba el formulario.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF4FBF7), Color(0xFFE7F5ED))
                    )
                )
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
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {

                // ==============================================
                // CONTENIDO (encabezado + formulario)
                // ==============================================

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(horizontal = 16.dp)
                ) {

                    Spacer(modifier = Modifier.height(espacioLogo))

                    Text(
                        text = "Publicar",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp, top = 2.dp, bottom = 2.dp),
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.4).sp,
                        color = Color(0xFF171717),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Divider(
                        modifier = Modifier.padding(horizontal = 1.dp, vertical = 2.dp),
                        thickness = 1.dp,
                        color = Color(0xFFE0E7E2)
                    )

                    SelectorTipoPublicacion(
                        tipo = tipo,
                        colorIndicador = colorPrincipal,
                        onCambio = { tipo = it }
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    ProgresoFormulario(
                        completados = completados,
                        total = 4,
                        color = colorPrincipal
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "* Campos obligatorios",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF171717),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Aparece(visible = entro, retraso = 360) {

                        Column {

                            FormularioSecciones(
                                acento = colorPrincipal,
                                acentoClaro = colorClaro,
                                fotos = fotos,
                                abrirGaleria = abrirGaleria,
                                abrirCamara = {
                                    if (fotos.size >= 3) {
                                        Toast.makeText(contexto, "Máximo 3 fotos", Toast.LENGTH_SHORT).show()
                                    } else {
                                        lanzadorCamara.launch(null)
                                    }
                                },
                                dictarDescripcion = dictarDescripcion,
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
                                mostrarCategoria = true,
                                mostrarDetalles = true,
                                mostrarContacto = true,
                                latitud = latitud,
                                longitud = longitud,
                                ubicacionCargando = ubicacionCargando,
                                obtenerUbicacion = solicitarUbicacion,
                                abrirMapa = abrirMapa
                            )

                            // ======================================
                            // LISTA DE OBJETOS PERDIDOS
                            // Cuando se elige "Encontré algo", muestra
                            // reportes activos para vincular el hallazgo
                            // con el reporte original y evitar duplicados.
                            // ======================================

                            Revela(visible = !esPerdido) {
                                Column {
                                    Spacer(modifier = Modifier.height(9.dp))

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
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = null,
                                                    tint = VerdeOscuro,
                                                    modifier = Modifier.size(21.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Objetos reportados como perdidos",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextoPrincipal
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "Si encuentras uno de estos objetos, selecciónalo para indicar dónde lo encontraste.",
                                                    fontSize = 12.sp,
                                                    lineHeight = 17.sp,
                                                    color = TextoSecundario
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // No exigir WhatsApp para mostrar el reporte:
                                        // los reportes antiguos pueden tener el contacto
                                        // en la subcolección contactos y no en el documento raíz.
                                        val reportesPerdidosActivos = reportesOpuestos
                                            .filter { reporte ->
                                                reporte.usuarioId.isNotBlank() &&
                                                        reporte.usuarioId != miUid &&
                                                        reporte.id.isNotBlank()
                                            }
                                            .distinctBy { it.id }
                                            .sortedByDescending { it.fechaMillis }

                                        if (reportesPerdidosActivos.isEmpty()) {
                                            Text(
                                                text = "Por ahora no hay objetos perdidos disponibles para vincular. Puedes continuar con el formulario y publicar el objeto encontrado como un reporte nuevo.",
                                                fontSize = 13.sp,
                                                lineHeight = 19.sp,
                                                color = TextoSecundario
                                            )
                                        } else {
                                            reportesPerdidosActivos.forEachIndexed { index, reporte ->
                                                TarjetaCoincidencia(
                                                    reporte = reporte,
                                                    textoBoton = "Lo encontré",
                                                    onReclamar = {
                                                        seleccionado = reporte
                                                    }
                                                )
                                                if (index < reportesPerdidosActivos.lastIndex) {
                                                    Spacer(modifier = Modifier.height(9.dp))
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "¿No aparece? Continúa con el formulario y pulsa «Publicar objeto encontrado» para crear un reporte independiente.",
                                                fontSize = 12.sp,
                                                lineHeight = 17.sp,
                                                color = TextoSecundario
                                            )
                                        }
                                    }
                                }
                            }

                            // ======================================
                            // SUGERENCIAS PARA OBJETOS PERDIDOS POR EL USUARIO
                            // ======================================

                            Revela(visible = esPerdido && sugerencias.isNotEmpty()) {

                                Column {

                                    Spacer(modifier = Modifier.height(9.dp))

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
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = null,
                                                    tint = VerdeOscuro,
                                                    modifier = Modifier.size(21.dp)
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

                            Spacer(modifier = Modifier.height(7.dp))
                        }
                    }
                }

                // ==============================================
                // BARRA INFERIOR CON EL BOTÓN PUBLICAR
                // ==============================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(
                            horizontal = 16.dp,
                            vertical = 7.dp
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

        avisoPublicacion?.let { mensajeAviso ->
            AvisoPublicacion(
                mensaje = mensajeAviso,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = 58.dp,
                        end = 12.dp,
                        start = 52.dp
                    )
            )
        }

        // ======================================================
        // DIÁLOGO: CONFIRMAR COINCIDENCIA (teléfono + mensaje)
        // ======================================================

        val reporteElegido = seleccionado

        if (reporteElegido != null) {

            ModalCoincidenciaDesdeAbajo(
                reporte = reporteElegido,
                usuarioPerdio = esPerdido,
                enviando = enviando,
                onCancelar = { seleccionado = null },
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
                            val saludo = if (esPerdido) {
                                "Hola, vi tu reporte del objeto encontrado \"${reporteElegido.titulo}\" en UniFind y creo que es mío."
                            } else {
                                "Hola, vi tu reporte del objeto perdido \"${reporteElegido.titulo}\" en UniFind y creo que lo encontré."
                            }
                            abrirWhatsApp(
                                contexto,
                                reporteElegido.whatsapp,
                                "$saludo $mensaje",
                                reporteElegido.whatsappLada
                            )
                            onTerminar()
                        },
                        onError = { motivo ->
                            enviando = false
                            Toast.makeText(contexto, motivo, Toast.LENGTH_LONG).show()
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
    // Se conserva la firma para no afectar llamadas existentes,
    // pero el contenido aparece inmediatamente, sin animación de entrada.
    if (visible) {
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
            color = Color(0xFF171717)
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
                texto = "Perdí algo",
                icono = Icons.Default.Search,
                seleccionado = tipo == "perdido",
                modifier = Modifier.weight(1f),
                onClick = {
                    onCambio("perdido")
                }
            )

            OpcionTipo(
                texto = "Encontré algo",
                icono = Icons.Default.Add,
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
    icono: androidx.compose.ui.graphics.vector.ImageVector,
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
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colorTexto,
            maxLines = 1
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
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
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
// CARD PRINCIPAL DEL FORMULARIO
// Entra desde abajo y mantiene todo el contenido ordenado.
// ==========================================================

@Composable
private fun TarjetaFormularioDesdeAbajo(
    acento: Color,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, forma)
            .clip(forma)
            .background(Color.White)
            .border(1.dp, BordeCampo.copy(alpha = 0.8f), forma)
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier.width(42.dp).height(5.dp).clip(CircleShape)
                .background(acento.copy(alpha = 0.28f)).align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(10.dp))
        contenido()
    }
}

// ==========================================================
// FORMULARIO: 4 SECCIONES NUMERADAS
// Los campos se muestran dentro de la tarjeta con scroll interno.
// ==========================================================

@Composable
private fun FormularioSecciones(
    acento: Color,
    acentoClaro: Color,
    fotos: List<Uri>,
    abrirGaleria: () -> Unit,
    abrirCamara: () -> Unit,
    dictarDescripcion: () -> Unit,
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
    mostrarContacto: Boolean,
    latitud: Double?,
    longitud: Double?,
    ubicacionCargando: Boolean,
    obtenerUbicacion: () -> Unit,
    abrirMapa: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SeccionTitulo(
            numero = 1,
            titulo = "Fotos",
            subtitulo = "Opcional · hasta 3 imágenes",
            acento = acento
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (fotos.isEmpty()) {
                AccionFoto(
                    modifier = Modifier.weight(1f),
                    icono = Icons.Default.CameraAlt,
                    titulo = "Tomar foto",
                    subtitulo = "Usar cámara",
                    acento = acento,
                    fondo = acentoClaro,
                    onClick = abrirCamara
                )
                AccionFoto(
                    modifier = Modifier.weight(1f),
                    icono = Icons.Default.PhotoLibrary,
                    titulo = "Galería",
                    subtitulo = "Elegir imágenes",
                    acento = acento,
                    fondo = FondoCampo,
                    onClick = abrirGaleria
                )
            } else {
                fotos.forEach { uri ->
                    MiniFoto(uri = uri, onQuitar = { quitarFoto(uri) })
                }
                if (fotos.size < 3) {
                    AccionFoto(
                        modifier = Modifier.size(84.dp),
                        icono = Icons.Default.CameraAlt,
                        titulo = "Otra",
                        subtitulo = "Cámara",
                        acento = acento,
                        fondo = acentoClaro,
                        onClick = abrirCamara
                    )
                }
            }
        }

        if (fotos.isNotEmpty()) {
            Text(
                text = "${fotos.size}/3 fotos",
                fontSize = 10.sp,
                color = TextoSecundario
            )
        }

        Divider(
            modifier = Modifier.padding(horizontal = 1.dp),
            thickness = 1.dp,
            color = Color(0xFFE0E7E2)
        )

        SeccionTitulo(
            numero = 2,
            titulo = "Detalles",
            subtitulo = "Del objeto",
            acento = acento
        )

        Etiqueta("Nombre", obligatorio = true)
        CampoTexto(
            valor = nombre,
            onCambio = onNombre,
            placeholder = ejemploNombre,
            modifier = Modifier.height(54.dp),
            singleLine = true,
            error = intentoPublicar && nombre.isBlank(),
            acento = acento
        )

        Spacer(Modifier.height(2.dp))
        Etiqueta("Categoría", obligatorio = true)
        CampoDesplegable(
            placeholder = "Selecciona una categoría",
            seleccion = categoria,
            opciones = categoriasFormulario,
            iconoInicio = null,
            flechaAbajo = true,
            error = intentoPublicar && categoria == null,
            acento = acento,
            onSeleccion = onCategoria
        )

        Spacer(Modifier.height(2.dp))
        Etiqueta("Descripción", obligatorio = false)
        CampoTextoConDictado(
            valor = descripcion,
            onCambio = { if (it.length <= LIMITE_DESCRIPCION) onDescripcion(it) },
            placeholder = "Color, marcas, tamaño y otros detalles...",
            dictar = dictarDescripcion,
            acento = acento
        )
        Text(
            text = "${descripcion.length}/$LIMITE_DESCRIPCION",
            modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
            fontSize = 10.sp,
            color = TextoClaro,
            textAlign = TextAlign.End
        )

        Divider(
            modifier = Modifier.padding(horizontal = 1.dp),
            thickness = 1.dp,
            color = Color(0xFFE0E7E2)
        )

        SeccionTitulo(
            numero = 3,
            titulo = "Ubicación y contacto",
            subtitulo = "Detecta dónde estabas y permite contactarte",
            acento = acento
        )

        Etiqueta("Ubicación exacta", obligatorio = true)
        CampoUbicacion(
            direccion = ubicacion,
            latitud = latitud,
            longitud = longitud,
            cargando = ubicacionCargando,
            error = intentoPublicar && ubicacion == null,
            acento = acento,
            onObtenerUbicacion = obtenerUbicacion,
            onAbrirMapa = abrirMapa
        )

        Spacer(Modifier.height(2.dp))
        Etiqueta("WhatsApp · 10 dígitos", obligatorio = true)
        CampoWhatsApp(
            valor = whatsapp,
            onCambio = onWhatsapp,
            error = intentoPublicar && whatsapp.length != 10,
            acento = acento
        )
        Spacer(Modifier.height(2.dp))
        Text(
            "Lo usarán únicamente para ponerse en contacto contigo sobre este reporte.",
            fontSize = 11.sp,
            lineHeight = 15.sp,
            color = TextoSecundario
        )

        Divider(
            modifier = Modifier.padding(horizontal = 1.dp),
            thickness = 1.dp,
            color = Color(0xFFE0E7E2)
        )
    }
}

@Composable
private fun SeccionTitulo(
    numero: Int,
    titulo: String,
    subtitulo: String,
    acento: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(30.dp).clip(CircleShape).background(acento),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = numero.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.1).sp,
                    color = TextoPrincipal
                )
                Text(
                    text = subtitulo,
                    fontSize = 11.sp,
                    color = TextoSecundario
                )
            }
        }
    }
}

@Composable
private fun CampoTextoConDictado(
    valor: String,
    onCambio: (String) -> Unit,
    placeholder: String,
    dictar: () -> Unit,
    acento: Color
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = Modifier.fillMaxWidth().height(96.dp),
        singleLine = false,
        placeholder = {
            Text(placeholder, fontSize = 14.sp, color = TextoClaro)
        },
        trailingIcon = {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(acentoClaroPara(acento))
                    .clickable(onClick = dictar),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Dictar descripción",
                    tint = acento,
                    modifier = Modifier.size(21.dp)
                )
            }
        },
        textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedBorderColor = acento,
            unfocusedBorderColor = BordeCampo,
            cursorColor = acento,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = FondoCampo
        )
    )
}

private fun acentoClaroPara(acento: Color): Color =
    if (acento == Rojo || acento == RojoOscuro) RojoClaro else VerdeClaro

@Composable
private fun CampoUbicacion(
    direccion: String?,
    latitud: Double?,
    longitud: Double?,
    cargando: Boolean,
    error: Boolean,
    acento: Color,
    onObtenerUbicacion: () -> Unit,
    onAbrirMapa: () -> Unit
) {
    val forma = RoundedCornerShape(14.dp)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(forma)
                .background(Color.White)
                .border(
                    if (error) 1.5.dp else 1.dp,
                    if (error) Rojo else Color(0xFFD8E2DC),
                    forma
                )
                .clickable(enabled = !cargando, onClick = onObtenerUbicacion)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = acento,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(9.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = direccion ?: "Detectar mi ubicación actual",
                    fontSize = 14.sp,
                    color = if (direccion == null) TextoClaro else TextoPrincipal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (latitud != null && longitud != null) {
                    Text(
                        text = "Ubicación GPS guardada",
                        fontSize = 10.sp,
                        color = acento
                    )
                }
            }

            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(21.dp),
                    color = acento,
                    strokeWidth = 2.dp
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(acentoClaroPara(acento)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Detectar ubicación",
                        tint = acento,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(7.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Usa el GPS del teléfono para marcar dónde estabas.",
                modifier = Modifier.weight(1f),
                fontSize = 10.sp,
                color = TextoSecundario
            )

            TextButton(
                onClick = onAbrirMapa,
                enabled = !cargando
            ) {
                Icon(
                    Icons.Default.Map,
                    contentDescription = null,
                    tint = acento,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    "Abrir mapa",
                    color = acento,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private val FormatoTelefonoVisual = VisualTransformation { texto ->
    val digitos = texto.text.filter { it.isDigit() }.take(10)
    val mostrado = buildString {
        digitos.forEachIndexed { index, char ->
            if (index == 3 || index == 6) append(' ')
            append(char)
        }
    }
    val mapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val seguro = offset.coerceIn(0, digitos.length)
            return seguro + (if (seguro > 3) 1 else 0) + (if (seguro > 6) 1 else 0)
        }
        override fun transformedToOriginal(offset: Int): Int {
            var original = offset.coerceIn(0, mostrado.length)
            if (offset > 3) original--
            if (offset > 7) original--
            return original.coerceIn(0, digitos.length)
        }
    }
    TransformedText(AnnotatedString(mostrado), mapping)
}

private data class PaisTelefono(val nombre: String, val codigo: String, val bandera: String)

private val PAISES_TELEFONO = listOf(
    PaisTelefono("México", "+52", "MX"),
    PaisTelefono("Estados Unidos", "+1", "US"),
    PaisTelefono("Canadá", "+1", "CA"),
    PaisTelefono("España", "+34", "ES"),
    PaisTelefono("Colombia", "+57", "CO"),
    PaisTelefono("Argentina", "+54", "AR"),
    PaisTelefono("Brasil", "+55", "BR"),
    PaisTelefono("Chile", "+56", "CL"),
    PaisTelefono("Perú", "+51", "PE"),
    PaisTelefono("Guatemala", "+502", "GT")
)

@Composable
private fun BanderaPais(codigo: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(width = 25.dp, height = 17.dp)) {
        val w = size.width
        val h = size.height
        fun rect(color: Color, left: Float, top: Float, right: Float, bottom: Float) {
            drawRect(color, androidx.compose.ui.geometry.Offset(left, top), androidx.compose.ui.geometry.Size(right-left, bottom-top))
        }
        when (codigo) {
            "MX" -> { rect(Color(0xFF006847),0f,0f,w/3,h); rect(Color.White,w/3,0f,2*w/3,h); rect(Color(0xFFCE1126),2*w/3,0f,w,h) }
            "US" -> { for (i in 0..6) rect(if(i%2==0) Color(0xFFB22234) else Color.White,0f,h*i/7,w,h*(i+1)/7); rect(Color(0xFF3C3B6E),0f,0f,w*0.45f,h*0.55f) }
            "CA" -> { rect(Color(0xFFD80621),0f,0f,w*0.27f,h); rect(Color.White,w*0.27f,0f,w*0.73f,h); rect(Color(0xFFD80621),w*0.73f,0f,w,h) }
            "ES" -> { rect(Color(0xFFAA151B),0f,0f,w,h*0.25f); rect(Color(0xFFF1BF00),0f,h*0.25f,w,h*0.75f); rect(Color(0xFFAA151B),0f,h*0.75f,w,h) }
            "CO" -> { rect(Color(0xFFFCD116),0f,0f,w,h/2); rect(Color(0xFF003893),0f,h/2,w,h*0.75f); rect(Color(0xFFCE1126),0f,h*0.75f,w,h) }
            "AR" -> { rect(Color(0xFF74ACDF),0f,0f,w,h/3); rect(Color.White,0f,h/3,w,h*2/3); rect(Color(0xFF74ACDF),0f,h*2/3,w,h) }
            "BR" -> { rect(Color(0xFF009739),0f,0f,w,h); val path = androidx.compose.ui.graphics.Path().apply { moveTo(w/2, h*0.15f); lineTo(w*0.9f,h/2); lineTo(w/2,h*0.85f); lineTo(w*0.1f,h/2); close() }; drawPath(path, Color(0xFFFFDF00)); drawCircle(Color(0xFF002776), radius=h*0.22f, center=androidx.compose.ui.geometry.Offset(w/2,h/2)) }
            "CL" -> { rect(Color.White,0f,0f,w,h/2); rect(Color(0xFFD52B1E),0f,h/2,w,h); rect(Color(0xFF0039A6),0f,0f,h/2,h/2) }
            "PE" -> { rect(Color(0xFFD91023),0f,0f,w/3,h); rect(Color.White,w/3,0f,2*w/3,h); rect(Color(0xFFD91023),2*w/3,0f,w,h) }
            else -> { rect(Color(0xFF4997D0),0f,0f,w,h/3); rect(Color.White,0f,h/3,w,h*2/3); rect(Color(0xFF4997D0),0f,h*2/3,w,h) }
        }
        drawRoundRect(Color(0x33000000), size = size, cornerRadius = CornerRadius(3.dp.toPx()), style = Stroke(width = 0.6.dp.toPx()))
    }
}

@Composable
private fun CampoWhatsApp(
    valor: String,
    onCambio: (String) -> Unit,
    error: Boolean,
    acento: Color
) {
    val contexto = androidx.compose.ui.platform.LocalContext.current
    var paisSeleccionado by remember {
        mutableStateOf(
            PAISES_TELEFONO.firstOrNull { it.codigo == contexto.getSharedPreferences("unifind", Context.MODE_PRIVATE).getString("whatsapp_lada", "+52") }
                ?: PAISES_TELEFONO.first()
        )
    }
    var mostrarPaises by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(end = 7.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF25D366)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Phone, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, if (error) Color(0xFFD32F2F) else Color(0xFFDCE5DF), RoundedCornerShape(14.dp))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .clickable { mostrarPaises = true }
                        .padding(horizontal = 2.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    BanderaPais(paisSeleccionado.bandera)
                    Text(paisSeleccionado.codigo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF26332C))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Elegir lada", tint = Color(0xFF68756D), modifier = Modifier.size(17.dp))
                }
                DropdownMenu(expanded = mostrarPaises, onDismissRequest = { mostrarPaises = false }) {
                    PAISES_TELEFONO.forEach { pais ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                                    BanderaPais(pais.bandera)
                                    Text(pais.nombre, fontSize = 14.sp)
                                    Text(pais.codigo, fontSize = 13.sp, color = Color(0xFF68756D))
                                }
                            },
                            onClick = {
                                paisSeleccionado = pais
                                contexto.getSharedPreferences("unifind", Context.MODE_PRIVATE).edit().putString("whatsapp_lada", pais.codigo).apply()
                                mostrarPaises = false
                            }
                        )
                    }
                }
            }
            Divider(modifier = Modifier.padding(horizontal = 8.dp).height(27.dp).width(1.dp), color = Color(0xFFDCE5DF))
            androidx.compose.foundation.text.BasicTextField(
                value = valor,
                onValueChange = { onCambio(it.filter(Char::isDigit).take(10)) },
                modifier = Modifier.weight(1f).fillMaxWidth(),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = Color(0xFF202820)),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                visualTransformation = FormatoTelefonoVisual,
                cursorBrush = androidx.compose.ui.graphics.SolidColor(acento),
                decorationBox = { inner ->
                    Box {
                        if (valor.isEmpty()) Text("826 166 8990", fontSize = 14.sp, color = Color(0xFF9AA39D))
                        inner()
                    }
                }
            )
        }
    }
}

// ==========================================================
// TARJETA DE SECCIÓN NUMERADA
// ==========================================================

@Composable
private fun AccionFoto(
    modifier: Modifier = Modifier,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    subtitulo: String,
    acento: Color,
    fondo: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(108.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(fondo)
            .border(1.dp, acento.copy(alpha = 0.18f), RoundedCornerShape(17.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(acento),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = Color.White,
                modifier = Modifier.size(21.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = titulo,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )
        Text(
            text = subtitulo,
            fontSize = 10.sp,
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TarjetaSeccionNumerada(
    numero: Int,
    titulo: String,
    subtitulo: String,
    acento: Color,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(30.dp).clip(CircleShape).background(acento),
                contentAlignment = Alignment.Center
            ) {
                Text(numero.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                Text(subtitulo, fontSize = 11.sp, color = TextoSecundario)
            }
        }
        Spacer(Modifier.height(10.dp))
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
                elevation = 4.dp,
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
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        categoriasFormulario.chunked(2).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                if (fila.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        if (error && seleccion == null) {
            Text("Selecciona una categoría", fontSize = 11.sp, color = Rojo)
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
    val forma = RoundedCornerShape(12.dp)
    val fondo by animateColorAsState(
        targetValue = if (activa) acento else FondoCampo,
        animationSpec = tween(220),
        label = "fondoChip"
    )
    val borde by animateColorAsState(
        targetValue = when {
            activa -> acento
            errorSinSeleccion -> Rojo
            else -> BordeCampo
        },
        animationSpec = tween(220),
        label = "bordeChip"
    )
    Column(
        modifier = modifier
            .height(56.dp)
            .clip(forma)
            .background(fondo)
            .border(
                width = if (errorSinSeleccion) 1.5.dp else 1.dp,
                color = borde,
                shape = forma
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val iconoCategoria = when (opcion) {
            "Mochilas" -> Icons.Default.Add
            "Electrónicos" -> Icons.Default.Search
            "Llaves" -> Icons.Default.Check
            "Ropa" -> Icons.Default.Close
            "Accesorios" -> Icons.Default.KeyboardArrowDown
            "Documentos" -> Icons.AutoMirrored.Filled.KeyboardArrowRight
            "Libros" -> Icons.Default.KeyboardArrowDown
            else -> Icons.Default.Add
        }

        Icon(
            imageVector = iconoCategoria,
            contentDescription = opcion,
            tint = if (activa) Color.White else acento,
            modifier = Modifier.size(21.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = opcion,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (activa) Color.White else TextoPrincipal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
        modifier = Modifier.height(1.dp)
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
    acento: Color = Verde,
    visualTransformation: VisualTransformation = VisualTransformation.None
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
        visualTransformation = visualTransformation,

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

    Box(
        modifier = Modifier
            .size(84.dp)
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
    val whatsapp: String,
    val whatsappLada: String
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
        whatsapp = doc.getString("whatsapp").orEmpty().filter { it.isDigit() },
        whatsappLada = doc.getString("whatsappLada") ?: "+52",
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
            // No descartar reportes antiguos por no tener whatsapp en el documento raíz.
            // El contacto puede existir en la subcolección contactos.
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

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Sin fotografía",
                        tint = TextoClaro,
                        modifier = Modifier.size(28.dp)
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
// CARD DESDE ABAJO: MENSAJE PARA QUIEN PUBLICÓ EL REPORTE
// Reemplaza el AlertDialog por una tarjeta tipo bottom sheet.
// ==========================================================

@Composable
private fun ModalCoincidenciaDesdeAbajo(
    reporte: ReporteExistente,
    usuarioPerdio: Boolean,
    enviando: Boolean,
    onCancelar: () -> Unit,
    onEnviar: (String) -> Unit
) {
    var mensaje by remember { mutableStateOf("") }
    var intento by remember { mutableStateOf(false) }
    val mensajeInvalido = mensaje.trim().length < 5

    AnimatedVisibility(
        visible = true,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(tween(220)),
        exit = fadeOut(tween(160))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoModal)
                .clickable(
                    enabled = !enviando,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onCancelar
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    animationSpec = tween(420, easing = FastOutSlowInEasing)
                ) { it } + fadeIn(tween(250)),
                exit = fadeOut(tween(150)) +
                        androidx.compose.animation.slideOutVertically(tween(250)) { it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = true,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {}
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(18.dp, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(BordeCampo)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(RojoClaro),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Search, null, tint = RojoOscuro, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = if (usuarioPerdio) "¿Es tu objeto?" else "¿Tú lo encontraste?",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )
                            Text(
                                text = "Confirma los detalles antes de contactar al usuario.",
                                fontSize = 12.sp,
                                color = TextoSecundario
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FondoGris)
                                .clickable(enabled = !enviando, onClick = onCancelar),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, "Cerrar", tint = TextoSecundario, modifier = Modifier.size(19.dp))
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(FondoCampo)
                            .border(1.dp, BordeCampo, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Text(reporte.titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            listOf(reporte.categoria, reporte.ubicacion).filter { it.isNotBlank() }.joinToString(" • "),
                            fontSize = 12.sp,
                            color = VerdeOscuro,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Etiqueta(
                        texto = if (usuarioPerdio) "¿Por qué es tuyo?" else "¿Dónde lo tienes?",
                        obligatorio = true
                    )

                    CampoTexto(
                        valor = mensaje,
                        onCambio = { if (it.length <= LIMITE_DESCRIPCION) mensaje = it },
                        placeholder = if (usuarioPerdio) {
                            "Marcas, contenido, detalles que solo el dueño sabría..."
                        } else {
                            "Ej. Lo tengo en la biblioteca, en el mostrador..."
                        },
                        modifier = Modifier.height(110.dp),
                        singleLine = false,
                        error = intento && mensajeInvalido,
                        acento = RojoOscuro
                    )

                    if (intento && mensajeInvalido) {
                        Spacer(Modifier.height(5.dp))
                        Text("Escribe al menos 5 caracteres", fontSize = 11.sp, color = RojoOscuro)
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Check, null, tint = Verde, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(7.dp))
                        Text(
                            "Al continuar se abrirá WhatsApp con el mensaje listo para enviarlo.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = TextoSecundario
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(FondoGris)
                                .clickable(enabled = !enviando, onClick = onCancelar),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Cancelar", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoSecundario)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(RojoOscuro)
                                .clickable(enabled = !enviando) {
                                    intento = true
                                    if (!mensajeInvalido) onEnviar(mensaje.trim())
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (enviando) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.5.dp, modifier = Modifier.size(22.dp))
                            } else {
                                Text("Abrir WhatsApp", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
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
    texto: String,
    ladaReporte: String? = null
) {
    // La lada se combina solo al abrir WhatsApp; el campo conserva únicamente el número local.
    val lada = (ladaReporte
        ?: contexto.getSharedPreferences("unifind", Context.MODE_PRIVATE)
            .getString("whatsapp_lada", "+52")
        ?: "+52").filter { it.isDigit() }
    val numero = telefono.filter { it.isDigit() }

    val intent =
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "https://wa.me/$lada$numero?text=${Uri.encode(texto)}"
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
// AVISO DE PUBLICACIÓN DENTRO DE LA APP
// ==========================================================

@Composable
private fun AvisoPublicacion(
    mensaje: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        modifier = modifier,
        enter = slideInVertically(
            animationSpec = tween(420, easing = FastOutSlowInEasing)
        ) { -it } + fadeIn(tween(250)),
        exit = fadeOut(tween(180))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(14.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .border(1.dp, RojoClaro, RoundedCornerShape(18.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Rojo),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("¡Publicado!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                Text(
                    mensaje,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = TextoSecundario,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
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
