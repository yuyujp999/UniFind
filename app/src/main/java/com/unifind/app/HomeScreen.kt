package com.unifind.app

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.unifind.app.data.Catalogo
import com.unifind.app.data.EstadoReporte
import com.unifind.app.data.UniFindRepository
import com.unifind.app.ui.Aparecer
import com.unifind.app.ui.ChipFiltro
import com.unifind.app.ui.EsqueletoTarjeta
import com.unifind.app.ui.EstadoVacio
import com.unifind.app.ui.NumeroAnimado
import com.unifind.app.ui.TarjetaReporte
import com.unifind.app.ui.UF
import com.unifind.app.ui.alPresionar

// ==========================================================
// INICIO
//
// Resumen en tiempo real de lo que pasa en el campus:
// contadores, categorías más perdidas y publicaciones recientes.
// ==========================================================

private const val RECIENTES_EN_INICIO = 5

@Composable
fun HomeScreen(
    onNavegar: (String) -> Unit = {},
    onAbrirReporte: (String) -> Unit = {}
) {

    val reportes by UniFindRepository.reportes.collectAsState()
    val cargando by UniFindRepository.cargando.collectAsState()
    val error by UniFindRepository.error.collectAsState()

    var filtro by remember { mutableStateOf("Todos") }

    // ------------------------------------------------------
    // USUARIO
    // ------------------------------------------------------

    val usuario = FirebaseAuth.getInstance().currentUser

    val nombre = usuario?.displayName
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.split(" ")
        ?.firstOrNull()
        ?.replaceFirstChar { it.uppercase() }
        ?: usuario?.email
            ?.substringBefore("@")
            ?.replaceFirstChar { it.uppercase() }
        ?: "Usuario"

    // ------------------------------------------------------
    // ESTADÍSTICAS
    // ------------------------------------------------------

    val perdidos = reportes.count { it.estado == EstadoReporte.PERDIDO }
    val encontrados = reportes.count { it.estado == EstadoReporte.ENCONTRADO }
    val recuperados = reportes.count { it.estado == EstadoReporte.RECUPERADO }

    val masPerdidas = remember(reportes) {
        reportes
            .filter { it.estado == EstadoReporte.PERDIDO }
            .groupingBy { it.categoria.ifBlank { "Otros" } }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(6)
    }

    val recientes = remember(reportes, filtro) {
        reportes
            .filter {
                when (filtro) {
                    "Perdidos" -> it.esPerdido
                    "Encontrados" -> !it.esPerdido
                    else -> true
                }
            }
            .sortedByDescending { it.fechaMillis }
            .take(RECIENTES_EN_INICIO)
    }

    // ======================================================
    // PANTALLA
    // ======================================================

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.banner_dentro),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.12f))
        )

        // Degradado oscuro para que el saludo siempre se lea
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x80004D3A), Color.Transparent)
                    )
                )
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                UniFindBottomBar(
                    seleccionado = "Inicio",
                    onSeleccionar = onNavegar
                )
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(bottom = padding.calculateBottomPadding()),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                // ---------- ENCABEZADO ----------
                item {
                    Aparecer(0) {
                        Encabezado(
                            nombre = nombre,
                            onPerfil = { onNavegar("Perfil") },
                            onCampana = { onNavegar("Mis reportes") }
                        )
                    }
                }

                // ---------- BUSCADOR ----------
                item {
                    Aparecer(1) {
                        BuscadorFalso(
                            onClick = { onNavegar("Buscar") }
                        )
                    }
                }

                // ---------- CONTADORES ----------
                item {
                    Aparecer(2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            TarjetaContador(
                                modifier = Modifier.weight(1f),
                                valor = perdidos,
                                texto = "Perdidos",
                                icono = R.drawable.chart_lost,
                                color = UF.Rojo,
                                fondo = UF.RojoSuave
                            )

                            TarjetaContador(
                                modifier = Modifier.weight(1f),
                                valor = encontrados,
                                texto = "Encontrados",
                                icono = R.drawable.chart_found,
                                color = UF.Verde,
                                fondo = UF.Menta
                            )

                            TarjetaContador(
                                modifier = Modifier.weight(1f),
                                valor = recuperados,
                                texto = "Recuperados",
                                icono = R.drawable.unlock_solid,
                                color = UF.Azul,
                                fondo = UF.AzulSuave
                            )
                        }
                    }
                }

                // ---------- MÁS PERDIDAS ----------
                if (masPerdidas.isNotEmpty()) {

                    item {
                        Aparecer(3) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                                Text(
                                    text = "Lo que más se pierde",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UF.Texto,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )

                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(masPerdidas.toList()) { (cat, total) ->
                                        TarjetaCategoria(
                                            categoria = cat,
                                            total = total,
                                            onClick = { onNavegar("Buscar") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ---------- RECIENTES ----------
                item {
                    Aparecer(4) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "Publicaciones recientes",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UF.Texto,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "Ver todo",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UF.Verde,
                                    modifier = Modifier
                                        .alPresionar { onNavegar("Buscar") }
                                        .padding(4.dp)
                                )
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(listOf("Todos", "Perdidos", "Encontrados")) {
                                    ChipFiltro(
                                        texto = it,
                                        seleccionado = filtro == it,
                                        onClick = { filtro = it }
                                    )
                                }
                            }
                        }
                    }
                }

                // ---------- LISTA ----------
                when {

                    cargando -> items(3) {
                        Box(Modifier.padding(horizontal = 20.dp)) {
                            EsqueletoTarjeta()
                        }
                    }

                    error != null -> item {
                        EstadoVacio(
                            emoji = "📡",
                            titulo = "Sin conexión",
                            texto = error.orEmpty(),
                            textoBoton = "Reintentar",
                            onBoton = { UniFindRepository.reintentar() }
                        )
                    }

                    recientes.isEmpty() -> item {
                        EstadoVacio(
                            emoji = "🎒",
                            titulo = "Todo tranquilo por ahora",
                            texto = "Aún no hay publicaciones. Si perdiste o " +
                                "encontraste algo, cuéntanos.",
                            textoBoton = "Publicar",
                            onBoton = { onNavegar("Publicar") }
                        )
                    }

                    else -> items(recientes, key = { it.id }) { reporte ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .animateItem()
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
// ENCABEZADO (saludo + campana + avatar)
// ==========================================================

@Composable
private fun Encabezado(
    nombre: String,
    onPerfil: () -> Unit,
    onCampana: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = "Hola, $nombre 👋",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )

            Text(
                text = "¿Qué buscamos hoy?",
                fontSize = 14.sp,
                color = Color(0xFFE6FFF4)
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .alPresionar(onCampana)
                .background(Color.White.copy(alpha = 0.92f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.bell_solid),
                contentDescription = "Mis reportes",
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(UF.VerdeOscuro)
            )
        }

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .alPresionar(onPerfil)
                .background(Color.White, CircleShape)
                .padding(3.dp)
                .background(UF.Verde, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nombre.firstOrNull()?.uppercase() ?: "U",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ==========================================================
// BUSCADOR (abre la pantalla Buscar)
// ==========================================================

@Composable
private fun BuscadorFalso(onClick: () -> Unit) {

    val forma = RoundedCornerShape(28.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(54.dp)
            .alPresionar(onClick)
            .shadow(
                elevation = 8.dp,
                shape = forma,
                ambientColor = Color(0x33006B4A),
                spotColor = Color(0x33006B4A)
            )
            .background(Color.White, forma)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.magnifying_glass_solid),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(UF.Verde)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = "Buscar mochila, llaves, celular…",
            fontSize = 15.sp,
            color = Color(0xFF7C8585)
        )
    }
}

// ==========================================================
// CONTADOR
// ==========================================================

@Composable
private fun TarjetaContador(
    modifier: Modifier,
    valor: Int,
    texto: String,
    icono: Int,
    color: Color,
    fondo: Color
) {

    val forma = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = forma,
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .background(Color.White, forma)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(fondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                colorFilter = ColorFilter.tint(color)
            )
        }

        NumeroAnimado(valor = valor, color = UF.Texto)

        Text(
            text = texto,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = UF.TextoSec,
            maxLines = 1
        )
    }
}

// ==========================================================
// CATEGORÍA MÁS PERDIDA
// ==========================================================

@Composable
private fun TarjetaCategoria(
    categoria: String,
    total: Int,
    onClick: () -> Unit
) {

    val forma = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .alPresionar(onClick)
            .shadow(
                elevation = 3.dp,
                shape = forma,
                ambientColor = Color(0x22000000),
                spotColor = Color(0x22000000)
            )
            .background(Color.White, forma)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = Catalogo.emoji(categoria), fontSize = 26.sp)

        Spacer(Modifier.width(10.dp))

        Column {

            Text(
                text = categoria,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = UF.Texto
            )

            Text(
                text = "$total perdido" + if (total == 1) "" else "s",
                fontSize = 12.sp,
                color = UF.Rojo,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
