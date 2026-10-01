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
private val Rojo = Color(0xFFE53935)

private val FondoPerdido = Color(0xFFFFE9E9)
private val FondoEncontrado = Color(0xFFE2F6EB)
private val FondoGris = Color(0xFFF3F5F5)
private val FondoCampo = Color(0xFFF8F9F9)
private val BordeCampo = Color(0xFFE0E4E5)
private val FondoConsejo = Color(0xFFFFF6DD)

private val TextoPrincipal = Color(0xFF202124)
private val TextoSecundario = Color(0xFF5F6368)
private val TextoClaro = Color(0xFF8A9094)


// ==========================================================
// OPCIONES DEL FORMULARIO
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

private const val LIMITE_DESCRIPCION = 200


// ==========================================================
// PANTALLA PUBLICAR
//
// 1) Primero muestra: Publicar objeto perdido / encontrado
// 2) Al elegir una opción, abre el formulario
// ==========================================================

@Composable
fun PublicarScreen(
    onNavegar: (String) -> Unit = {},
    onTerminar: () -> Unit = {}
) {

    // null = pantalla de elegir | "perdido" | "encontrado"
    var tipo by remember { mutableStateOf<String?>(null) }

    // Botón atrás del teléfono: del formulario regresa a elegir
    BackHandler(enabled = tipo != null) {
        tipo = null
    }

    val tipoActual = tipo

    if (tipoActual == null) {

        SelectorTipo(
            onNavegar = onNavegar,
            onElegir = { tipo = it }
        )

    } else {

        FormularioReporte(
            tipo = tipoActual,
            onBack = { tipo = null },
            onPublicado = onTerminar
        )
    }
}


// ==========================================================
// PASO 1: ELEGIR PERDIDO O ENCONTRADO
// ==========================================================

@Composable
private fun SelectorTipo(
    onNavegar: (String) -> Unit,
    onElegir: (String) -> Unit
) {

    Scaffold(

        containerColor = Color.White,

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
                text = "Publicar objeto",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))


            // ==================================================
            // BANNER
            // ==================================================

            BannerAyuda()

            Spacer(modifier = Modifier.height(26.dp))


            // ==================================================
            // OPCIONES
            // ==================================================

            Text(
                text = "¿Qué quieres reportar?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )

            Spacer(modifier = Modifier.height(14.dp))

            OpcionPublicar(
                fondo = FondoPerdido,
                borde = Color(0xFFFFCFCF),
                acento = Rojo,
                icono = R.drawable.magnifying_glass_solid,
                titulo = "Publicar objeto perdido",
                subtitulo = "Perdí algo y quiero encontrarlo",
                onClick = { onElegir("perdido") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            OpcionPublicar(
                fondo = FondoEncontrado,
                borde = Color(0xFFBFE8D2),
                acento = Verde,
                icono = R.drawable.plus_solid,
                titulo = "Publicar objeto encontrado",
                subtitulo = "Encontré algo y quiero devolverlo",
                onClick = { onElegir("encontrado") }
            )

            Spacer(modifier = Modifier.height(28.dp))


            // ==================================================
            // CÓMO FUNCIONA
            // ==================================================

            ComoFunciona()

            Spacer(modifier = Modifier.height(18.dp))


            // ==================================================
            // CONSEJO
            // ==================================================

            ConsejoPublicar()
        }
    }
}


// ==========================================================
// BANNER VERDE CON DEGRADADO
// ==========================================================

@Composable
private fun BannerAyuda() {

    val forma = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(
                Brush.linearGradient(
                    colors = listOf(Verde, VerdeOscuro)
                )
            )
            .drawBehind {

                // Círculos decorativos
                drawCircle(
                    color = Color.White.copy(alpha = 0.10f),
                    radius = 70.dp.toPx(),
                    center = Offset(size.width - 20.dp.toPx(), 6.dp.toPx())
                )

                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = 46.dp.toPx(),
                    center = Offset(size.width - 100.dp.toPx(), size.height)
                )
            }
            .padding(22.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = "Ayuda a que cada objeto regrese a su dueño",
                    fontSize = 19.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Publica en menos de un minuto.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Box(
                modifier = Modifier
                    .size(66.dp)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Text(text = "🔎", fontSize = 30.sp)
            }
        }
    }
}


// ==========================================================
// TARJETA DE OPCIÓN (PERDIDO / ENCONTRADO)
// ==========================================================

@Composable
private fun OpcionPublicar(
    fondo: Color,
    borde: Color,
    acento: Color,
    icono: Int,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {

    val forma = RoundedCornerShape(24.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = forma,
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .background(fondo, forma)
            .border(1.dp, borde, forma)
            .clip(forma)
            .clickable { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ICONO EN CUADRO DE COLOR
        Box(
            modifier = Modifier
                .size(62.dp)
                .background(acento, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = icono),
                contentDescription = titulo,
                modifier = Modifier.size(28.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitulo,
                fontSize = 13.sp,
                color = TextoSecundario
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // FLECHA EN CÍRCULO BLANCO
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = acento,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


// ==========================================================
// CÓMO FUNCIONA (3 PASOS)
// ==========================================================

@Composable
private fun ComoFunciona() {

    val forma = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoCampo, forma)
            .border(1.dp, BordeCampo, forma)
            .padding(18.dp)
    ) {

        Text(
            text = "¿Cómo funciona?",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )

        Spacer(modifier = Modifier.height(14.dp))

        PasoItem(numero = "1", texto = "Elige si perdiste o encontraste un objeto")

        Spacer(modifier = Modifier.height(12.dp))

        PasoItem(numero = "2", texto = "Agrega fotos y describe el objeto")

        Spacer(modifier = Modifier.height(12.dp))

        PasoItem(numero = "3", texto = "Publica y espera a que alguien coincida")
    }
}


@Composable
private fun PasoItem(
    numero: String,
    texto: String
) {

    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(FondoEncontrado, CircleShape),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = numero,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = texto,
            fontSize = 14.sp,
            color = TextoSecundario
        )
    }
}


// ==========================================================
// CONSEJO
// ==========================================================

@Composable
private fun ConsejoPublicar() {

    val forma = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoConsejo, forma)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = "💡", fontSize = 24.sp)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Con fotos claras y un lugar aproximado es más fácil encontrar coincidencias.",
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = Color(0xFF6B5300)
        )
    }
}


// ==========================================================
// PASO 2: FORMULARIO
// ==========================================================

@Composable
private fun FormularioReporte(
    tipo: String,
    onBack: () -> Unit,
    onPublicado: () -> Unit
) {

    val contexto = LocalContext.current
    val esPerdido = tipo == "perdido"

    val titulo = if (esPerdido) "Reportar objeto perdido" else "Reportar objeto encontrado"
    val ejemploNombre = if (esPerdido) "Ej. Mochila negra" else "Ej. Llaves con llavero azul"


    // ------------------------------------------------------
    // DATOS DEL FORMULARIO
    // ------------------------------------------------------

    val fotos = remember { mutableStateListOf<Uri>() }
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf<String?>(null) }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf<String?>(null) }

    // Cuando ya intentó publicar, se marcan en rojo los campos faltantes
    var intentoPublicar by remember { mutableStateOf(false) }


    // ------------------------------------------------------
    // SELECTOR DE FOTOS (galería, hasta 3)
    // ------------------------------------------------------

    val lanzadorFotos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(3)
    ) { uris ->
        uris.forEach { uri ->
            if (fotos.size < 3 && !fotos.contains(uri)) {
                fotos.add(uri)
            }
        }
    }

    val abrirGaleria: () -> Unit = {
        if (fotos.size >= 3) {
            Toast.makeText(contexto, "Máximo 3 fotos", Toast.LENGTH_SHORT).show()
        } else {
            lanzadorFotos.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }


    // ------------------------------------------------------
    // PUBLICAR (con validación)
    // ------------------------------------------------------

    val publicar: () -> Unit = {

        intentoPublicar = true

        when {
            nombre.isBlank() ->
                Toast.makeText(contexto, "Escribe el nombre del objeto", Toast.LENGTH_SHORT).show()

            categoria == null ->
                Toast.makeText(contexto, "Selecciona una categoría", Toast.LENGTH_SHORT).show()

            ubicacion == null ->
                Toast.makeText(contexto, "Selecciona la ubicación", Toast.LENGTH_SHORT).show()

            else -> {
                // POR AHORA NO SE GUARDA EN NINGUNA BASE DE DATOS.
                // Aquí después conectaremos el guardado del reporte.
                Toast.makeText(contexto, "Reporte publicado", Toast.LENGTH_SHORT).show()
                onPublicado()
            }
        }
    }


    // ======================================================
    // PANTALLA
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {

        // ==================================================
        // FLECHA DE REGRESAR + ETIQUETA DEL TIPO
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 20.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = TextoPrincipal,
                    modifier = Modifier.size(26.dp)
                )
            }

            // ETIQUETA: PERDIDO / ENCONTRADO
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (esPerdido) FondoPerdido else FondoEncontrado)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (esPerdido) Rojo else VerdeOscuro, CircleShape)
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = if (esPerdido) "Perdido" else "Encontrado",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (esPerdido) Rojo else VerdeOscuro
                )
            }
        }


        // ==================================================
        // CONTENIDO CON SCROLL
        // ==================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = titulo,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Completa la información del objeto.",
                fontSize = 15.sp,
                color = TextoSecundario
            )

            Spacer(modifier = Modifier.height(22.dp))


            // ==================================================
            // AGREGAR FOTOS
            // ==================================================

            Etiqueta(texto = "Fotos", obligatorio = false)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(FondoCampo)
                    .drawBehind {
                        drawRoundRect(
                            color = Color(0xFFB8BEC1),
                            cornerRadius = CornerRadius(18.dp.toPx()),
                            style = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 0f)
                            )
                        )
                    }
                    .clickable { abrirGaleria() },
                contentAlignment = Alignment.Center
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(FondoEncontrado, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(text = "📷", fontSize = 26.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Agregar fotos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )

                    Text(
                        text = "(Puedes subir hasta 3)",
                        fontSize = 13.sp,
                        color = TextoSecundario
                    )
                }
            }

            // MINIATURAS
            if (fotos.isNotEmpty()) {

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    fotos.toList().forEach { uri ->

                        MiniFoto(
                            uri = uri,
                            onQuitar = { fotos.remove(uri) }
                        )
                    }

                    if (fotos.size < 3) {

                        Box(
                            modifier = Modifier
                                .size(66.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(FondoGris)
                                .clickable { abrirGaleria() },
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar otra foto",
                                tint = TextoSecundario,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))


            // ==================================================
            // NOMBRE
            // ==================================================

            Etiqueta(texto = "Nombre del objeto", obligatorio = true)

            CampoTexto(
                valor = nombre,
                onCambio = { nombre = it },
                placeholder = ejemploNombre,
                modifier = Modifier.height(56.dp),
                singleLine = true,
                error = intentoPublicar && nombre.isBlank()
            )

            Spacer(modifier = Modifier.height(20.dp))


            // ==================================================
            // CATEGORÍA
            // ==================================================

            Etiqueta(texto = "Categoría", obligatorio = true)

            CampoDesplegable(
                placeholder = "Selecciona una categoría",
                seleccion = categoria,
                opciones = categoriasFormulario,
                error = intentoPublicar && categoria == null,
                onSeleccion = { categoria = it }
            )

            Spacer(modifier = Modifier.height(20.dp))


            // ==================================================
            // DESCRIPCIÓN
            // ==================================================

            Etiqueta(texto = "Descripción", obligatorio = false)

            CampoTexto(
                valor = descripcion,
                onCambio = {
                    if (it.length <= LIMITE_DESCRIPCION) {
                        descripcion = it
                    }
                },
                placeholder = "Describe el objeto con el mayor detalle posible...",
                modifier = Modifier.height(120.dp),
                singleLine = false,
                error = false
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${descripcion.length}/$LIMITE_DESCRIPCION",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                color = TextoClaro,
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(14.dp))


            // ==================================================
            // UBICACIÓN
            // ==================================================

            Etiqueta(texto = "Ubicación aproximada", obligatorio = true)

            CampoDesplegable(
                placeholder = "Selecciona la ubicación",
                seleccion = ubicacion,
                opciones = ubicacionesFormulario,
                iconoInicio = R.drawable.location_dot_solid,
                flechaAbajo = false,
                error = intentoPublicar && ubicacion == null,
                onSeleccion = { ubicacion = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "* Campos obligatorios",
                fontSize = 12.sp,
                color = TextoClaro
            )

            Spacer(modifier = Modifier.height(24.dp))
        }


        // ==================================================
        // BOTÓN PUBLICAR (fijo abajo)
        // ==================================================

        Button(
            onClick = publicar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeOscuro
            )
        ) {

            Text(
                text = "Publicar",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


// ==========================================================
// ETIQUETA DE CAMPO (con * si es obligatorio)
// ==========================================================

@Composable
private fun Etiqueta(
    texto: String,
    obligatorio: Boolean
) {

    Row {

        Text(
            text = texto,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )

        if (obligatorio) {

            Text(
                text = " *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Rojo
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}


// ==========================================================
// CAMPO DE TEXTO (el texto que escribes sale en negro)
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
                fontSize = 15.sp,
                color = TextoClaro
            )
        },

        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 15.sp
        ),

        shape = RoundedCornerShape(14.dp),

        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            errorTextColor = Color.Black,
            focusedBorderColor = Verde,
            unfocusedBorderColor = BordeCampo,
            errorBorderColor = Rojo,
            cursorColor = Verde,
            errorCursorColor = Rojo,
            focusedContainerColor = FondoCampo,
            unfocusedContainerColor = FondoCampo,
            errorContainerColor = FondoCampo
        )
    )
}


// ==========================================================
// CAMPO DESPLEGABLE (categoría y ubicación)
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

    var abierto by remember { mutableStateOf(false) }
    val forma = RoundedCornerShape(14.dp)

    Box(modifier = Modifier.fillMaxWidth()) {

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
                .clickable { abierto = true }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (iconoInicio != null) {

                Image(
                    painter = painterResource(id = iconoInicio),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(VerdeOscuro)
                )

                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = seleccion ?: placeholder,
                modifier = Modifier.weight(1f),
                fontSize = 15.sp,
                color = if (seleccion == null) TextoClaro else Color.Black,
                maxLines = 1
            )

            Icon(
                imageVector = if (flechaAbajo) {
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
            onDismissRequest = { abierto = false },
            modifier = Modifier.background(Color.White)
        ) {

            opciones.forEach { opcion ->

                DropdownMenuItem(
                    text = {
                        Text(
                            text = opcion,
                            fontSize = 15.sp,
                            fontWeight = if (seleccion == opcion) FontWeight.Bold else FontWeight.Normal,
                            color = if (seleccion == opcion) VerdeOscuro else TextoPrincipal
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
// MINIATURA DE FOTO (con botón para quitarla)
// ==========================================================

@Composable
private fun MiniFoto(
    uri: Uri,
    onQuitar: () -> Unit
) {

    val contexto = LocalContext.current

    // Se carga reducida para no gastar memoria
    val bitmap = remember(uri) {
        runCatching {
            val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
            contexto.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opciones)
            }
        }.getOrNull()
    }

    Box(
        modifier = Modifier
            .size(66.dp)
            .clip(RoundedCornerShape(12.dp))
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
                .padding(3.dp)
                .size(20.dp)
                .background(Color(0xAA000000), CircleShape)
                .clip(CircleShape)
                .clickable { onQuitar() },
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Quitar foto",
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}