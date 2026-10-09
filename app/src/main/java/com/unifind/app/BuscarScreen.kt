package com.unifind.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unifind.app.data.Catalogo
import com.unifind.app.data.EstadoReporte
import com.unifind.app.data.Reporte
import com.unifind.app.data.UniFindRepository
import com.unifind.app.ui.Aparecer
import com.unifind.app.ui.ChipFiltro
import com.unifind.app.ui.EncabezadoOla
import com.unifind.app.ui.EsqueletoTarjeta
import com.unifind.app.ui.EstadoVacio
import com.unifind.app.ui.TarjetaReporte
import com.unifind.app.ui.UF
import com.unifind.app.ui.alPresionar
import com.unifind.app.ui.normalizar

// ==========================================================
// BUSCAR OBJETOS
//
// F04 - Buscar objetos (texto libre)
// F05 - Filtrar publicaciones (tipo, categoría, ubicación,
//       estado y orden)
// ==========================================================

private enum class Orden(val texto: String) {
    RECIENTES("Más recientes"),
    ANTIGUOS("Más antiguos")
}

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
    var tipo by rememberSaveable { mutableStateOf("Todos") }
    var categoria by rememberSaveable { mutableStateOf<String?>(null) }
    var ubicacion by rememberSaveable { mutableStateOf<String?>(null) }
    var estado by rememberSaveable { mutableStateOf<String?>(null) }
    var orden by rememberSaveable { mutableStateOf(Orden.RECIENTES) }
    var verFiltros by rememberSaveable { mutableStateOf(false) }

    val filtrosActivos =
        listOf(categoria, ubicacion, estado).count { it != null } +
            (if (orden != Orden.RECIENTES) 1 else 0)

    // ------------------------------------------------------
    // FILTRADO
    // ------------------------------------------------------

    val resultados: List<Reporte> = remember(
        reportes, consulta, tipo, categoria, ubicacion, estado, orden
    ) {

        val palabras = normalizar(consulta)
            .split(" ")
            .filter { it.isNotBlank() }

        val filtrados = reportes.filter { r ->

            val coincideTexto = palabras.isEmpty() || run {
                val pajar = normalizar(
                    "${r.titulo} ${r.descripcion} ${r.categoria} ${r.ubicacion}"
                )
                palabras.all { pajar.contains(it) }
            }

            val coincideTipo = when (tipo) {
                "Perdidos" -> r.esPerdido
                "Encontrados" -> !r.esPerdido
                else -> true
            }

            coincideTexto &&
                coincideTipo &&
                (categoria == null || r.categoria == categoria) &&
                (ubicacion == null || r.ubicacion == ubicacion) &&
                (estado == null || r.estado.etiqueta == estado)
        }

        if (orden == Orden.RECIENTES) {
            filtrados.sortedByDescending { it.fechaMillis }
        } else {
            filtrados.sortedBy { it.fechaMillis }
        }
    }

    fun limpiar() {
        consulta = ""
        tipo = "Todos"
        categoria = null
        ubicacion = null
        estado = null
        orden = Orden.RECIENTES
    }

    // ======================================================
    // PANTALLA
    // ======================================================

    Scaffold(
        containerColor = Color(0xFFF7FAF9),
        bottomBar = {
            UniFindBottomBar(
                seleccionado = "Buscar",
                onSeleccionar = onNavegar
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .imePadding()
        ) {

            Box {

                EncabezadoOla(
                    titulo = "Buscar objetos",
                    subtitulo = "Encuentra lo que perdiste o ayuda a otros",
                    alto = 150.dp
                )

                // Barra de búsqueda que flota sobre el borde de la ola
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 26.dp)
                        .padding(horizontal = 20.dp)
                ) {

                    CampoBusqueda(
                        valor = consulta,
                        onCambio = { consulta = it },
                        filtrosActivos = filtrosActivos,
                        filtrosVisibles = verFiltros,
                        onFiltros = { verFiltros = !verFiltros },
                        onBuscar = { foco.clearFocus() }
                    )
                }
            }

            Spacer(Modifier.height(38.dp))

            // ---------- TIPO ----------
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listOf("Todos", "Perdidos", "Encontrados")) { opcion ->
                    ChipFiltro(
                        texto = opcion,
                        seleccionado = tipo == opcion,
                        onClick = { tipo = opcion }
                    )
                }
            }

            // ---------- PANEL DE FILTROS (F05) ----------
            AnimatedVisibility(
                visible = verFiltros,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(250)) + fadeOut(tween(200))
            ) {

                PanelFiltros(
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

            Spacer(Modifier.height(12.dp))

            // ---------- CONTADOR ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (cargando) "Buscando…"
                    else "${resultados.size} resultado" +
                        if (resultados.size == 1) "" else "s",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = UF.TextoSec,
                    modifier = Modifier.weight(1f)
                )

                if (consulta.isNotEmpty() || tipo != "Todos" || filtrosActivos > 0) {

                    Text(
                        text = "Limpiar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = UF.Verde,
                        modifier = Modifier
                            .alPresionar { limpiar() }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ---------- RESULTADOS ----------
            when {

                cargando -> Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(4) { EsqueletoTarjeta() }
                }

                error != null -> EstadoVacio(
                    emoji = "📡",
                    titulo = "Sin conexión",
                    texto = error.orEmpty(),
                    textoBoton = "Reintentar",
                    onBoton = { UniFindRepository.reintentar() }
                )

                resultados.isEmpty() -> EstadoVacio(
                    emoji = "🔍",
                    titulo = "No encontramos nada",
                    texto = if (reportes.isEmpty())
                        "Aún no hay publicaciones. ¡Sé la primera persona en publicar!"
                    else
                        "Prueba con otras palabras o quita algunos filtros.",
                    textoBoton = if (reportes.isEmpty()) "Publicar" else "Limpiar filtros",
                    onBoton = {
                        if (reportes.isEmpty()) onNavegar("Publicar") else limpiar()
                    }
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp, end = 20.dp, bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(resultados, key = { it.id }) { reporte ->

                        Aparecer(
                            indice = resultados.indexOf(reporte).coerceAtMost(6),
                            modifier = Modifier.animateItem()
                        ) {
                            TarjetaReporte(
                                reporte = reporte,
                                onClick = { onAbrirReporte(reporte.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================================
// CAMPO DE BÚSQUEDA (texto en negro)
// ==========================================================

@Composable
private fun CampoBusqueda(
    valor: String,
    onCambio: (String) -> Unit,
    filtrosActivos: Int,
    filtrosVisibles: Boolean,
    onFiltros: () -> Unit,
    onBuscar: () -> Unit
) {

    val forma = RoundedCornerShape(28.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 10.dp,
                shape = forma,
                ambientColor = Color(0x33006B4A),
                spotColor = Color(0x33006B4A)
            )
            .background(Color.White, forma)
            .padding(start = 18.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.magnifying_glass_solid),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(UF.Verde)
        )

        Spacer(Modifier.width(12.dp))

        Box(modifier = Modifier.weight(1f)) {

            if (valor.isEmpty()) {
                Text(
                    text = "Mochila, llaves, audífonos…",
                    fontSize = 16.sp,
                    color = Color(0xFF9AA3A6),
                    maxLines = 1
                )
            }

            BasicTextField(
                value = valor,
                onValueChange = onCambio,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                ),
                cursorBrush = SolidColor(UF.Verde),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onBuscar() }),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (valor.isNotEmpty()) {

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .alPresionar { onCambio("") },
                contentAlignment = Alignment.Center
            ) {
                Text("✕", fontSize = 16.sp, color = UF.TextoSec)
            }
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .alPresionar(onFiltros)
                .background(
                    if (filtrosVisibles || filtrosActivos > 0) UF.Verde else UF.Menta,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.sliders_solid),
                contentDescription = "Filtros",
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(
                    if (filtrosVisibles || filtrosActivos > 0) Color.White
                    else UF.VerdeOscuro
                )
            )

            if (filtrosActivos > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                        .background(UF.Rojo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filtrosActivos.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ==========================================================
// PANEL DE FILTROS (F05)
// ==========================================================

@Composable
private fun PanelFiltros(
    categoria: String?,
    onCategoria: (String) -> Unit,
    ubicacion: String?,
    onUbicacion: (String) -> Unit,
    estado: String?,
    onEstado: (String) -> Unit,
    orden: Orden,
    onOrden: (Orden) -> Unit
) {

    val forma = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(
                elevation = 3.dp,
                shape = forma,
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .background(Color.White, forma)
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        FilaFiltro("Categoría") {
            items(Catalogo.categorias) { c ->
                ChipFiltro(
                    texto = "${c.emoji} ${c.nombre}",
                    seleccionado = categoria == c.nombre,
                    onClick = { onCategoria(c.nombre) }
                )
            }
        }

        FilaFiltro("Ubicación") {
            items(Catalogo.ubicaciones) { u ->
                ChipFiltro(
                    texto = u,
                    seleccionado = ubicacion == u,
                    onClick = { onUbicacion(u) }
                )
            }
        }

        FilaFiltro("Estado") {
            items(EstadoReporte.entries.toList()) { e ->
                ChipFiltro(
                    texto = e.corto,
                    seleccionado = estado == e.etiqueta,
                    onClick = { onEstado(e.etiqueta) }
                )
            }
        }

        FilaFiltro("Ordenar por") {
            items(Orden.entries.toList()) { o ->
                ChipFiltro(
                    texto = o.texto,
                    seleccionado = orden == o,
                    onClick = { onOrden(o) }
                )
            }
        }
    }
}

@Composable
private fun FilaFiltro(
    titulo: String,
    contenido: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

        Text(
            text = titulo,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = UF.VerdeOscuro,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = contenido
        )
    }
}
