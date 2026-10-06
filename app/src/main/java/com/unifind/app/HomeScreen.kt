package com.unifind.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.net.URL


// ==========================================================
// COLORES UNIFIND
// ==========================================================

private val Verde = Color(0xFF008F63)
private val VerdeOscuro = Color(0xFF006B4A)
private val VerdeClaro = Color(0xFFE5F7EF)

private val Rojo = Color(0xFFE53935)
private val RojoClaro = Color(0xFFFFE9E9)

private val Azul = Color(0xFF3478F6)
private val AzulClaro = Color(0xFFEAF1FF)

private val Morado = Color(0xFF8E5CF6)
private val MoradoClaro = Color(0xFFF0EAFE)

private val Naranja = Color(0xFFF39C3D)
private val NaranjaClaro = Color(0xFFFFF1DF)

private val Amarillo = Color(0xFFF4C542)
private val AmarilloClaro = Color(0xFFFFF8DC)

private val FondoEncontrado = Color(0xFFE2F6EB)

private val TextoPrincipal = Color(0xFF202124)
private val TextoSecundario = Color(0xFF5F6368)
private val TextoClaro = Color(0xFF8A9094)


// ==========================================================
// PUBLICACIONES QUE SE MUESTRAN EN HOME
// ==========================================================

private const val PUBLICACIONES_HOME = 2


// ==========================================================
// MODELO DE PUBLICACIÓN
// ==========================================================

data class Publicacion(

    val titulo: String,

    val ubicacion: String,

    val tiempo: String,

    val encontrado: Boolean,

    val categoria: String,

    val publicadoPor: String,

    // ======================================================
    // IMAGEN DEL USUARIO
    //
    // IMPORTANTE:
    // NO ponemos iconos de mochila, celular, etc.
    //
    // Cuando Firebase tenga una URL de imagen real,
    // aquí podremos utilizarla.
    // ======================================================

    val imagen: Int? = null
)


// ==========================================================
// HOME SCREEN
// ==========================================================

@Composable
fun HomeScreen(
    onNavegar: (String) -> Unit = {}
) {

    // ======================================================
    // USUARIO ACTUAL
    // ======================================================

    val usuarioActual = FirebaseAuth
        .getInstance()
        .currentUser


    // ======================================================
    // NOMBRE
    // ======================================================

    val nombreCompleto = usuarioActual
        ?.displayName
        ?.trim()
        .orEmpty()


    val nombreUsuario = if (nombreCompleto.isNotEmpty()) {

        nombreCompleto
            .split(" ")
            .firstOrNull()
            ?.replaceFirstChar { it.uppercase() }
            ?: "Usuario"

    } else {

        usuarioActual
            ?.email
            ?.substringBefore("@")
            ?.replaceFirstChar { it.uppercase() }
            ?: "Usuario"
    }


    // ======================================================
    // LETRA DEL USUARIO
    // ======================================================

    val letraUsuario = nombreUsuario
        .trim()
        .firstOrNull()
        ?.uppercase()
        ?: "U"


    // ======================================================
    // FOTO DE PERFIL
    // ======================================================

    val fotoPerfilUrl = usuarioActual
        ?.photoUrl
        ?.toString()


    // ======================================================
    // PUBLICACIONES DE EJEMPLO
    //
    // DESPUÉS ESTO SE CAMBIARÁ POR FIREBASE.
    // ======================================================

    val publicaciones = listOf(

        Publicacion(
            titulo = "Llaves con llavero azul",
            ubicacion = "Biblioteca",
            tiempo = "Hace 2 horas",
            encontrado = true,
            categoria = "Llaves",
            publicadoPor = "Carlos"
        ),

        Publicacion(
            titulo = "Mochila negra",
            ubicacion = "Edificio A",
            tiempo = "Hace 5 horas",
            encontrado = false,
            categoria = "Mochilas",
            publicadoPor = "María"
        ),

        Publicacion(
            titulo = "Celular Samsung",
            ubicacion = "Cafetería",
            tiempo = "Hace 1 día",
            encontrado = true,
            categoria = "Celulares",
            publicadoPor = "Luis"
        ),

        Publicacion(
            titulo = "Calculadora científica",
            ubicacion = "Salón 204",
            tiempo = "Hace 1 día",
            encontrado = false,
            categoria = "Calculadoras",
            publicadoPor = "Ana"
        ),

        Publicacion(
            titulo = "Audífonos inalámbricos",
            ubicacion = "Laboratorio",
            tiempo = "Hace 2 días",
            encontrado = false,
            categoria = "Audífonos",
            publicadoPor = "Diego"
        ),

        Publicacion(
            titulo = "Mochila gris",
            ubicacion = "Edificio B",
            tiempo = "Hace 3 días",
            encontrado = false,
            categoria = "Mochilas",
            publicadoPor = "Sofía"
        ),

        Publicacion(
            titulo = "Credencial de estudiante",
            ubicacion = "Pasillo principal",
            tiempo = "Hace 4 días",
            encontrado = true,
            categoria = "Credenciales",
            publicadoPor = "Jorge"
        )
    )


    // ======================================================
    // BUSCADOR
    // ======================================================

    var textoBusqueda by remember {
        mutableStateOf("")
    }


    // ======================================================
    // FILTRO DE PUBLICACIONES
    //
    // SOLAMENTE:
    // Perdidos
    // Encontrados
    // ======================================================

    var filtro by remember {
        mutableStateOf("Perdidos")
    }


    // ======================================================
    // FILTRAR PUBLICACIONES
    // ======================================================

    val publicacionesFiltradas = publicaciones.filter { publicacion ->

        val coincideBusqueda =
            textoBusqueda.isBlank() ||
                    publicacion.titulo.contains(
                        textoBusqueda,
                        ignoreCase = true
                    ) ||
                    publicacion.categoria.contains(
                        textoBusqueda,
                        ignoreCase = true
                    ) ||
                    publicacion.ubicacion.contains(
                        textoBusqueda,
                        ignoreCase = true
                    ) ||
                    publicacion.publicadoPor.contains(
                        textoBusqueda,
                        ignoreCase = true
                    )


        val coincideFiltro = when (filtro) {

            "Perdidos" ->
                !publicacion.encontrado

            "Encontrados" ->
                publicacion.encontrado

            else ->
                true
        }


        coincideBusqueda && coincideFiltro
    }


    // ======================================================
    // SOLO 2 PUBLICACIONES EN HOME
    // ======================================================

    val publicacionesVisibles =
        publicacionesFiltradas.take(PUBLICACIONES_HOME)


    // ======================================================
    // ESTADÍSTICAS
    // ======================================================

    val totalPerdidos =
        publicaciones.count { !it.encontrado }

    val totalEncontrados =
        publicaciones.count { it.encontrado }

    val totalPublicaciones =
        publicaciones.size


    // ======================================================
    // CATEGORÍAS MÁS PERDIDAS
    // ======================================================

    val categoriasPerdidas = publicaciones
        .filter { !it.encontrado }
        .groupingBy { it.categoria }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(4)


    // ======================================================
    // FONDO COMPLETO
    // ======================================================

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // ==================================================
        // BANNER DE FONDO
        // ==================================================

        Image(
            painter = painterResource(
                id = R.drawable.banner_dentro
            ),
            contentDescription = "Fondo UniFind",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        // ==================================================
        // CAPA TRANSPARENTE
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(alpha = 0.12f)
                )
        )


        // ==================================================
        // SCAFFOLD
        // ==================================================

        Scaffold(

            modifier = Modifier.fillMaxSize(),

            containerColor = Color.Transparent,

            bottomBar = {

                UniFindBottomBar(
                    seleccionado = "Inicio",
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

            ) {


                // ==================================================
                // HEADER
                // ==================================================

                HomeHeader(

                    nombreUsuario = nombreUsuario,

                    letraUsuario = letraUsuario,

                    fotoPerfilUrl = fotoPerfilUrl,

                    onPerfil = {
                        onNavegar("Perfil")
                    },

                    onNotificaciones = {
                        onNavegar("Notificaciones")
                    }
                )


                // ==================================================
                // CONTENIDO
                // ==================================================

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 18.dp,
                            end = 18.dp,
                            top = 8.dp,
                            bottom = 30.dp
                        )

                ) {


                    // ==================================================
                    // BUSCADOR
                    // ==================================================

                    SearchBarHome(

                        value = textoBusqueda,

                        onValueChange = {
                            textoBusqueda = it
                        }
                    )


                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )


                    // ==================================================
                    // RESUMEN
                    // ==================================================

                    Text(

                        text = "Resumen de esta semana",

                        fontSize = 21.sp,

                        fontWeight = FontWeight.ExtraBold,

                        color = TextoPrincipal
                    )


                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )


                    Text(

                        text = "Así se mueve UniFind estos días",

                        fontSize = 13.sp,

                        color = TextoSecundario
                    )


                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )


                    // ==================================================
                    // TARJETAS DE ESTADÍSTICAS
                    // ==================================================

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)

                    ) {

                        SummaryCard(

                            modifier =
                                Modifier.weight(1f),

                            numero =
                                totalPerdidos.toString(),

                            titulo =
                                "Perdidos",

                            icono =
                                R.drawable.chart_lost,

                            color =
                                Rojo,

                            fondo =
                                RojoClaro
                        )


                        SummaryCard(

                            modifier =
                                Modifier.weight(1f),

                            numero =
                                totalEncontrados.toString(),

                            titulo =
                                "Encontrados",

                            icono =
                                R.drawable.chart_found,

                            color =
                                Verde,

                            fondo =
                                VerdeClaro
                        )


                        SummaryCard(

                            modifier =
                                Modifier.weight(1f),

                            numero =
                                totalPublicaciones.toString(),

                            titulo =
                                "Publicaciones",

                            icono =
                                R.drawable.chart_total,

                            color =
                                Azul,

                            fondo =
                                AzulClaro
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )


                    // ==================================================
                    // OBJETOS MÁS PERDIDOS
                    // ==================================================

                    MostLostCard(

                        categorias =
                            categoriasPerdidas
                    )


                    Spacer(
                        modifier = Modifier.height(28.dp)
                    )


                    // ==================================================
                    // PUBLICACIONES RECIENTES
                    // ==================================================

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceBetween,

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Column {

                            Text(

                                text =
                                    "Publicaciones recientes",

                                fontSize = 20.sp,

                                fontWeight =
                                    FontWeight.ExtraBold,

                                color =
                                    TextoPrincipal
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(3.dp)
                            )


                            Text(

                                text =
                                    "Explora los objetos reportados",

                                fontSize = 12.sp,

                                color =
                                    TextoSecundario
                            )
                        }


                        // ==================================================
                        // VER MÁS
                        // ==================================================

                        Box(

                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(50.dp)
                                )
                                .background(
                                    VerdeClaro
                                )
                                .clickable {

                                    onNavegar("Buscar")
                                }
                                .padding(
                                    horizontal = 13.dp,
                                    vertical = 8.dp
                                )

                        ) {

                            Text(

                                text = "Ver más",

                                fontSize = 12.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    VerdeOscuro
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )


                    // ==================================================
                    // FILTROS
                    //
                    // SOLO PERDIDOS Y ENCONTRADOS
                    // ==================================================

                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(9.dp)

                    ) {

                        FilterChipUniFind(

                            text =
                                "Perdidos",

                            selected =
                                filtro == "Perdidos",

                            onClick = {

                                filtro =
                                    "Perdidos"
                            }
                        )


                        FilterChipUniFind(

                            text =
                                "Encontrados",

                            selected =
                                filtro == "Encontrados",

                            onClick = {

                                filtro =
                                    "Encontrados"
                            }
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // ==================================================
                    // PUBLICACIONES
                    //
                    // SOLO 2
                    // ==================================================

                    if (
                        publicacionesVisibles.isEmpty()
                    ) {

                        EmptyPublications()

                    } else {

                        publicacionesVisibles.forEach {

                                publicacion ->

                            PublicationCard(
                                publicacion =
                                    publicacion
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )
                        }


                        // ==================================================
                        // VER MÁS
                        // ==================================================

                        Box(

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                                .clip(
                                    RoundedCornerShape(14.dp)
                                )
                                .background(
                                    Verde
                                )
                                .clickable {

                                    onNavegar("Buscar")
                                },

                            contentAlignment =
                                Alignment.Center

                        ) {

                            Text(

                                text =
                                    "Ver más publicaciones",

                                fontSize = 14.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================================
// TARJETA DEL RESUMEN
// ==========================================================

@Composable
fun SummaryCard(

    modifier: Modifier,

    numero: String,

    titulo: String,

    icono: Int,

    color: Color,

    fondo: Color

) {

    val forma =
        RoundedCornerShape(19.dp)


    Column(

        modifier = modifier
            .height(124.dp)
            .shadow(
                elevation = 4.dp,
                shape = forma
            )
            .clip(forma)
            .background(
                Color.White.copy(alpha = 0.97f)
            )
            .border(
                1.dp,
                Color.White,
                forma
            )
            .padding(11.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.SpaceBetween

    ) {


        // ==================================================
        // ICONO
        // ==================================================

        Box(

            modifier = Modifier
                .size(39.dp)
                .clip(CircleShape)
                .background(fondo),

            contentAlignment =
                Alignment.Center

        ) {

            Image(

                painter =
                    painterResource(id = icono),

                contentDescription =
                    titulo,

                modifier =
                    Modifier.size(20.dp),

                colorFilter =
                    ColorFilter.tint(color)
            )
        }


        // ==================================================
        // NÚMERO
        // ==================================================

        Text(

            text = numero,

            fontSize = 25.sp,

            fontWeight =
                FontWeight.ExtraBold,

            color = color
        )


        // ==================================================
        // TÍTULO
        // ==================================================

        Text(

            text = titulo,

            fontSize = 11.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextoSecundario,

            textAlign =
                TextAlign.Center,

            maxLines = 1
        )
    }
}


// ==========================================================
// HEADER
// ==========================================================

@Composable
fun HomeHeader(

    nombreUsuario: String,

    letraUsuario: String,

    fotoPerfilUrl: String?,

    onPerfil: () -> Unit,

    onNotificaciones: () -> Unit

) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp)

    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 18.dp
                ),

            verticalArrangement =
                Arrangement.SpaceBetween

        ) {


            // ==================================================
            // LOGO + NOTIFICACIONES
            // ==================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Column {

                    Text(

                        text = "UniFind",

                        fontSize = 27.sp,

                        fontWeight =
                            FontWeight.ExtraBold,

                        color = Color.White
                    )


                    Text(

                        text =
                            "Objetos que te conectan",

                        fontSize = 10.sp,

                        color =
                            Color.White.copy(
                                alpha = 0.92f
                            )
                    )
                }


                // ==================================================
                // NOTIFICACIONES
                // ==================================================

                Box(

                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(
                                alpha = 0.92f
                            )
                        )
                        .clickable {
                            onNotificaciones()
                        },

                    contentAlignment =
                        Alignment.Center

                ) {

                    Image(

                        painter =
                            painterResource(
                                id =
                                    R.drawable.bell_solid
                            ),

                        contentDescription =
                            "Notificaciones",

                        modifier =
                            Modifier.size(21.dp),

                        colorFilter =
                            ColorFilter.tint(
                                VerdeOscuro
                            )
                    )


                    // ==================================================
                    // PUNTO ROJO
                    // ==================================================

                    Box(

                        modifier = Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .padding(
                                top = 7.dp,
                                end = 7.dp
                            )
                            .size(9.dp)
                            .background(
                                Rojo,
                                CircleShape
                            )
                            .border(
                                1.dp,
                                Color.White,
                                CircleShape
                            )
                    )
                }
            }


            // ==================================================
            // PERFIL + SALUDO
            // ==================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                ProfileAvatar(

                    letra =
                        letraUsuario,

                    fotoUrl =
                        fotoPerfilUrl,

                    onClick =
                        onPerfil
                )


                Spacer(
                    modifier =
                        Modifier.width(13.dp)
                )


                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(

                        text =
                            "¡Hola, $nombreUsuario!",

                        fontSize = 23.sp,

                        lineHeight = 27.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color = Color.White,

                        maxLines = 1,

                        overflow =
                            TextOverflow.Ellipsis
                    )


                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )


                    Text(

                        text =
                            "Encuentra o reporta un objeto",

                        fontSize = 13.sp,

                        color =
                            Color.White.copy(
                                alpha = 0.95f
                            )
                    )
                }
            }
        }
    }
}


// ==========================================================
// BUSCADOR
// ==========================================================

@Composable
fun SearchBarHome(

    value: String,

    onValueChange: (String) -> Unit

) {

    val forma =
        RoundedCornerShape(17.dp)


    Row(

        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = forma
            )
            .clip(forma)
            .background(
                Color.White.copy(
                    alpha = 0.96f
                )
            )
            .border(
                1.dp,
                Color.White,
                forma
            )
            .padding(
                horizontal = 16.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {


        Image(

            painter =
                painterResource(
                    id =
                        R.drawable.magnifying_glass_solid
                ),

            contentDescription =
                "Buscar",

            modifier =
                Modifier.size(20.dp),

            colorFilter =
                ColorFilter.tint(Verde)
        )


        Spacer(
            modifier =
                Modifier.width(12.dp)
        )


        BasicTextField(

            value = value,

            onValueChange =
                onValueChange,

            singleLine = true,

            modifier =
                Modifier.weight(1f),

            textStyle =
                TextStyle(
                    fontSize = 14.sp,
                    color = TextoPrincipal
                ),

            decorationBox = {

                    innerTextField ->

                Box(
                    contentAlignment =
                        Alignment.CenterStart
                ) {

                    if (value.isEmpty()) {

                        Text(

                            text =
                                "Busca llaves, mochila, celular...",

                            fontSize = 14.sp,

                            color =
                                TextoClaro
                        )
                    }

                    innerTextField()
                }
            }
        )


        if (value.isNotEmpty()) {

            Text(

                text = "×",

                fontSize = 24.sp,

                color =
                    TextoSecundario,

                modifier =
                    Modifier.clickable {

                        onValueChange("")
                    }
            )
        }
    }
}


// ==========================================================
// OBJETOS MÁS PERDIDOS
// ==========================================================

@Composable
fun MostLostCard(

    categorias:
    List<Map.Entry<String, Int>>

) {

    val forma =
        RoundedCornerShape(22.dp)


    val maximo =
        categorias.maxOfOrNull {
            it.value
        } ?: 1


    // ======================================================
    // COLORES DE LA GRÁFICA
    // ======================================================

    val colores = listOf(

        Naranja,

        Morado,

        Azul,

        Verde
    )


    val fondos = listOf(

        NaranjaClaro,

        MoradoClaro,

        AzulClaro,

        VerdeClaro
    )


    Column(

        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = forma
            )
            .clip(forma)
            .background(
                Color.White.copy(
                    alpha = 0.97f
                )
            )
            .border(
                1.dp,
                Color.White,
                forma
            )
            .padding(17.dp)

    ) {


        // ==================================================
        // ENCABEZADO
        // ==================================================

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier
                    .size(43.dp)
                    .clip(CircleShape)
                    .background(
                        MoradoClaro
                    ),

                contentAlignment =
                    Alignment.Center

            ) {

                Text(

                    text = "↗",

                    fontSize = 24.sp,

                    fontWeight =
                        FontWeight.ExtraBold,

                    color =
                        Morado
                )
            }


            Spacer(
                modifier =
                    Modifier.width(11.dp)
            )


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    text =
                        "Objetos más perdidos",

                    fontSize = 17.sp,

                    fontWeight =
                        FontWeight.ExtraBold,

                    color =
                        TextoPrincipal
                )


                Text(

                    text =
                        "Categorías con más reportes",

                    fontSize = 12.sp,

                    color =
                        TextoSecundario
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        if (categorias.isEmpty()) {

            Text(

                text =
                    "Todavía no hay objetos perdidos.",

                fontSize = 13.sp,

                color =
                    TextoSecundario
            )

        } else {

            categorias.forEachIndexed {

                    index,
                    categoria ->

                val color =
                    colores[
                        index % colores.size
                    ]

                val fondo =
                    fondos[
                        index % fondos.size
                    ]


                // ==========================================
                // NOMBRE + CANTIDAD
                // ==========================================

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Box(

                        modifier = Modifier
                            .size(34.dp)
                            .clip(
                                CircleShape
                            )
                            .background(
                                fondo
                            ),

                        contentAlignment =
                            Alignment.Center

                    ) {

                        Text(

                            text =
                                "${index + 1}",

                            fontSize = 12.sp,

                            fontWeight =
                                FontWeight.ExtraBold,

                            color =
                                color
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )


                    Column(

                        modifier =
                            Modifier.weight(1f)

                    ) {

                        Text(

                            text =
                                categoria.key,

                            fontSize = 14.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextoPrincipal,

                            maxLines = 1,

                            overflow =
                                TextOverflow.Ellipsis
                        )


                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )


                        // ==================================
                        // BARRA
                        // ==================================

                        Box(

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(
                                    RoundedCornerShape(
                                        50.dp
                                    )
                                )
                                .background(
                                    fondo
                                )

                        ) {

                            Box(

                                modifier = Modifier
                                    .fillMaxWidth(
                                        categoria.value
                                            .toFloat() /
                                                maximo
                                    )
                                    .height(8.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            50.dp
                                        )
                                    )
                                    .background(
                                        color
                                    )
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )


                    Text(

                        text =
                            categoria.value.toString(),

                        fontSize = 17.sp,

                        fontWeight =
                            FontWeight.ExtraBold,

                        color =
                            color
                    )
                }


                if (
                    index <
                    categorias.lastIndex
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(15.dp)
                    )
                }
            }
        }
    }
}


// ==========================================================
// AVATAR
// ==========================================================

@Composable
fun ProfileAvatar(

    letra: String,

    fotoUrl: String?,

    onClick: () -> Unit

) {

    var bitmap by remember(
        fotoUrl
    ) {

        mutableStateOf<Bitmap?>(
            null
        )
    }


    LaunchedEffect(fotoUrl) {

        if (
            !fotoUrl.isNullOrEmpty()
        ) {

            bitmap =
                withContext(
                    Dispatchers.IO
                ) {

                    try {

                        val connection =
                            URL(fotoUrl)
                                .openConnection()

                        connection.connect()


                        connection
                            .getInputStream()
                            .use {

                                    inputStream ->

                                BitmapFactory
                                    .decodeStream(
                                        inputStream
                                    )
                            }

                    } catch (
                        e: Exception
                    ) {

                        null
                    }
                }
        }
    }


    Box(

        modifier = Modifier
            .size(57.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(
                2.dp,
                Color.White,
                CircleShape
            )
            .clickable {
                onClick()
            },

        contentAlignment =
            Alignment.Center

    ) {

        val fotoActual =
            bitmap


        if (
            fotoActual != null
        ) {

            Image(

                bitmap =
                    fotoActual.asImageBitmap(),

                contentDescription =
                    "Foto de perfil",

                modifier =
                    Modifier.fillMaxSize(),

                contentScale =
                    ContentScale.Crop
            )

        } else {

            Box(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            VerdeOscuro
                        ),

                contentAlignment =
                    Alignment.Center

            ) {

                Text(

                    text = letra,

                    fontSize = 24.sp,

                    fontWeight =
                        FontWeight.ExtraBold,

                    color =
                        Color.White
                )
            }
        }
    }
}


// ==========================================================
// CHIP DE FILTRO
// ==========================================================

@Composable
fun FilterChipUniFind(

    text: String,

    selected: Boolean,

    onClick: () -> Unit

) {

    val forma =
        RoundedCornerShape(50.dp)


    Box(

        modifier = Modifier
            .clip(forma)
            .background(

                if (selected)
                    Verde
                else
                    Color.White.copy(
                        alpha = 0.90f
                    )
            )
            .border(

                1.dp,

                if (selected)
                    Verde
                else
                    Color(0xFFE2E7E5),

                forma
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 18.dp,
                vertical = 9.dp
            ),

        contentAlignment =
            Alignment.Center

    ) {

        Text(

            text = text,

            fontSize = 13.sp,

            fontWeight =
                FontWeight.Bold,

            color =

                if (selected)
                    Color.White
                else
                    TextoSecundario
        )
    }
}


// ==========================================================
// TARJETA DE PUBLICACIÓN
//
// IMPORTANTE:
// NO HAY ICONOS DE MOCHILA, CELULAR, ETC.
//
// AQUÍ QUEDA UN ESPACIO VACÍO PARA LA IMAGEN REAL.
// ==========================================================

@Composable
fun PublicationCard(

    publicacion: Publicacion

) {

    val forma =
        RoundedCornerShape(20.dp)


    Row(

        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = forma
            )
            .clip(forma)
            .background(
                Color.White.copy(
                    alpha = 0.97f
                )
            )
            .border(
                1.dp,
                Color.White,
                forma
            )
            .padding(12.dp),

        verticalAlignment =
            Alignment.CenterVertically

    ) {


        // ==================================================
        // IMAGEN DEL OBJETO
        // ==================================================

        Box(

            modifier = Modifier
                .size(88.dp)
                .clip(
                    RoundedCornerShape(15.dp)
                )
                .background(
                    Color(0xFFF1F3F3)
                )
                .border(
                    1.dp,
                    Color(0xFFE3E7E6),
                    RoundedCornerShape(15.dp)
                ),

            contentAlignment =
                Alignment.Center

        ) {

            val imagenPublicacion =
                publicacion.imagen


            if (
                imagenPublicacion != null
            ) {

                Image(

                    painter =
                        painterResource(
                            id =
                                imagenPublicacion
                        ),

                    contentDescription =
                        publicacion.titulo,

                    modifier =
                        Modifier.fillMaxSize(),

                    contentScale =
                        ContentScale.Crop
                )

            } else {

                // ==========================================
                // SIN ICONO
                //
                // SOLAMENTE ESPACIO PARA LA FOTO
                // ==========================================

                Text(

                    text = "Sin imagen",

                    fontSize = 11.sp,

                    color =
                        TextoClaro
                )
            }
        }


        Spacer(
            modifier =
                Modifier.width(14.dp)
        )


        // ==================================================
        // INFORMACIÓN
        // ==================================================

        Column(

            modifier =
                Modifier.weight(1f)

        ) {


            // ==================================================
            // ESTADO + CATEGORÍA
            // ==================================================

            Row(

                horizontalArrangement =
                    Arrangement.spacedBy(6.dp),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                StatusChip(

                    encontrado =
                        publicacion.encontrado
                )


                CategoryChip(

                    categoria =
                        publicacion.categoria
                )
            }


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            // ==================================================
            // TÍTULO
            // ==================================================

            Text(

                text =
                    publicacion.titulo,

                fontSize = 16.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextoPrincipal,

                maxLines = 1,

                overflow =
                    TextOverflow.Ellipsis
            )


            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )


            // ==================================================
            // USUARIO
            // ==================================================

            Text(

                text =
                    "Publicado por ${publicacion.publicadoPor}",

                fontSize = 12.sp,

                fontWeight =
                    FontWeight.Medium,

                color =
                    TextoSecundario,

                maxLines = 1,

                overflow =
                    TextOverflow.Ellipsis
            )


            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )


            // ==================================================
            // UBICACIÓN + TIEMPO
            // ==================================================

            Row(

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Image(

                    painter =
                        painterResource(
                            id =
                                R.drawable.location_dot_solid
                        ),

                    contentDescription =
                        "Ubicación",

                    modifier =
                        Modifier.size(13.dp),

                    colorFilter =
                        ColorFilter.tint(
                            Verde
                        )
                )


                Spacer(
                    modifier =
                        Modifier.width(5.dp)
                )


                Text(

                    text =
                        publicacion.ubicacion,

                    fontSize = 11.sp,

                    color =
                        TextoSecundario,

                    maxLines = 1,

                    overflow =
                        TextOverflow.Ellipsis
                )


                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )


                Image(

                    painter =
                        painterResource(
                            id =
                                R.drawable.clock_solid
                        ),

                    contentDescription =
                        "Hora",

                    modifier =
                        Modifier.size(13.dp),

                    colorFilter =
                        ColorFilter.tint(
                            Verde
                        )
                )


                Spacer(
                    modifier =
                        Modifier.width(5.dp)
                )


                Text(

                    text =
                        publicacion.tiempo,

                    fontSize = 11.sp,

                    color =
                        TextoSecundario,

                    maxLines = 1,

                    overflow =
                        TextOverflow.Ellipsis
                )
            }
        }
    }
}


// ==========================================================
// SIN PUBLICACIONES
// ==========================================================

@Composable
fun EmptyPublications() {

    val forma =
        RoundedCornerShape(18.dp)


    Box(

        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(forma)
            .background(
                Color.White.copy(
                    alpha = 0.90f
                )
            )
            .border(
                1.dp,
                Color(0xFFE4E8E7),
                forma
            ),

        contentAlignment =
            Alignment.Center

    ) {

        Text(

            text =
                "No encontramos publicaciones.",

            fontSize = 14.sp,

            color =
                TextoSecundario
        )
    }
}


// ==========================================================
// STATUS CHIP
// ==========================================================

@Composable
fun StatusChip(

    encontrado: Boolean

) {

    val fondo =

        if (encontrado)
            FondoEncontrado
        else
            RojoClaro


    val color =

        if (encontrado)
            VerdeOscuro
        else
            Rojo


    val texto =

        if (encontrado)
            "Encontrado"
        else
            "Perdido"


    Row(

        modifier = Modifier
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(fondo)
            .padding(
                horizontal = 9.dp,
                vertical = 4.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        Box(

            modifier = Modifier
                .size(7.dp)
                .background(
                    color,
                    CircleShape
                )
        )


        Spacer(
            modifier =
                Modifier.width(5.dp)
        )


        Text(

            text = texto,

            fontSize = 11.sp,

            fontWeight =
                FontWeight.Bold,

            color = color
        )
    }
}


// ==========================================================
// CHIP DE CATEGORÍA
// ==========================================================

@Composable
fun CategoryChip(

    categoria: String

) {

    Box(

        modifier = Modifier
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(
                Color(0xFFF1F3F3)
            )
            .border(
                1.dp,
                Color(0xFFE3E7E6),
                RoundedCornerShape(20.dp)
            )
            .padding(
                horizontal = 9.dp,
                vertical = 4.dp
            )

    ) {

        Text(

            text = categoria,

            fontSize = 11.sp,

            fontWeight =
                FontWeight.SemiBold,

            color =
                TextoSecundario,

            maxLines = 1,

            overflow =
                TextOverflow.Ellipsis
        )
    }
}