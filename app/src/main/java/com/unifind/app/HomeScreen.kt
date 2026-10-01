package com.unifind.app

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

private val FondoPerdido = Color(0xFFFFE9E9)
private val FondoEncontrado = Color(0xFFE2F6EB)
private val FondoGris = Color(0xFFF3F5F5)

private val TextoPrincipal = Color(0xFF202124)
private val TextoSecundario = Color(0xFF5F6368)
private val TextoClaro = Color(0xFF8A9094)


// ==========================================================
// MODELO DE PUBLICACIÓN
// ==========================================================

data class Publicacion(
    val titulo: String,
    val ubicacion: String,
    val tiempo: String,
    val encontrado: Boolean,
    val imagen: Int? = null
)


// ==========================================================
// HOME SCREEN
// ==========================================================

@Composable
fun HomeScreen(
    onNavegar: (String) -> Unit = {}
) {

    // ------------------------------------------------------
    // DATOS DE EJEMPLO
    //
    // Para poner fotos, guarda tus imágenes en res/drawable
    // y cambia el comentario, por ejemplo:
    // imagen = R.drawable.foto_llaves
    // (si lo descomentas, pon una coma después de encontrado = ...)
    // ------------------------------------------------------

    val publicaciones = listOf(
        Publicacion(
            titulo = "Llaves con llavero azul",
            ubicacion = "Biblioteca",
            tiempo = "Hace 2 horas",
            encontrado = true
            // imagen = R.drawable.foto_llaves
        ),
        Publicacion(
            titulo = "Mochila negra",
            ubicacion = "Edificio A",
            tiempo = "Hace 5 horas",
            encontrado = false
            // imagen = R.drawable.foto_mochila
        ),
        Publicacion(
            titulo = "Celular Samsung",
            ubicacion = "Cafetería",
            tiempo = "Hace 1 día",
            encontrado = true
            // imagen = R.drawable.foto_celular
        )
    )


    // ------------------------------------------------------
    // FILTRO SELECCIONADO
    // ------------------------------------------------------

    var filtro by remember { mutableStateOf("Todos") }

    val publicacionesFiltradas = when (filtro) {
        "Perdidos" -> publicaciones.filter { !it.encontrado }
        "Encontrados" -> publicaciones.filter { it.encontrado }
        else -> publicaciones
    }


    // ======================================================
    // SCAFFOLD
    // ======================================================

    Scaffold(

        containerColor = Color.White,

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
            // ENCABEZADO
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // AVATAR
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Verde),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "A",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // SALUDO
                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = "Hola, Ana",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )

                    Text(
                        text = "¿Qué te gustaría hacer hoy?",
                        fontSize = 14.sp,
                        color = TextoSecundario
                    )
                }

                // CAMPANA CON PUNTO ROJO
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(FondoGris)
                        .clickable {
                            // Después agregaremos las notificaciones
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.bell_solid),
                        contentDescription = "Notificaciones",
                        modifier = Modifier.size(22.dp),
                        colorFilter = ColorFilter.tint(TextoPrincipal)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 9.dp, end = 11.dp)
                            .size(11.dp)
                            .background(Rojo, CircleShape)
                            .border(1.5.dp, FondoGris, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))


            // ==================================================
            // BUSCADOR (al tocarlo abre la pantalla Buscar)
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FondoGris)
                    .clickable {
                        onNavegar("Buscar")
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.magnifying_glass_solid),
                    contentDescription = "Buscar",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(TextoClaro)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Busca llaves, mochila, celular...",
                    fontSize = 15.sp,
                    color = TextoClaro
                )
            }

            Spacer(modifier = Modifier.height(20.dp))


            // ==================================================
            // BOTONES DE REPORTAR
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                ReportButton(
                    modifier = Modifier.weight(1f),
                    backgroundColor = FondoPerdido,
                    borderColor = Color(0xFFFFCFCF),
                    icon = R.drawable.magnifying_glass_solid,
                    iconTint = Rojo,
                    title = "Reportar",
                    subtitle = "objeto perdido"
                )

                ReportButton(
                    modifier = Modifier.weight(1f),
                    backgroundColor = FondoEncontrado,
                    borderColor = Color(0xFFBFE8D2),
                    icon = R.drawable.plus_solid,
                    iconTint = Verde,
                    title = "Reportar",
                    subtitle = "objeto encontrado"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))


            // ==================================================
            // TÍTULO PUBLICACIONES
            // ==================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Publicaciones recientes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Text(
                    text = "Ver todas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro,
                    modifier = Modifier.clickable {
                        // "Ver todas" lleva a la pantalla Buscar
                        onNavegar("Buscar")
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))


            // ==================================================
            // FILTROS
            // ==================================================

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                listOf("Todos", "Perdidos", "Encontrados").forEach { opcion ->

                    FilterChipUniFind(
                        text = opcion,
                        selected = filtro == opcion,
                        onClick = { filtro = opcion }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))


            // ==================================================
            // LISTA DE PUBLICACIONES
            // ==================================================

            publicacionesFiltradas.forEach { publicacion ->

                PublicationCard(publicacion = publicacion)

                Spacer(modifier = Modifier.height(14.dp))
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

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) Verde else FondoGris)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else TextoSecundario
        )
    }
}


// ==========================================================
// BOTÓN REPORTAR
// ==========================================================

@Composable
fun ReportButton(
    modifier: Modifier,
    backgroundColor: Color,
    borderColor: Color,
    icon: Int,
    iconTint: Color,
    title: String,
    subtitle: String
) {

    val forma = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .height(140.dp)
            .clip(forma)
            .background(backgroundColor)
            .border(1.dp, borderColor, forma)
            .clickable {
                // Después conectaremos las pantallas de reporte
            },
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ICONO DENTRO DE CÍRCULO BLANCO
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(26.dp),
                    colorFilter = ColorFilter.tint(iconTint)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111),
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111),
                textAlign = TextAlign.Center
            )
        }
    }
}


// ==========================================================
// TARJETA DE PUBLICACIÓN
// ==========================================================

@Composable
fun PublicationCard(
    publicacion: Publicacion
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
                // Después abriremos el detalle de la publicación
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ==================================================
        // IMAGEN DEL OBJETO
        // ==================================================

        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFEDEDED)),
            contentAlignment = Alignment.Center
        ) {

            if (publicacion.imagen != null) {

                Image(
                    painter = painterResource(id = publicacion.imagen),
                    contentDescription = publicacion.titulo,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

            } else {

                // Placeholder temporal
                Image(
                    painter = painterResource(id = R.drawable.file_solid),
                    contentDescription = publicacion.titulo,
                    modifier = Modifier.size(34.dp),
                    colorFilter = ColorFilter.tint(Color(0xFFB0B5B8))
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))


        // ==================================================
        // INFORMACIÓN
        // ==================================================

        Column(modifier = Modifier.weight(1f)) {

            // ESTADO
            StatusChip(encontrado = publicacion.encontrado)

            Spacer(modifier = Modifier.height(7.dp))

            // TÍTULO
            Text(
                text = publicacion.titulo,
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
                    text = publicacion.ubicacion,
                    fontSize = 13.sp,
                    color = TextoSecundario
                )

                Spacer(modifier = Modifier.width(14.dp))

                Image(
                    painter = painterResource(id = R.drawable.clock_solid),
                    contentDescription = "Hora",
                    modifier = Modifier.size(14.dp),
                    colorFilter = ColorFilter.tint(Verde)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = publicacion.tiempo,
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


// ==========================================================
// ETIQUETA DE ESTADO (ENCONTRADO / PERDIDO)
// ==========================================================

@Composable
fun StatusChip(encontrado: Boolean) {

    val fondo = if (encontrado) FondoEncontrado else FondoPerdido
    val color = if (encontrado) VerdeOscuro else Rojo
    val texto = if (encontrado) "Encontrado" else "Perdido"

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = texto,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}