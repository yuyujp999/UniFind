package com.unifind.app

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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

private val TextoPrincipal = Color(0xFF202124)
private val TextoSecundario = Color(0xFF5F6368)

private val MoradoAvatar = Color(0xFF6C63C7)


// ==========================================================
// PANTALLA PERFIL
// ==========================================================

@Composable
fun PerfilScreen(
    onNavegar: (String) -> Unit = {},
    nombre: String = "Ana García",
    correo: String = "ana@universidad.edu.mx",
    reportes: Int = 4,
    recuperados: Int = 1,
    contactos: Int = 4,
    onCerrarSesion: () -> Unit = {}
) {

    var mostrarDialogo by remember { mutableStateOf(false) }

    val inicial = nombre.trim().firstOrNull()?.uppercase() ?: "?"

    Scaffold(

        containerColor = Color.White,

        bottomBar = {
            UniFindBottomBar(
                seleccionado = "Perfil",
                onSeleccionar = onNavegar
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {

            // ==================================================
            // ENCABEZADO CON DEGRADADO SUAVE
            // ==================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(FondoEncontrado, Color.White)
                        )
                    )
                    .statusBarsPadding()
                    .padding(top = 28.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // AVATAR
                Box(
                    modifier = Modifier
                        .size(98.dp)
                        .shadow(10.dp, CircleShape, ambientColor = MoradoAvatar, spotColor = MoradoAvatar)
                        .background(MoradoAvatar, CircleShape)
                        .border(4.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = inicial,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = nombre,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = correo,
                    fontSize = 14.sp,
                    color = TextoSecundario
                )
            }


            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                Spacer(modifier = Modifier.height(16.dp))


                // ==================================================
                // ESTADÍSTICAS
                // ==================================================

                val formaStats = RoundedCornerShape(20.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = formaStats,
                            ambientColor = Color(0x22000000),
                            spotColor = Color(0x22000000)
                        )
                        .background(Color.White, formaStats)
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    EstadisticaPerfil(Modifier.weight(1f), reportes.toString(), "Reportes")

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(34.dp)
                            .background(FondoGris)
                    )

                    EstadisticaPerfil(Modifier.weight(1f), recuperados.toString(), "Recuperados")

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(34.dp)
                            .background(FondoGris)
                    )

                    EstadisticaPerfil(Modifier.weight(1f), contactos.toString(), "Contactos")
                }

                Spacer(modifier = Modifier.height(20.dp))


                // ==================================================
                // MENÚ
                // ==================================================

                val formaMenu = RoundedCornerShape(20.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = formaMenu,
                            ambientColor = Color(0x22000000),
                            spotColor = Color(0x22000000)
                        )
                        .background(Color.White, formaMenu)
                        .clip(formaMenu)
                ) {

                    ItemPerfil(
                        icono = Icons.Outlined.List,
                        texto = "Mis publicaciones",
                        onClick = { onNavegar("Mis reportes") }
                    )

                    DivisorPerfil()

                    ItemPerfil(
                        icono = Icons.Outlined.Notifications,
                        texto = "Notificaciones",
                        onClick = { /* Pendiente */ }
                    )

                    DivisorPerfil()

                    ItemPerfil(
                        icono = Icons.Outlined.Edit,
                        texto = "Editar perfil",
                        onClick = { /* Pendiente */ }
                    )

                    DivisorPerfil()

                    ItemPerfil(
                        icono = Icons.Outlined.Lock,
                        texto = "Cambiar contraseña",
                        onClick = { /* Pendiente */ }
                    )

                    DivisorPerfil()

                    ItemPerfil(
                        icono = Icons.Outlined.Info,
                        texto = "Ayuda",
                        onClick = { /* Pendiente */ }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                // ==================================================
                // CERRAR SESIÓN
                // ==================================================

                val formaSalir = RoundedCornerShape(20.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(formaSalir)
                        .background(FondoPerdido)
                        .border(1.dp, Rojo.copy(alpha = 0.35f), formaSalir)
                        .clickable { mostrarDialogo = true }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = Rojo,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Cerrar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Rojo
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "UniFind · Versión 1.0",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 12.sp,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }


    // ======================================================
    // CONFIRMACIÓN PARA CERRAR SESIÓN
    // ======================================================

    if (mostrarDialogo) {

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = "¿Cerrar sesión?",
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
            },
            text = {
                Text(
                    text = "Tendrás que iniciar sesión de nuevo para ver tus reportes.",
                    color = TextoSecundario
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogo = false
                        onCerrarSesion()
                    }
                ) {
                    Text(text = "Cerrar sesión", color = Rojo, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text(text = "Cancelar", color = VerdeOscuro, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}


// ==========================================================
// ESTADÍSTICA (NÚMERO + ETIQUETA)
// ==========================================================

@Composable
private fun EstadisticaPerfil(
    modifier: Modifier,
    valor: String,
    etiqueta: String
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = valor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeOscuro
        )

        Text(
            text = etiqueta,
            fontSize = 12.sp,
            color = TextoSecundario,
            maxLines = 1
        )
    }
}


// ==========================================================
// ITEM DEL MENÚ
// ==========================================================

@Composable
private fun ItemPerfil(
    icono: ImageVector,
    texto: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FondoEncontrado),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icono,
                contentDescription = texto,
                tint = VerdeOscuro,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = texto,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextoPrincipal
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextoSecundario,
            modifier = Modifier.size(24.dp)
        )
    }
}


// ==========================================================
// DIVISOR ENTRE ITEMS
// ==========================================================

@Composable
private fun DivisorPerfil() {

    HorizontalDivider(
        modifier = Modifier.padding(start = 70.dp, end = 16.dp),
        thickness = 1.dp,
        color = FondoGris
    )
}