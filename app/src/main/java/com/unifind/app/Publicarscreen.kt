package com.unifind.app

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================================
// COLORES UNIFIND
// ==========================================================

private val Verde = Color(0xFF008F63)
private val VerdeOscuro = Color(0xFF006B4A)
private val VerdeClaro = Color(0xFFE2F6EB)

private val Rojo = Color(0xFFE53935)
private val RojoOscuro = Color(0xFFC62828)
private val RojoClaro = Color(0xFFFFE9E9)

private val FondoPrincipal = Color(0xFFF8FAF9)
private val FondoCampo = Color(0xFFF6F8F8)
private val FondoGris = Color(0xFFF0F3F3)

private val BordeCampo = Color(0xFFE0E5E3)

private val FondoConsejo = Color(0xFFFFF7DF)

private val TextoPrincipal = Color(0xFF202124)
private val TextoSecundario = Color(0xFF5F6368)
private val TextoClaro = Color(0xFF8A9094)

private const val LIMITE_DESCRIPCION = 200

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
// FONDO GENERAL UNIFIND
// ==========================================================

@Composable
private fun FondoUniFind() {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.banner_pantallas
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.55f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(alpha = 0.66f)
                )
        )
    }
}

// ==========================================================
// PANTALLA PRINCIPAL DE PUBLICAR
// ==========================================================

@Composable
fun PublicarScreen(
    onNavegar: (String) -> Unit = {},
    onTerminar: () -> Unit = {}
) {

    var tipo by remember {
        mutableStateOf<String?>(null)
    }

    BackHandler(enabled = tipo != null) {
        tipo = null
    }

    val tipoActual = tipo

    if (tipoActual == null) {

        SelectorTipo(
            onNavegar = onNavegar,
            onElegir = {
                tipo = it
            }
        )

    } else {

        FormularioReporte(
            tipo = tipoActual,
            onBack = {
                tipo = null
            },
            onPublicado = onTerminar
        )
    }
}

// ==========================================================
// SELECTOR DE PERDIDO / ENCONTRADO
// ==========================================================

@Composable
private fun SelectorTipo(
    onNavegar: (String) -> Unit,
    onElegir: (String) -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        FondoUniFind()

        Scaffold(
            containerColor = Color.Transparent,

            bottomBar = {
                UniFindBottomBar(
                    seleccionado = "Publicar",
                    onSeleccionar = onNavegar
                )
            }

        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(
                        bottom = paddingValues.calculateBottomPadding()
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 12.dp
                    )
            ) {

                // ==================================================
                // ENCABEZADO
                // ==================================================

                Text(
                    text = "Publicar",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                // ==================================================
                // BANNER PRINCIPAL
                // ==================================================

                BannerPublicarPrincipal()

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                // ==================================================
                // TÍTULO
                // ==================================================

                Text(
                    text = "¿Qué quieres reportar?",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Selecciona una opción para comenzar tu publicación.",
                    fontSize = 14.sp,
                    color = TextoSecundario
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                // ==================================================
                // OBJETO PERDIDO
                // ==================================================

                OpcionPublicarNueva(
                    fondo = RojoClaro,
                    borde = Color(0xFFFFCDCD),
                    acento = Rojo,
                    icono = R.drawable.magnifying_glass_solid,
                    titulo = "Objeto perdido",
                    subtitulo = "Perdí algo y quiero encontrarlo",
                    etiqueta = "LO PERDÍ",
                    onClick = {
                        onElegir("perdido")
                    }
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // ==================================================
                // OBJETO ENCONTRADO
                // ==================================================

                OpcionPublicarNueva(
                    fondo = VerdeClaro,
                    borde = Color(0xFFBCE6D1),
                    acento = Verde,
                    icono = R.drawable.plus_solid,
                    titulo = "Objeto encontrado",
                    subtitulo = "Encontré algo y quiero devolverlo",
                    etiqueta = "LO ENCONTRÉ",
                    onClick = {
                        onElegir("encontrado")
                    }
                )

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                // ==================================================
                // CÓMO FUNCIONA
                // ==================================================

                ComoFuncionaNueva()

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                // ==================================================
                // CONSEJO
                // ==================================================

                ConsejoPublicarNueva()

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }
        }
    }
}

// ==========================================================
// BANNER PRINCIPAL
// ==========================================================

@Composable
private fun BannerPublicarPrincipal() {

    val forma = RoundedCornerShape(27.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 7.dp,
                shape = forma,
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .clip(forma)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Verde,
                        VerdeOscuro
                    )
                )
            )
            .drawBehind {

                drawCircle(
                    color = Color.White.copy(alpha = 0.10f),
                    radius = 100.dp.toPx(),
                    center = Offset(
                        size.width - 20.dp.toPx(),
                        0f
                    )
                )

                drawCircle(
                    color = Color.White.copy(alpha = 0.07f),
                    radius = 65.dp.toPx(),
                    center = Offset(
                        size.width - 105.dp.toPx(),
                        size.height
                    )
                )

                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = 42.dp.toPx(),
                    center = Offset(
                        25.dp.toPx(),
                        size.height - 10.dp.toPx()
                    )
                )
            }
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.18f),
                shape = forma
            )
            .padding(22.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Ayuda a reunir objetos con sus dueños",
                    fontSize = 21.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                Text(
                    text = "Publica tu reporte de forma rápida y sencilla.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.White.copy(alpha = 0.90f)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(
                        Color.White.copy(alpha = 0.18f),
                        CircleShape
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.22f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.magnifying_glass_solid
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}

// ==========================================================
// TARJETAS PERDIDO / ENCONTRADO
// ==========================================================

@Composable
private fun OpcionPublicarNueva(
    fondo: Color,
    borde: Color,
    acento: Color,
    icono: Int,
    titulo: String,
    subtitulo: String,
    etiqueta: String,
    onClick: () -> Unit
) {

    val forma = RoundedCornerShape(24.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = forma,
                ambientColor = Color(0x18000000),
                spotColor = Color(0x18000000)
            )
            .background(
                color = fondo.copy(alpha = 0.96f),
                shape = forma
            )
            .border(
                width = 1.dp,
                color = borde,
                shape = forma
            )
            .clip(forma)
            .clickable {
                onClick()
            }
            .padding(18.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(
                        color = acento,
                        shape = RoundedCornerShape(19.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(id = icono),
                    contentDescription = titulo,
                    modifier = Modifier.size(29.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = etiqueta,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = acento
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = subtitulo,
                    fontSize = 13.sp,
                    color = TextoSecundario
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        Color.White.copy(alpha = 0.88f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = acento,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

// ==========================================================
// CÓMO FUNCIONA
// ==========================================================

@Composable
private fun ComoFuncionaNueva() {

    val forma = RoundedCornerShape(23.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = forma,
                ambientColor = Color(0x16000000),
                spotColor = Color(0x16000000)
            )
            .background(
                Color.White.copy(alpha = 0.95f),
                forma
            )
            .border(
                1.dp,
                BordeCampo,
                forma
            )
            .padding(20.dp)
    ) {

        Text(
            text = "¿Cómo funciona?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        PasoItemNuevo(
            numero = "1",
            titulo = "Elige qué quieres reportar",
            texto = "Indica si perdiste o encontraste un objeto."
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        PasoItemNuevo(
            numero = "2",
            titulo = "Agrega los detalles",
            texto = "Añade fotos, nombre, categoría y ubicación."
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        PasoItemNuevo(
            numero = "3",
            titulo = "Publica tu reporte",
            texto = "Tu publicación quedará disponible para encontrar coincidencias."
        )
    }
}

// ==========================================================
// PASO
// ==========================================================

@Composable
private fun PasoItemNuevo(
    numero: String,
    titulo: String,
    texto: String
) {

    Row(
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    VerdeClaro,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = numero,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro
            )
        }

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = texto,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = TextoSecundario
            )
        }
    }
}

// ==========================================================
// CONSEJO
// ==========================================================

@Composable
private fun ConsejoPublicarNueva() {

    val forma = RoundedCornerShape(20.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                FondoConsejo.copy(alpha = 0.95f),
                forma
            )
            .border(
                1.dp,
                Color(0xFFFFE9A7),
                forma
            )
            .padding(17.dp),
        verticalAlignment = Alignment.Top
    ) {

        Text(
            text = "💡",
            fontSize = 25.sp
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Consejo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B5300)
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "Usa fotos claras y agrega una ubicación aproximada para aumentar las posibilidades de encontrar coincidencias.",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = Color(0xFF6B5300)
            )
        }
    }
}

// ==========================================================
// FORMULARIO
// ==========================================================

@Composable
private fun FormularioReporte(
    tipo: String,
    onBack: () -> Unit,
    onPublicado: () -> Unit
) {

    val contexto = LocalContext.current

    val esPerdido = tipo == "perdido"

    val titulo =
        if (esPerdido) {
            "Reportar objeto perdido"
        } else {
            "Reportar objeto encontrado"
        }

    val subtitulo =
        if (esPerdido) {
            "Cuéntanos qué perdiste para ayudarte a encontrarlo."
        } else {
            "Describe lo que encontraste para ayudar a devolverlo."
        }

    val ejemploNombre =
        if (esPerdido) {
            "Ej. Mochila negra"
        } else {
            "Ej. Llaves con llavero azul"
        }

    // ======================================================
    // ESTADOS
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

    var intentoPublicar by remember {
        mutableStateOf(false)
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
    // PUBLICAR
    // ======================================================

    val publicar: () -> Unit = {

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

            else -> {

                Toast.makeText(
                    contexto,
                    "Reporte publicado",
                    Toast.LENGTH_SHORT
                ).show()

                onPublicado()
            }
        }
    }

    // ======================================================
    // PANTALLA
    // ======================================================

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        FondoUniFind()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        end = 18.dp,
                        top = 6.dp,
                        bottom = 5.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable {
                            onBack()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = TextoPrincipal,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    text = "Nueva publicación",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Row(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .background(
                            if (esPerdido) {
                                RojoClaro
                            } else {
                                VerdeClaro
                            }
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (esPerdido) {
                                    Rojo
                                } else {
                                    Verde
                                },
                                CircleShape
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    Text(
                        text =
                            if (esPerdido) {
                                "Perdido"
                            } else {
                                "Encontrado"
                            },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color =
                            if (esPerdido) {
                                RojoOscuro
                            } else {
                                VerdeOscuro
                            }
                    )
                }
            }

            // ==================================================
            // CONTENIDO
            // ==================================================

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 18.dp
                    )
            ) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // ==================================================
                // BANNER DEL FORMULARIO
                // ==================================================

                BannerFormulario(
                    esPerdido = esPerdido
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = titulo,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = subtitulo,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = TextoSecundario
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                // ==================================================
                // FOTOS
                // ==================================================

                SeccionFormulario(
                    titulo = "Fotos",
                    obligatorio = false
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(155.dp)
                            .clip(
                                RoundedCornerShape(20.dp)
                            )
                            .background(
                                Color.White.copy(alpha = 0.95f)
                            )
                            .drawBehind {

                                drawRoundRect(
                                    color = Color(0xFFB8BEC1),
                                    cornerRadius = CornerRadius(
                                        20.dp.toPx()
                                    ),
                                    style = Stroke(
                                        width = 1.5.dp.toPx(),
                                        pathEffect =
                                            PathEffect.dashPathEffect(
                                                floatArrayOf(
                                                    18f,
                                                    12f
                                                ),
                                                0f
                                            )
                                    )
                                )
                            }
                            .clickable {
                                abrirGaleria()
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        VerdeClaro,
                                        CircleShape
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text = "📷",
                                    fontSize = 25.sp
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "Agregar fotos",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )

                            Spacer(
                                modifier = Modifier.height(2.dp)
                            )

                            Text(
                                text = "Hasta 3 fotos",
                                fontSize = 12.sp,
                                color = TextoSecundario
                            )
                        }
                    }

                    // ==================================================
                    // MINIATURAS
                    // ==================================================

                    if (fotos.isNotEmpty()) {

                        Spacer(
                            modifier = Modifier.height(13.dp)
                        )

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            fotos.toList().forEach { uri ->

                                MiniFoto(
                                    uri = uri,
                                    onQuitar = {
                                        fotos.remove(uri)
                                    }
                                )
                            }

                            if (fotos.size < 3) {

                                Box(
                                    modifier = Modifier
                                        .size(66.dp)
                                        .clip(
                                            RoundedCornerShape(12.dp)
                                        )
                                        .background(
                                            FondoGris
                                        )
                                        .clickable {
                                            abrirGaleria()
                                        },
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Add,
                                        contentDescription =
                                            "Agregar otra foto",
                                        tint =
                                            TextoSecundario,
                                        modifier =
                                            Modifier.size(27.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(22.dp)
                )

                // ==================================================
                // INFORMACIÓN DEL OBJETO
                // ==================================================

                TarjetaSeccionFormulario {

                    Text(
                        text = "Información del objeto",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    // ==================================================
                    // NOMBRE
                    // ==================================================

                    Etiqueta(
                        texto = "Nombre del objeto",
                        obligatorio = true
                    )

                    CampoTexto(
                        valor = nombre,
                        onCambio = {
                            nombre = it
                        },
                        placeholder = ejemploNombre,
                        modifier = Modifier.height(56.dp),
                        singleLine = true,
                        error =
                            intentoPublicar &&
                                    nombre.isBlank()
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    // ==================================================
                    // CATEGORÍA
                    // ==================================================

                    Etiqueta(
                        texto = "Categoría",
                        obligatorio = true
                    )

                    CampoDesplegable(
                        placeholder =
                            "Selecciona una categoría",
                        seleccion = categoria,
                        opciones = categoriasFormulario,
                        error =
                            intentoPublicar &&
                                    categoria == null,
                        onSeleccion = {
                            categoria = it
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    // ==================================================
                    // DESCRIPCIÓN
                    // ==================================================

                    Etiqueta(
                        texto = "Descripción",
                        obligatorio = false
                    )

                    CampoTexto(
                        valor = descripcion,
                        onCambio = {

                            if (
                                it.length <=
                                LIMITE_DESCRIPCION
                            ) {
                                descripcion = it
                            }
                        },
                        placeholder =
                            "Describe el objeto con el mayor detalle posible...",
                        modifier = Modifier.height(120.dp),
                        singleLine = false,
                        error = false
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "${descripcion.length}/$LIMITE_DESCRIPCION",
                        modifier =
                            Modifier.fillMaxWidth(),
                        fontSize = 12.sp,
                        color = TextoClaro,
                        textAlign = TextAlign.End
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // ==================================================
                // UBICACIÓN
                // ==================================================

                TarjetaSeccionFormulario {

                    Text(
                        text = "¿Dónde ocurrió?",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "Indica una ubicación aproximada.",
                        fontSize = 13.sp,
                        color = TextoSecundario
                    )

                    Spacer(
                        modifier = Modifier.height(17.dp)
                    )

                    Etiqueta(
                        texto = "Ubicación aproximada",
                        obligatorio = true
                    )

                    CampoDesplegable(
                        placeholder =
                            "Selecciona la ubicación",
                        seleccion = ubicacion,
                        opciones = ubicacionesFormulario,
                        iconoInicio =
                            R.drawable.location_dot_solid,
                        flechaAbajo = false,
                        error =
                            intentoPublicar &&
                                    ubicacion == null,
                        onSeleccion = {
                            ubicacion = it
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Text(
                    text = "* Campos obligatorios",
                    fontSize = 12.sp,
                    color = TextoClaro
                )

                Spacer(
                    modifier = Modifier.height(25.dp)
                )
            }

            // ==================================================
            // BOTÓN PUBLICAR
            // ==================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.96f)
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 10.dp
                    )
            ) {

                Button(
                    onClick = publicar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeOscuro
                    ),
                    elevation =
                        ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp
                        )
                ) {

                    Text(
                        text = "Publicar reporte",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ==========================================================
// BANNER DEL FORMULARIO
// ==========================================================

@Composable
private fun BannerFormulario(
    esPerdido: Boolean
) {

    val colorPrincipal =
        if (esPerdido) {
            Rojo
        } else {
            Verde
        }

    val colorSecundario =
        if (esPerdido) {
            RojoOscuro
        } else {
            VerdeOscuro
        }

    val forma = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = forma,
                ambientColor = Color(0x18000000),
                spotColor = Color(0x18000000)
            )
            .clip(forma)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        colorPrincipal,
                        colorSecundario
                    )
                )
            )
            .drawBehind {

                drawCircle(
                    color = Color.White.copy(
                        alpha = 0.10f
                    ),
                    radius = 80.dp.toPx(),
                    center = Offset(
                        size.width - 10.dp.toPx(),
                        5.dp.toPx()
                    )
                )

                drawCircle(
                    color = Color.White.copy(
                        alpha = 0.06f
                    ),
                    radius = 48.dp.toPx(),
                    center = Offset(
                        20.dp.toPx(),
                        size.height
                    )
                )
            }
            .border(
                1.dp,
                Color.White.copy(alpha = 0.16f),
                forma
            )
            .padding(19.dp)
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text =
                        if (esPerdido) {
                            "¿Perdiste algo?"
                        } else {
                            "¿Encontraste algo?"
                        },
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text =
                        if (esPerdido) {
                            "Agrega los detalles para que puedan ayudarte a encontrarlo."
                        } else {
                            "Comparte los detalles para ayudar a devolverlo."
                        },
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color.White.copy(
                        alpha = 0.90f
                    )
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Color.White.copy(
                            alpha = 0.18f
                        ),
                        CircleShape
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.20f),
                        CircleShape
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                Image(
                    painter = painterResource(
                        id =
                            if (esPerdido) {
                                R.drawable.magnifying_glass_solid
                            } else {
                                R.drawable.plus_solid
                            }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(27.dp),
                    colorFilter =
                        ColorFilter.tint(
                            Color.White
                        )
                )
            }
        }
    }
}

// ==========================================================
// TARJETA DE SECCIÓN
// ==========================================================

@Composable
private fun TarjetaSeccionFormulario(
    contenido: @Composable () -> Unit
) {

    val forma = RoundedCornerShape(21.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = forma,
                ambientColor = Color(0x15000000),
                spotColor = Color(0x15000000)
            )
            .background(
                Color.White.copy(alpha = 0.95f),
                forma
            )
            .border(
                1.dp,
                BordeCampo,
                forma
            )
            .padding(18.dp)
    ) {

        contenido()
    }
}

// ==========================================================
// SECCIÓN SIMPLE
// ==========================================================

@Composable
private fun SeccionFormulario(
    titulo: String,
    obligatorio: Boolean,
    contenido: @Composable () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Etiqueta(
            texto = titulo,
            obligatorio = obligatorio
        )

        contenido()
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
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )

        if (obligatorio) {

            Text(
                text = " *",
                fontSize = 15.sp,
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
    error: Boolean
) {

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        isError = error,

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

                focusedBorderColor = Verde,
                unfocusedBorderColor = BordeCampo,
                errorBorderColor = Rojo,

                cursorColor = Verde,
                errorCursorColor = Rojo,

                focusedContainerColor =
                    FondoCampo.copy(alpha = 0.94f),

                unfocusedContainerColor =
                    FondoCampo.copy(alpha = 0.94f),

                errorContainerColor =
                    FondoCampo.copy(alpha = 0.94f)
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
                .background(
                    FondoCampo.copy(alpha = 0.94f)
                )
                .border(
                    width =
                        if (error) {
                            1.5.dp
                        } else {
                            1.dp
                        },
                    color =
                        if (error) {
                            Rojo
                        } else {
                            BordeCampo
                        },
                    shape = forma
                )
                .clickable {
                    abierto = true
                }
                .padding(
                    horizontal = 14.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            if (iconoInicio != null) {

                Image(
                    painter = painterResource(
                        id = iconoInicio
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter =
                        ColorFilter.tint(
                            VerdeOscuro
                        )
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )
            }

            Text(
                text =
                    seleccion
                        ?: placeholder,
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
                                    VerdeOscuro
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
            .size(66.dp)
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(
                Color(0xFFEDEDED)
            )
    ) {

        if (bitmap != null) {

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription =
                    "Foto del objeto",
                modifier =
                    Modifier.fillMaxSize(),
                contentScale =
                    ContentScale.Crop
            )
        }

        // ==================================================
        // BOTÓN QUITAR
        // ==================================================

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(3.dp)
                .size(20.dp)
                .background(
                    Color(0xAA000000),
                    CircleShape
                )
                .clip(CircleShape)
                .clickable {
                    onQuitar()
                },
            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Default.Close,
                contentDescription =
                    "Quitar foto",
                tint = Color.White,
                modifier =
                    Modifier.size(13.dp)
            )
        }
    }
}