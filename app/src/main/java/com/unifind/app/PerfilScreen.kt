package com.unifind.app

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.unifind.app.data.EstadoReporte
import com.unifind.app.data.UniFindRepository
import com.unifind.app.ui.Aparecer
import com.unifind.app.ui.NumeroAnimado
import com.unifind.app.ui.UF
import com.unifind.app.ui.alPresionar

// ==========================================================
// PERFIL
// Datos reales de la cuenta y estadísticas del usuario.
// ==========================================================

@Composable
fun PerfilScreen(
    onNavegar: (String) -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {

    val contexto = LocalContext.current

    val reportes by UniFindRepository.reportes.collectAsState()

    val usuario = FirebaseAuth.getInstance().currentUser

    val nombre = usuario?.displayName
        ?.takeIf { it.isNotBlank() }
        ?: usuario?.email?.substringBefore("@")
        ?: "Usuario"

    val correo = usuario?.email.orEmpty()

    val miUid = UniFindRepository.miUid

    val mios = remember(reportes, miUid) {
        reportes.filter { it.usuarioId == miUid }
    }

    val recuperados = mios.count { it.estado == EstadoReporte.RECUPERADO }
    val contactos = mios.sumOf { it.numContactos }

    var dialogoSalir by remember { mutableStateOf(false) }
    var dialogoWhatsapp by remember { mutableStateOf(false) }
    var dialogoAyuda by remember { mutableStateOf(false) }

    var whatsapp by remember {
        mutableStateOf(
            contexto
                .getSharedPreferences("unifind", Context.MODE_PRIVATE)
                .getString("whatsapp", "")
                .orEmpty()
        )
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            UniFindBottomBar(
                seleccionado = "Perfil",
                onSeleccionar = onNavegar
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {

            // ---------- ENCABEZADO ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(UF.Menta, Color.White))
                    )
                    .statusBarsPadding()
                    .padding(top = 28.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Aparecer(0) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Box(
                            modifier = Modifier
                                .size(98.dp)
                                .shadow(
                                    10.dp, CircleShape,
                                    ambientColor = UF.Verde, spotColor = UF.Verde
                                )
                                .background(UF.Verde, CircleShape)
                                .border(4.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nombre.firstOrNull()?.uppercase() ?: "U",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = nombre,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = UF.Texto
                        )

                        Text(
                            text = correo,
                            fontSize = 14.sp,
                            color = UF.TextoSec
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Spacer(Modifier.height(4.dp))

                // ---------- ESTADÍSTICAS ----------
                Aparecer(1) {

                    val forma = RoundedCornerShape(20.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                4.dp, forma,
                                ambientColor = Color(0x22000000),
                                spotColor = Color(0x22000000)
                            )
                            .background(Color.White, forma)
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Estadistica(Modifier.weight(1f), mios.size, "Reportes")

                        Box(
                            Modifier
                                .width(1.dp)
                                .height(34.dp)
                                .background(UF.Gris)
                        )

                        Estadistica(Modifier.weight(1f), recuperados, "Recuperados")

                        Box(
                            Modifier
                                .width(1.dp)
                                .height(34.dp)
                                .background(UF.Gris)
                        )

                        Estadistica(Modifier.weight(1f), contactos, "Contactos")
                    }
                }

                // ---------- OPCIONES ----------
                Aparecer(2) {

                    val forma = RoundedCornerShape(20.dp)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                4.dp, forma,
                                ambientColor = Color(0x22000000),
                                spotColor = Color(0x22000000)
                            )
                            .background(Color.White, forma)
                    ) {

                        ItemPerfil(
                            icono = R.drawable.file_solid,
                            titulo = "Mis publicaciones",
                            detalle = "${mios.size} en total"
                        ) { onNavegar("Mis reportes") }

                        Divisor()

                        ItemPerfil(
                            icono = R.drawable.square_whatsapp_brands_solid,
                            titulo = "Mi WhatsApp",
                            detalle = if (whatsapp.length == 10) whatsapp
                            else "Agrégalo para que te contacten",
                            sinTinte = true
                        ) { dialogoWhatsapp = true }

                        Divisor()

                        ItemPerfil(
                            icono = R.drawable.eye_solid,
                            titulo = "¿Cómo funciona UniFind?",
                            detalle = "Reporta, busca y recupera"
                        ) { dialogoAyuda = true }

                        Divisor()

                        ItemPerfil(
                            icono = R.drawable.unlock_solid,
                            titulo = "Cerrar sesión",
                            detalle = "Salir de tu cuenta",
                            colorTitulo = UF.Rojo
                        ) { dialogoSalir = true }
                    }
                }

                Text(
                    text = "UniFind · UT Santa Catarina, Campus Montemorelos",
                    fontSize = 12.sp,
                    color = UF.TextoSec,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

    // ---------- DIÁLOGOS ----------

    if (dialogoSalir) {

        AlertDialog(
            onDismissRequest = { dialogoSalir = false },
            containerColor = Color.White,
            title = { Text("¿Cerrar sesión?", fontWeight = FontWeight.Bold) },
            text = { Text("Tendrás que iniciar sesión otra vez para usar UniFind.") },
            confirmButton = {
                TextButton(onClick = {
                    dialogoSalir = false
                    onCerrarSesion()
                }) {
                    Text("Cerrar sesión", color = UF.Rojo, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogoSalir = false }) {
                    Text("Cancelar", color = UF.TextoSec)
                }
            }
        )
    }

    if (dialogoWhatsapp) {

        var texto by remember { mutableStateOf(whatsapp) }

        AlertDialog(
            onDismissRequest = { dialogoWhatsapp = false },
            containerColor = Color.White,
            title = { Text("Mi WhatsApp", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    Text(
                        "Se usa para que otras personas te contacten cuando " +
                            "publiques o reclames un objeto.",
                        fontSize = 14.sp,
                        color = UF.TextoSec
                    )

                    OutlinedTextField(
                        value = texto,
                        onValueChange = {
                            texto = it.filter { c -> c.isDigit() }.take(10)
                        },
                        label = { Text("10 dígitos") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = UF.Verde,
                            cursorColor = UF.Verde,
                            focusedLabelColor = UF.VerdeOscuro
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = texto.length == 10,
                    onClick = {

                        whatsapp = texto

                        contexto
                            .getSharedPreferences("unifind", Context.MODE_PRIVATE)
                            .edit()
                            .putString("whatsapp", texto)
                            .apply()

                        UniFindRepository.guardarWhatsapp(texto)

                        Toast.makeText(contexto, "Número guardado", Toast.LENGTH_SHORT).show()

                        dialogoWhatsapp = false
                    }
                ) {
                    Text("Guardar", color = UF.Verde, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogoWhatsapp = false }) {
                    Text("Cancelar", color = UF.TextoSec)
                }
            }
        )
    }

    if (dialogoAyuda) {

        AlertDialog(
            onDismissRequest = { dialogoAyuda = false },
            containerColor = Color.White,
            title = { Text("¿Cómo funciona?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "1. Publica lo que perdiste o encontraste.\n\n" +
                        "2. Busca en la lista; si alguien ya lo reportó, " +
                        "toca «Yo lo encontré» o «Es mío».\n\n" +
                        "3. Habla con la otra persona por WhatsApp.\n\n" +
                        "4. Cuando se entregue, marca el reporte como recuperado.",
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { dialogoAyuda = false }) {
                    Text("Entendido", color = UF.Verde, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun Estadistica(modifier: Modifier, valor: Int, texto: String) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        NumeroAnimado(valor = valor, color = UF.VerdeOscuro, tamano = 24.sp)

        Text(text = texto, fontSize = 12.sp, color = UF.TextoSec)
    }
}

@Composable
private fun ItemPerfil(
    icono: Int,
    titulo: String,
    detalle: String,
    colorTitulo: Color = UF.Texto,
    sinTinte: Boolean = false,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alPresionar(onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    if (colorTitulo == UF.Rojo) UF.RojoSuave else UF.Menta,
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = if (sinTinte) null else ColorFilter.tint(
                    if (colorTitulo == UF.Rojo) UF.Rojo else UF.VerdeOscuro
                )
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorTitulo
            )

            Text(text = detalle, fontSize = 13.sp, color = UF.TextoSec)
        }

        Text(text = "›", fontSize = 22.sp, color = UF.TextoSec)
    }
}

@Composable
private fun Divisor() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 72.dp)
            .height(1.dp)
            .background(UF.Gris)
    )
}
