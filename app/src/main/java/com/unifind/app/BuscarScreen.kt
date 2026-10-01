package com.unifind.app

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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

private val FondoEncontrado = Color(0xFFE2F6EB)
private val FondoGris = Color(0xFFF3F5F5)

private val TextoPrincipal = Color(0xFF202124)
private val TextoSecundario = Color(0xFF5F6368)
private val TextoClaro = Color(0xFF8A9094)


// ==========================================================
// MODELOS
// ==========================================================

private data class Categoria(
    val nombre: String,
    val emoji: String
)

private data class ResultadoBusqueda(
    val titulo: String,
    val ubicacion: String,
    val tiempo: String,
    val minutos: Int,          // sirve para ordenar por reciente
    val encontrado: Boolean,
    val categoria: String,
    val imagen: Int? = null    // R.drawable.foto_xxx cuando tengas fotos
)


// ==========================================================
// PANTALLA BUSCAR OBJETOS
// ==========================================================

@Composable
fun BuscarScreen(
    onNavegar: (String) -> Unit = {}
) {

    // ------------------------------------------------------
    // CATEGORÍAS
    //
    // Por ahora usan emojis para que compile sin iconos extra.
    // Si luego agregas tus iconos a res/drawable, se pueden
    // cambiar por Image(painterResource(...)).
    // ------------------------------------------------------

    val categorias = listOf(
        Categoria("Mochilas", "🎒"),
        Categoria("Electrónicos", "📱"),
        Categoria("Llaves", "🔑"),
        Categoria("Ropa", "👕"),
        Categoria("Accesorios", "👓"),
        Categoria("Documentos", "📄"),
        Categoria("Libros", "📚"),
        Categoria("Otros", "⋯")
    )


    // ------------------------------------------------------
    // DATOS DE EJEMPLO (12 resultados)
    // ------------------------------------------------------

    val todos = listOf(
        ResultadoBusqueda("Mochila gris", "Edificio B", "Hace 5 horas", 300, false, "Mochilas"),
        ResultadoBusqueda("Tarjeta universitaria", "Aulas", "Hace 5 horas", 301, true, "Documentos"),
        ResultadoBusqueda("Audífonos inalámbricos", "Cafetería", "Hace 3 horas", 180, true, "Electrónicos"),
        ResultadoBusqueda("Llaves con llavero azul", "Biblioteca", "Hace 2 horas", 120, true, "Llaves"),
        ResultadoBusqueda("Celular Samsung", "Cafetería", "Hace 1 día", 1440, true, "Electrónicos"),
        ResultadoBusqueda("Chamarra azul", "Gimnasio", "Hace 2 días", 2880, false, "Ropa"),
        ResultadoBusqueda("Libro de Cálculo", "Biblioteca", "Hace 1 día", 1500, false, "Libros"),
        ResultadoBusqueda("Lentes de sol", "Estacionamiento", "Hace 4 horas", 240, false, "Accesorios"),
        ResultadoBusqueda("Credencial de estudiante", "Edificio A", "Hace 6 horas", 360, false, "Documentos"),
        ResultadoBusqueda("Cargador USB-C", "Aula 5", "Hace 7 horas", 420, true, "Electrónicos"),
        ResultadoBusqueda("Gorra negra", "Cancha", "Hace 3 días", 4320, false, "Ropa"),
        ResultadoBusqueda("Cuaderno verde", "Cafetería", "Hace 2 días", 2900, true, "Libros")
    )


    // ------------------------------------------------------
    // ESTADOS DE LA PANTALLA
    // ------------------------------------------------------

    var consulta by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Todos") }
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }
    var orden by remember { mutableStateOf("Más recientes") }
    var menuAbierto by remember { mutableStateOf(false) }

    val favoritos = remember { mutableStateListOf<String>() }


    // ------------------------------------------------------
    // FILTRADO Y ORDEN
    // ------------------------------------------------------

    val resultados = todos
        .filter {
            when (estado) {
                "Perdidos" -> !it.encontrado
                "Encontrados" -> it.encontrado
                else -> true
            }
        }
        .filter {
            categoriaSeleccionada == null || it.categoria == categoriaSeleccionada
        }
        .filter {
            val texto = consulta.trim()
            texto.isEmpty() ||
                    it.titulo.contains(texto, ignoreCase = true) ||
                    it.ubicacion.contains(texto, ignoreCase = true) ||
                    it.categoria.contains(texto, ignoreCase = true)
        }
        .let { lista ->
            when (orden) {
                "Más antiguos" -> lista.sortedByDescending { it.minutos }
                "Nombre (A-Z)" -> lista.sortedBy { it.titulo }
                else -> lista.sortedBy { it.minutos }
            }
        }


    // ======================================================
    // SCAFFOLD
    // ======================================================

    Scaffold(

        containerColor = Color.White,

        bottomBar = {
            UniFindBottomBar(
                seleccionado = "Buscar",
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
                text = "Buscar objetos",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))


            // ==================================================
            // BUSCADOR + BOTÓN DE FILTROS
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = consulta,
                    onValueChange = { consulta = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    singleLine = true,

                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.magnifying_glass_solid),
                            contentDescription = "Buscar",
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(TextoClaro)
                        )
                    },

                    trailingIcon = if (consulta.isNotEmpty()) {
                        {
                            Text(
                                text = "✕",
                                fontSize = 16.sp,
                                color = TextoClaro,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .clickable { consulta = "" }
                                    .padding(10.dp)
                            )
                        }
                    } else {
                        null
                    },

                    placeholder = {
                        Text(
                            text = "Buscar por palabra clave...",
                            fontSize = 15.sp,
                            color = TextoClaro
                        )
                    },

                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 15.sp
                    ),

                    shape = RoundedCornerShape(16.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Verde,
                        unfocusedBorderColor = Color(0xFFE3E6E7),
                        cursorColor = Verde,
                        focusedContainerColor = FondoGris,
                        unfocusedContainerColor = FondoGris
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                // BOTÓN DE FILTROS
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(FondoGris)
                        .clickable {
                            // Después abriremos filtros avanzados
                        },
                    contentAlignment = Alignment.Center
                ) {
                    FiltroIcono(color = TextoPrincipal)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))


            // ==================================================
            // FILTROS DE ESTADO
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                listOf("Todos", "Perdidos", "Encontrados").forEach { opcion ->

                    EstadoChip(
                        modifier = Modifier.weight(1f),
                        text = opcion,
                        selected = estado == opcion,
                        onClick = { estado = opcion }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))


            // ==================================================
            // CATEGORÍAS (CUADRÍCULA 4 x 2)
            // ==================================================

            categorias.chunked(4).forEach { fila ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    fila.forEach { categoria ->

                        CategoriaItem(
                            modifier = Modifier.weight(1f),
                            categoria = categoria,
                            seleccionada = categoriaSeleccionada == categoria.nombre,
                            onClick = {
                                // Si tocas la misma categoría, se quita el filtro
                                categoriaSeleccionada =
                                    if (categoriaSeleccionada == categoria.nombre) {
                                        null
                                    } else {
                                        categoria.nombre
                                    }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))


            // ==================================================
            // ENCABEZADO DE RESULTADOS + ORDENAR
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Resultados (${resultados.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Box {

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFDADEDF), RoundedCornerShape(12.dp))
                            .clickable { menuAbierto = true }
                            .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Ordenar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Ordenar",
                            tint = TextoPrincipal,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false },
                        modifier = Modifier.background(Color.White)
                    ) {

                        listOf("Más recientes", "Más antiguos", "Nombre (A-Z)").forEach { opcion ->

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opcion,
                                        fontSize = 15.sp,
                                        fontWeight = if (orden == opcion) FontWeight.Bold else FontWeight.Normal,
                                        color = if (orden == opcion) VerdeOscuro else TextoPrincipal
                                    )
                                },
                                onClick = {
                                    orden = opcion
                                    menuAbierto = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            // ==================================================
            // LISTA DE RESULTADOS
            // ==================================================

            if (resultados.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(text = "🔍", fontSize = 40.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "No encontramos objetos",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )

                    Text(
                        text = "Prueba con otra palabra o quita algún filtro",
                        fontSize = 14.sp,
                        color = TextoSecundario,
                        textAlign = TextAlign.Center
                    )
                }

            } else {

                resultados.forEach { resultado ->

                    ResultadoCard(
                        resultado = resultado,
                        favorito = favoritos.contains(resultado.titulo),
                        onFavorito = {
                            if (favoritos.contains(resultado.titulo)) {
                                favoritos.remove(resultado.titulo)
                            } else {
                                favoritos.add(resultado.titulo)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}


// ==========================================================
// CHIP DE ESTADO (TODOS / PERDIDOS / ENCONTRADOS)
// ==========================================================

@Composable
private fun EstadoChip(
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
// CATEGORÍA
// ==========================================================

@Composable
private fun CategoriaItem(
    modifier: Modifier,
    categoria: Categoria,
    seleccionada: Boolean,
    onClick: () -> Unit
) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (seleccionada) FondoEncontrado else FondoGris)
                .border(
                    width = if (seleccionada) 2.dp else 0.dp,
                    color = if (seleccionada) Verde else Color.Transparent,
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = categoria.emoji,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = categoria.nombre,
            fontSize = 12.sp,
            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium,
            color = if (seleccionada) VerdeOscuro else TextoPrincipal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


// ==========================================================
// TARJETA DE RESULTADO
// ==========================================================

@Composable
private fun ResultadoCard(
    resultado: ResultadoBusqueda,
    favorito: Boolean,
    onFavorito: () -> Unit
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
                // Después abriremos el detalle del objeto
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // IMAGEN
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFEDEDED)),
            contentAlignment = Alignment.Center
        ) {

            if (resultado.imagen != null) {

                Image(
                    painter = painterResource(id = resultado.imagen),
                    contentDescription = resultado.titulo,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )

            } else {

                Image(
                    painter = painterResource(id = R.drawable.file_solid),
                    contentDescription = resultado.titulo,
                    modifier = Modifier.size(34.dp),
                    colorFilter = ColorFilter.tint(Color(0xFFB0B5B8))
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // INFORMACIÓN
        Column(modifier = Modifier.weight(1f)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                StatusChip(encontrado = resultado.encontrado)

                Icon(
                    imageVector = if (favorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (favorito) Rojo else TextoClaro,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .clickable { onFavorito() }
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = resultado.titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // UBICACIÓN
            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(id = R.drawable.location_dot_solid),
                    contentDescription = "Ubicación",
                    modifier = Modifier.size(14.dp),
                    colorFilter = ColorFilter.tint(Verde)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = resultado.ubicacion,
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // HORA
            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(id = R.drawable.clock_solid),
                    contentDescription = "Hora",
                    modifier = Modifier.size(14.dp),
                    colorFilter = ColorFilter.tint(Verde)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = resultado.tiempo,
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    maxLines = 1
                )
            }
        }
    }
}


// ==========================================================
// ICONO DE FILTROS (dibujado, no necesita archivo)
// ==========================================================

@Composable
private fun FiltroIcono(color: Color) {

    Canvas(modifier = Modifier.size(24.dp)) {

        val grosor = 2.dp.toPx()
        val radio = 4.dp.toPx()

        // Cada par es: altura de la línea, posición de la bolita
        val lineas = listOf(
            0.20f to 0.68f,
            0.50f to 0.30f,
            0.80f to 0.60f
        )

        lineas.forEach { (alto, posicion) ->

            val y = size.height * alto
            val x = size.width * posicion

            drawLine(
                color = color,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = grosor,
                cap = StrokeCap.Round
            )

            drawCircle(
                color = FondoGris,
                radius = radio,
                center = Offset(x, y)
            )

            drawCircle(
                color = color,
                radius = radio,
                center = Offset(x, y),
                style = Stroke(width = grosor)
            )
        }
    }
}