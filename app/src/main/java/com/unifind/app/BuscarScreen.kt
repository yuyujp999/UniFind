package com.unifind.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unifind.app.data.Catalogo
import com.unifind.app.data.EstadoReporte
import com.unifind.app.data.Reporte
import com.unifind.app.data.UniFindRepository
import com.unifind.app.ui.ChipFiltro
import com.unifind.app.ui.EsqueletoTarjeta
import com.unifind.app.ui.EstadoVacio
import com.unifind.app.ui.TarjetaReporte
import com.unifind.app.ui.normalizar

// ==========================================================
// COLORES UNIFIND
// ==========================================================
private val BuscarFondo = Color(0xFFF4FAF7)
private val BuscarVerde = Color(0xFF00785A)
private val BuscarVerdeOscuro = Color(0xFF00533F)
private val BuscarMenta = Color(0xFFE1F4EB)
private val BuscarTexto = Color(0xFF183344)
private val BuscarGris = Color(0xFF71818B)
private val BuscarRojo = Color(0xFFE84D63)
private val BuscarNaranja = Color(0xFFF28C28)
private val BuscarNaranjaSuave = Color(0xFFFFF1E2)

private enum class Orden(val texto: String) {
    RECIENTES("Más recientes"),
    ANTIGUOS("Más antiguos")
}

private data class EstiloCategoria(
    val icono: ImageVector,
    val fondo: Color,
    val color: Color
)

private fun estiloDeCategoria(nombre: String): EstiloCategoria {
    return when (normalizar(nombre)) {
        "mochilas", "bolsas", "maletas" -> EstiloCategoria(
            Icons.Default.Inventory2, Color(0xFFDDF4E9), Color(0xFF08785A)
        )
        "celulares", "telefonos", "telefonos moviles" -> EstiloCategoria(
            Icons.Default.Smartphone, Color(0xFFE5F0FF), Color(0xFF2879DB)
        )
        "llaves" -> EstiloCategoria(
            Icons.Default.Key, Color(0xFFFFF2D6), Color(0xFFD99800)
        )
        "ropa", "prendas" -> EstiloCategoria(
            Icons.Default.Checkroom, Color(0xFFF0E7FF), Color(0xFF8655D5)
        )
        "laptops", "computadoras", "electronicos" -> EstiloCategoria(
            Icons.Default.Laptop, Color(0xFFE5F0FF), Color(0xFF347BE0)
        )
        "audifonos", "accesorios", "audio" -> EstiloCategoria(
            Icons.Default.Headphones, Color(0xFFFFE8ED), Color(0xFFE84863)
        )
        "relojes" -> EstiloCategoria(
            Icons.Default.Watch, Color(0xFFFFF0DC), Color(0xFFB87516)
        )
        "libros", "cuadernos", "material escolar" -> EstiloCategoria(
            Icons.Default.MenuBook, Color(0xFFE5F5EE), Color(0xFF178267)
        )
        else -> EstiloCategoria(
            Icons.Default.Category, Color(0xFFEAF1F0), Color(0xFF54736A)
        )
    }
}

// ==========================================================
// BUSCAR SCREEN
// ==========================================================
@Composable
fun BuscarScreen(
    onNavegar: (String) -> Unit = {},
    onAbrirReporte: (String) -> Unit = {}
) {
    val reportes by UniFindRepository.reportes.collectAsState()
    val cargando by UniFindRepository.cargando.collectAsState()
    val error by UniFindRepository.error.collectAsState()
    val foco = LocalFocusManager.current

    var consulta by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf<String?>(null) }
    var ubicacion by rememberSaveable { mutableStateOf<String?>(null) }
    var estado by rememberSaveable { mutableStateOf<String?>(null) }
    var orden by rememberSaveable { mutableStateOf(Orden.RECIENTES) }
    var verFiltros by rememberSaveable { mutableStateOf(false) }

    val filtrosActivos = listOf(categoria, ubicacion, estado).count { it != null } +
            if (orden != Orden.RECIENTES) 1 else 0

    val resultados: List<Reporte> = remember(
        reportes, consulta, categoria, ubicacion, estado, orden
    ) {
        val palabras = normalizar(consulta).split(" ").filter { it.isNotBlank() }
        val filtrados = reportes.filter { reporte ->
            val textoReporte = normalizar(
                "${reporte.titulo} ${reporte.descripcion} ${reporte.categoria} ${reporte.ubicacion}"
            )
            val coincideTexto = palabras.isEmpty() || palabras.all { palabra ->
                textoReporte.contains(palabra)
            }
            coincideTexto &&
                    (categoria == null || normalizar(reporte.categoria) == normalizar(categoria!!)) &&
                    (ubicacion == null || reporte.ubicacion == ubicacion) &&
                    (estado == null || reporte.estado.etiqueta == estado)
        }
        when (orden) {
            Orden.RECIENTES -> filtrados.sortedByDescending { it.fechaMillis }
            Orden.ANTIGUOS -> filtrados.sortedBy { it.fechaMillis }
        }
    }

    val mostrarResultados = consulta.isNotBlank() || categoria != null ||
            ubicacion != null || estado != null || orden != Orden.RECIENTES

    fun limpiarBusqueda() {
        consulta = ""
        categoria = null
        ubicacion = null
        estado = null
        orden = Orden.RECIENTES
        verFiltros = false
        foco.clearFocus()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BuscarFondo,
        bottomBar = {
            UniFindBottomBar(seleccionado = "Buscar", onSeleccionar = onNavegar)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BuscarFondo)
                .padding(bottom = padding.calculateBottomPadding()),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 54.dp,
                bottom = 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(key = "titulo_buscar") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Buscar",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111111),
                        modifier = Modifier.padding(start = 2.dp, top = 2.dp, bottom = 2.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 1.dp)
                            .height(1.dp)
                            .background(Color(0xFFDCE8E2))
                    )
                }
            }

            item(key = "campo_busqueda") {
                CampoBusquedaMejorado(
                    valor = consulta,
                    onCambio = { consulta = it },
                    filtrosActivos = filtrosActivos,
                    filtrosVisibles = verFiltros,
                    onFiltros = {
                        verFiltros = !verFiltros
                        foco.clearFocus()
                    },
                    onBuscar = { foco.clearFocus() },
                    onLimpiarTexto = {
                        consulta = ""
                        foco.clearFocus()
                    }
                )
            }

            item(key = "filtros_animados") {
                AnimatedVisibility(
                    visible = verFiltros,
                    enter = expandVertically(tween(260)) + fadeIn(tween(260)) +
                            slideInVertically(initialOffsetY = { -it / 8 }, animationSpec = tween(260)),
                    exit = shrinkVertically(tween(180)) + fadeOut(tween(160))
                ) {
                    PanelFiltrosBuscar(
                        categoria = categoria,
                        onCategoria = { categoria = if (categoria == it) null else it },
                        ubicacion = ubicacion,
                        onUbicacion = { ubicacion = if (ubicacion == it) null else it },
                        estado = estado,
                        onEstado = { estado = if (estado == it) null else it },
                        orden = orden,
                        onOrden = { orden = it }
                    )
                }
            }

            if (!mostrarResultados) {
                item(key = "encabezado_categorias") {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(5.dp)
                                    .height(25.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(BuscarNaranja)
                            )
                            Text(
                                text = "Categorías",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF171717)
                            )
                        }
                        Text(
                            text = "Encuentra rápidamente lo que buscas",
                            fontSize = 13.sp,
                            color = BuscarGris
                        )
                    }
                }

                item(key = "tarjeta_unica_categorias") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFDCE8E2)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Catalogo.categorias.forEachIndexed { index, elemento ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(tween(300, delayMillis = index * 45)) +
                                            slideInVertically(
                                                initialOffsetY = { it / 5 },
                                                animationSpec = tween(320, delayMillis = index * 45)
                                            )
                                ) {
                                    CategoriaBuscarFila(
                                        nombre = elemento.nombre,
                                        seleccionada = categoria == elemento.nombre,
                                        onClick = {
                                            categoria = if (categoria == elemento.nombre) null else elemento.nombre
                                            foco.clearFocus()
                                        }
                                    )
                                }
                                if (index < Catalogo.categorias.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 52.dp, end = 4.dp)
                                            .height(1.dp)
                                            .background(Color(0xFFEAF0ED))
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                item(key = "encabezado_resultados") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Resultados encontrados",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF171717)
                            )
                            Text(
                                text = if (cargando) "Buscando..." else
                                    "${resultados.size} resultado${if (resultados.size == 1) "" else "s"}",
                                fontSize = 12.sp,
                                color = BuscarGris
                            )
                        }
                        Text(
                            text = "Limpiar",
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { limpiarBusqueda() }
                                .padding(9.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BuscarVerde
                        )
                    }
                }
                when {
                    cargando -> items(3) { EsqueletoTarjeta() }
                    error != null -> item(key = "error") {
                        EstadoVacio(
                            emoji = "",
                            titulo = "Sin conexión",
                            texto = error.orEmpty(),
                            textoBoton = "Reintentar",
                            onBoton = { UniFindRepository.reintentar() }
                        )
                    }
                    resultados.isEmpty() -> item(key = "sin_resultados") {
                        EstadoVacio(
                            emoji = "",
                            titulo = "No encontramos resultados",
                            texto = "Prueba con otro nombre o cambia los filtros.",
                            textoBoton = "Limpiar búsqueda",
                            onBoton = { limpiarBusqueda() }
                        )
                    }
                    else -> items(items = resultados, key = { it.id }) { reporte ->
                        TarjetaReporte(
                            reporte = reporte,
                            onClick = {
                                foco.clearFocus()
                                onAbrirReporte(reporte.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================================
// CAMPO DE BÚSQUEDA
// ==========================================================
@Composable
private fun CampoBusquedaMejorado(
    valor: String,
    onCambio: (String) -> Unit,
    filtrosActivos: Int,
    filtrosVisibles: Boolean,
    onFiltros: () -> Unit,
    onBuscar: () -> Unit,
    onLimpiarTexto: () -> Unit
) {
    val forma = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(
                elevation = 3.dp,
                shape = forma,
                ambientColor = Color(0x22004D3A),
                spotColor = Color(0x22004D3A)
            )
            .clip(forma)
            .background(Color.White)
            .padding(start = 12.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Buscar objeto",
            tint = BuscarVerde,
            modifier = Modifier.size(21.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (valor.isEmpty()) {
                Text(
                    text = "Mochila, llaves, celular...",
                    fontSize = 13.sp,
                    color = Color(0xFF98A5A6),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            BasicTextField(
                value = valor,
                onValueChange = onCambio,
                singleLine = true,
                textStyle = TextStyle(fontSize = 13.sp, color = BuscarTexto),
                cursorBrush = Brush.verticalGradient(listOf(BuscarVerde, BuscarVerde)),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onBuscar() }),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (valor.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(29.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onLimpiarTexto),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Borrar búsqueda",
                    tint = BuscarGris,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (filtrosVisibles || filtrosActivos > 0) BuscarVerde else BuscarMenta)
                .clickable(onClick = onFiltros),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Filtros avanzados",
                tint = if (filtrosVisibles || filtrosActivos > 0) Color.White else BuscarVerde,
                modifier = Modifier.size(19.dp)
            )
            if (filtrosActivos > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(15.dp)
                        .background(BuscarRojo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filtrosActivos.toString(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ==========================================================
// FILA COMPACTA DE CATEGORÍA CON ANIMACIÓN
// ==========================================================
@Composable
private fun CategoriaBuscarFila(
    nombre: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    val estilo = estiloDeCategoria(nombre)
    val escala by animateFloatAsState(
        targetValue = if (seleccionada) 1.01f else 1f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "escalaFilaCategoria"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = escala; scaleY = escala }
            .clip(RoundedCornerShape(14.dp))
            .background(if (seleccionada) Color(0xFFFFF5E9) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 5.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(estilo.fondo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = estilo.icono,
                contentDescription = nombre,
                tint = estilo.color,
                modifier = Modifier.size(23.dp)
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = nombre,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2925),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (seleccionada) "Categoría seleccionada" else "Toca para ver objetos",
                fontSize = 11.sp,
                color = if (seleccionada) BuscarNaranja else BuscarGris,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (seleccionada) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Quitar categoría",
                tint = BuscarNaranja,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF9AA9A3),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

// ==========================================================
// TARJETA DE CATEGORÍA
// ==========================================================
@Composable
private fun CategoriaBuscarCard(
    nombre: String,
    seleccionada: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val estilo = estiloDeCategoria(nombre)
    val escala by animateFloatAsState(
        targetValue = if (seleccionada) 1.015f else 1f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "escalaCategoria"
    )
    val elevacion by animateFloatAsState(
        targetValue = if (seleccionada) 5f else 1f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "elevacionCategoria"
    )
    Card(
        modifier = modifier
            .graphicsLayer { scaleX = escala; scaleY = escala }
            .aspectRatio(1.85f)
            .shadow(elevacion.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (seleccionada) 1.5.dp else 1.dp,
            color = if (seleccionada) BuscarNaranja.copy(alpha = 0.8f) else Color(0xFFD7E3DE)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) Color(0xFFFFF5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(estilo.fondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = estilo.icono,
                    contentDescription = nombre,
                    tint = estilo.color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nombre,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BuscarTexto,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = if (seleccionada) "Seleccionada" else "Ver objetos",
                    fontSize = 10.sp,
                    fontWeight = if (seleccionada) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (seleccionada) BuscarNaranja else BuscarGris,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ==========================================================
// FILTROS AVANZADOS
// ==========================================================
@Composable
private fun PanelFiltrosBuscar(
    categoria: String?,
    onCategoria: (String) -> Unit,
    ubicacion: String?,
    onUbicacion: (String) -> Unit,
    estado: String?,
    onEstado: (String) -> Unit,
    orden: Orden,
    onOrden: (Orden) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FilaFiltroBuscar("Categoría") {
            items(Catalogo.categorias) { elemento ->
                ChipFiltro(
                    texto = elemento.nombre,
                    seleccionado = categoria == elemento.nombre,
                    onClick = { onCategoria(elemento.nombre) }
                )
            }
        }
        FilaFiltroBuscar("Ubicación") {
            items(Catalogo.ubicaciones) { lugar ->
                ChipFiltro(
                    texto = lugar,
                    seleccionado = ubicacion == lugar,
                    onClick = { onUbicacion(lugar) }
                )
            }
        }
        FilaFiltroBuscar("Estado") {
            items(EstadoReporte.entries.toList()) { elemento ->
                ChipFiltro(
                    texto = elemento.corto,
                    seleccionado = estado == elemento.etiqueta,
                    onClick = { onEstado(elemento.etiqueta) }
                )
            }
        }
        FilaFiltroBuscar("Ordenar por") {
            items(Orden.entries.toList()) { elemento ->
                ChipFiltro(
                    texto = elemento.texto,
                    seleccionado = orden == elemento,
                    onClick = { onOrden(elemento) }
                )
            }
        }
    }
}

@Composable
private fun FilaFiltroBuscar(
    titulo: String,
    contenido: LazyListScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = titulo,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = BuscarVerdeOscuro,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = contenido
        )
    }
}
