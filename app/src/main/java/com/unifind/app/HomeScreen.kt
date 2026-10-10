
package com.unifind.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.unifind.app.data.EstadoReporte
import com.unifind.app.data.Reporte
import com.unifind.app.data.UniFindRepository
import com.unifind.app.ui.NumeroAnimado
import kotlinx.coroutines.delay
import java.util.Calendar

// ==========================================================
// COLORES UNIFIND
// ==========================================================

private val FondoHome = Color(0xFFF4FAF7)
private val VerdePrincipal = Color(0xFF00765A)
private val VerdeOscuroHome = Color(0xFF00523F)
private val VerdeClaroHome = Color(0xFFE3F5ED)
private val TextoHome = Color(0xFF142F40)
private val GrisHome = Color(0xFF687C88)
private val RojoHome = Color(0xFFEF4760)
private val AzulHome = Color(0xFF3182E8)

// ==========================================================
// PANTALLA DE INICIO
// ==========================================================

@Composable
fun HomeScreen(
    onNavegar: (String) -> Unit = {},
    onAbrirReporte: (String) -> Unit = {}
) {
    val reportes by UniFindRepository.reportes.collectAsState()
    val listaState = rememberLazyListState()

    val usuario = FirebaseAuth.getInstance().currentUser

    val nombre = usuario?.displayName
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.split(Regex("\\s+"))
        ?.firstOrNull()
        ?.replaceFirstChar { it.uppercase() }
        ?: usuario?.email
            ?.substringBefore("@")
            ?.replaceFirstChar { it.uppercase() }
        ?: "Usuario"

    // Estadísticas conectadas al repositorio.
    val perdidos = reportes.count {
        it.estado == EstadoReporte.PERDIDO
    }

    val encontrados = reportes.count {
        it.estado == EstadoReporte.ENCONTRADO
    }

    val recuperados = reportes.count {
        it.estado == EstadoReporte.RECUPERADO
    }

    // Categorías más reportadas como perdidas.
    val categorias = remember(reportes) {
        reportes
            .filter {
                it.estado == EstadoReporte.PERDIDO
            }
            .groupingBy {
                it.categoria.trim().ifBlank { "Otros" }
            }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(6)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoHome)
    ) {
        // Fondo original de UniFind.
        Image(
            painter = painterResource(
                id = R.drawable.banner_dentro
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa suave para mejorar la legibilidad.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0x18004D3A),
                            0.20f to Color(0x12FFFFFF),
                            0.48f to Color(0x40F4FAF7),
                            1.0f to Color(0xA6F4FAF7)
                        )
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
                state = listaState,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(
                        bottom = padding.calculateBottomPadding()
                    ),
                contentPadding = PaddingValues(
                    // Se baja un poco el contenido para
                    // dejar respirar el banner del fondo.
                    top = 22.dp,
                    bottom = 30.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ENCABEZADO
                item(key = "encabezado") {
                    EntradaAlVerse(0) {
                        EncabezadoHome(
                            nombre = nombre,
                            onPerfil = {
                                onNavegar("Perfil")
                            },
                            onCampana = {
                                onNavegar("Mis reportes")
                            }
                        )
                    }
                }

                // BUSCADOR
                item(key = "buscador") {
                    EntradaAlVerse(1) {
                        Column {
                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )
                            BuscadorHome(
                                onClick = {
                                    onNavegar("Buscar")
                                }
                            )
                        }
                    }
                }

                // CONTADORES
                item(key = "estadisticas") {
                    EntradaAlVerse(2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp),
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {
                            ContadorHome(
                                modifier = Modifier.weight(1f),
                                valor = perdidos,
                                titulo = "Perdidos",
                                icono = Icons.Default.Search,
                                color = RojoHome,
                                fondo = Color(0xFFFFE9ED)
                            )

                            ContadorHome(
                                modifier = Modifier.weight(1f),
                                valor = encontrados,
                                titulo = "Encontrados",
                                icono = Icons.Default.Inventory2,
                                color = VerdePrincipal,
                                fondo = VerdeClaroHome
                            )

                            ContadorHome(
                                modifier = Modifier.weight(1f),
                                valor = recuperados,
                                titulo = "Recuperados",
                                icono = Icons.Default.LockOpen,
                                color = AzulHome,
                                fondo = Color(0xFFE5F0FF)
                            )
                        }
                    }
                }

                // BANNER PARA PUBLICAR
                item(key = "banner_publicar") {
                    EntradaAlVerse(3) {
                        BannerPublicarHome(
                            onClick = {
                                onNavegar("Publicar")
                            }
                        )
                    }
                }

                // CATEGORÍAS
                item(key = "categorias") {
                    EntradaAlVerse(4) {
                        SeccionCategorias(
                            categorias = categorias.map {
                                it.key
                            },
                            onVerTodas = {
                                onNavegar("Buscar")
                            },
                            onCategoria = {
                                onNavegar("Buscar")
                            }
                        )
                    }
                }

                // ÚNICA GRÁFICA CONSERVADA:
                // ACTIVIDAD DE LOS ÚLTIMOS SIETE DÍAS
                item(key = "actividad_semanal") {
                    EntradaAlVerse(5) {
                        GraficaActividadHome(
                            reportes = reportes
                        )
                    }
                }
            }
        }
    }
}

// ==========================================================
// ANIMACIÓN DE ENTRADA Y SALIDA
// ==========================================================

@Composable
private fun EntradaAlVerse(
    retraso: Int,
    contenido: @Composable () -> Unit
) {
    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        if (retraso > 0) {
            delay(retraso * 45L)
        }
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 380,
                easing = FastOutSlowInEasing
            )
        ) + slideInVertically(
            initialOffsetY = { altura ->
                altura / 8
            },
            animationSpec = tween(
                durationMillis = 430,
                easing = FastOutSlowInEasing
            )
        ),
        exit = fadeOut(
            animationSpec = tween(180)
        ) + slideOutVertically(
            targetOffsetY = { altura ->
                altura / 12
            },
            animationSpec = tween(180)
        )
    ) {
        contenido()
    }
}

// ==========================================================
// ENCABEZADO
// ==========================================================

@Composable
private fun EncabezadoHome(
    nombre: String,
    onPerfil: () -> Unit,
    onCampana: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 22.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Hola, $nombre",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "¿Qué buscamos hoy?",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.96f)
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(5.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onCampana),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = VerdeOscuroHome,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(11.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .padding(3.dp)
                .clip(CircleShape)
                .background(VerdePrincipal)
                .clickable(onClick = onPerfil),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nombre.firstOrNull()?.uppercase() ?: "U",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==========================================================
// BUSCADOR
// ==========================================================

@Composable
private fun BuscadorHome(
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(25.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(58.dp)
            .shadow(
                elevation = 7.dp,
                shape = forma,
                ambientColor = Color(0x22004D3A),
                spotColor = Color(0x22004D3A)
            )
            .clip(forma)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Buscar",
            tint = VerdePrincipal,
            modifier = Modifier.size(25.dp)
        )

        Spacer(Modifier.width(13.dp))

        Text(
            text = "Buscar mochila, llaves, celular...",
            modifier = Modifier.weight(1f),
            color = Color(0xFF788792),
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(39.dp)
                .clip(CircleShape)
                .background(VerdeClaroHome),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Filtros de búsqueda",
                tint = VerdePrincipal,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ==========================================================
// CONTADORES
// ==========================================================

@Composable
private fun ContadorHome(
    modifier: Modifier,
    valor: Int,
    titulo: String,
    icono: ImageVector,
    color: Color,
    fondo: Color
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(23.dp),
                ambientColor = Color(0x12000000)
            )
            .clip(RoundedCornerShape(23.dp))
            .background(Color.White.copy(alpha = 0.97f))
            .padding(
                horizontal = 4.dp,
                vertical = 15.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(fondo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        NumeroAnimado(
            valor = valor,
            color = TextoHome
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = titulo,
            fontSize = 11.sp,
            color = GrisHome,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false
        )
    }
}

// ==========================================================
// BANNER PARA PUBLICAR
// ==========================================================

@Composable
private fun BannerPublicarHome(
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(25.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(174.dp)
            .shadow(5.dp, forma)
            .clip(forma)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.banner_card_inicio
            ),
            contentDescription =
                "Juntos hacemos la diferencia",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0x30E8F8EF),
                            Color(0x12E8F8EF),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 18.dp,
                    top = 14.dp,
                    bottom = 14.dp,
                    end = 145.dp
                ),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Juntos hacemos la diferencia",
                fontSize = 19.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VerdeOscuroHome
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text =
                    "Publica, encuentra y ayuda a que más personas recuperen sus objetos.",
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = Color(0xFF426D60)
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(VerdePrincipal)
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Publicar ahora",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.width(5.dp))

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ==========================================================
// CATEGORÍAS
// ==========================================================

@Composable
private fun SeccionCategorias(
    categorias: List<String>,
    onVerTodas: () -> Unit,
    onCategoria: (String) -> Unit
) {
    val predeterminadas = listOf(
        "Mochilas",
        "Celulares",
        "Llaves",
        "Ropa",
        "Laptops",
        "Accesorios"
    )

    val visibles = remember(categorias) {
        (
                categorias.filter { it.isNotBlank() } +
                        predeterminadas
                )
            .distinctBy { it.lowercase() }
            .take(8)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categorías",
                modifier = Modifier.weight(1f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextoHome
            )

            Text(
                text = "Ver todas",
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onVerTodas)
                    .padding(5.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = VerdePrincipal
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 17.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = visibles,
                key = { it.lowercase() }
            ) { categoria ->
                val estilo = obtenerEstiloCategoria(categoria)

                CategoriaIcono(
                    titulo = categoria,
                    icono = estilo.first,
                    fondo = estilo.second,
                    color = estilo.third,
                    onClick = {
                        onCategoria(categoria)
                    }
                )
            }
        }
    }
}

// ==========================================================
// ESTILO DE CATEGORÍA
// ==========================================================

private fun obtenerEstiloCategoria(
    categoria: String
): Triple<ImageVector, Color, Color> {
    return when {
        categoria.contains("mochila", true) ||
                categoria.contains("bolsa", true) -> Triple(
            Icons.Default.Inventory2,
            Color(0xFFDDF5EA),
            Color(0xFF08785A)
        )

        categoria.contains("celular", true) ||
                categoria.contains("teléfono", true) -> Triple(
            Icons.Default.Smartphone,
            Color(0xFFE5F0FF),
            Color(0xFF2778DB)
        )

        categoria.contains("llave", true) -> Triple(
            Icons.Default.Key,
            Color(0xFFFFF4D8),
            Color(0xFFE6A000)
        )

        categoria.contains("ropa", true) ||
                categoria.contains("playera", true) -> Triple(
            Icons.Default.Checkroom,
            Color(0xFFF0E8FF),
            Color(0xFF8656D8)
        )

        categoria.contains("laptop", true) ||
                categoria.contains("computadora", true) -> Triple(
            Icons.Default.Laptop,
            Color(0xFFE5F0FF),
            Color(0xFF347BE0)
        )

        categoria.contains("accesorio", true) ||
                categoria.contains("audífono", true) -> Triple(
            Icons.Default.Headphones,
            Color(0xFFFFE8ED),
            Color(0xFFE84863)
        )

        else -> Triple(
            Icons.Default.Inventory2,
            VerdeClaroHome,
            VerdePrincipal
        )
    }
}

// ==========================================================
// TARJETA DE CATEGORÍA
// ==========================================================

@Composable
private fun CategoriaIcono(
    titulo: String,
    icono: ImageVector,
    fondo: Color,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(82.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(67.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(fondo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = color,
                modifier = Modifier.size(31.dp)
            )
        }

        Spacer(Modifier.height(7.dp))

        Text(
            text = titulo,
            fontSize = 11.sp,
            color = GrisHome,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ==========================================================
// GRÁFICA DE BARRAS: ACTIVIDAD RECIENTE
// ==========================================================

private data class DiaActividadHome(
    val etiqueta: String,
    val total: Int
)

@Composable
private fun GraficaActividadHome(
    reportes: List<Reporte>
) {
    val dias = remember(reportes) {
        val calendario = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val etiquetas = listOf(
            "Dom", "Lun", "Mar", "Mié",
            "Jue", "Vie", "Sáb"
        )

        (6 downTo 0).map { desplazamiento ->
            val inicioDia =
                (calendario.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, -desplazamiento)
                }

            val inicio = inicioDia.timeInMillis

            val fin = (inicioDia.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, 1)
            }.timeInMillis

            val totalDia = reportes.count { reporte ->
                reporte.fechaMillis >= inicio &&
                        reporte.fechaMillis < fin
            }

            DiaActividadHome(
                etiqueta = etiquetas[
                    inicioDia.get(Calendar.DAY_OF_WEEK) - 1
                ],
                total = totalDia
            )
        }
    }

    val maximo = maxOf(
        1,
        dias.maxOfOrNull { it.total } ?: 1
    )

    val totalSemana = dias.sumOf { it.total }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Actividad reciente",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoHome
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = "Reportes de los últimos 7 días",
                    fontSize = 12.sp,
                    color = GrisHome
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    text = "$totalSemana reportes",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdePrincipal
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.97f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 18.dp,
                    bottom = 16.dp
                )
            ) {
                // Escala y columnas de la gráfica.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(166.dp),
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier
                            .width(22.dp)
                            .height(137.dp),
                        verticalArrangement =
                            Arrangement.SpaceBetween,
                        horizontalAlignment =
                            Alignment.End
                    ) {
                        Text(
                            text = maximo.toString(),
                            fontSize = 9.sp,
                            color = GrisHome
                        )

                        Text(
                            text = (maximo / 2).toString(),
                            fontSize = 9.sp,
                            color = GrisHome
                        )

                        Text(
                            text = "0",
                            fontSize = 9.sp,
                            color = GrisHome
                        )
                    }

                    dias.forEach { dia ->
                        BarraDiaHome(
                            dia = dia,
                            maximo = maximo,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Leyenda de la gráfica.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(VerdePrincipal)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "Total de reportes por día",
                        fontSize = 11.sp,
                        color = GrisHome
                    )
                }
            }
        }
    }
}

// ==========================================================
// BARRA ANIMADA POR DÍA
// ==========================================================

@Composable
private fun BarraDiaHome(
    dia: DiaActividadHome,
    maximo: Int,
    modifier: Modifier = Modifier
) {
    val fraccion = dia.total.toFloat() /
            maximo.coerceAtLeast(1).toFloat()

    val altura by animateFloatAsState(
        targetValue = if (dia.total == 0) {
            0f
        } else {
            fraccion.coerceIn(0.05f, 1f)
        },
        animationSpec = tween(
            durationMillis = 650,
            easing = FastOutSlowInEasing
        ),
        label = "alturaBarra"
    )

    Column(
        modifier = modifier.height(166.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = dia.total.toString(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextoHome
        )

        Spacer(Modifier.height(5.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(111.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (dia.total > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.67f)
                        .height(
                            (altura * 111f)
                                .dp
                                .coerceAtLeast(5.dp)
                        )
                        .clip(
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp
                            )
                        )
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF008A67),
                                    Color(0xFF7FD4B0)
                                )
                            )
                        )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = dia.etiqueta,
            fontSize = 10.sp,
            color = GrisHome
        )
    }
}
