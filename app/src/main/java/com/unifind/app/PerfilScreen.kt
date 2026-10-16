package com.unifind.app

import android.widget.Toast
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unifind.app.data.EstadoReporte
import com.unifind.app.data.UniFindRepository
import com.unifind.app.ui.Aparecer
import com.unifind.app.ui.NumeroAnimado
import com.unifind.app.ui.UF
import com.unifind.app.ui.alPresionar

private val PerfilFondo = Color(0xFFF6F9F7)
private val PerfilVerde = Color(0xFF16845B)
private val PerfilTexto = Color(0xFF17231F)
private val PerfilSecundario = Color(0xFF69766F)
private val PerfilBorde = Color(0xFFE3EBE6)
private val PerfilNaranja = Color(0xFFE58A20)

@Composable
fun PerfilScreen(
    onNavegar: (String) -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val contexto = LocalContext.current
    val reportes by UniFindRepository.reportes.collectAsState()
    val usuario = FirebaseAuth.getInstance().currentUser
    val uid = usuario?.uid.orEmpty()

    val nombre = usuario?.displayName
        ?.takeIf { it.isNotBlank() }
        ?: usuario?.email?.substringBefore("@")
        ?: "Usuario"
    val correo = usuario?.email.orEmpty()

    val miUid = UniFindRepository.miUid
    val mios = remember(reportes, miUid) { reportes.filter { it.usuarioId == miUid } }
    val recuperados = mios.count { it.estado == EstadoReporte.RECUPERADO }
    val contactos = mios.sumOf { it.numContactos }

    var dialogoSalir by remember { mutableStateOf(false) }
    var dialogoWhatsapp by remember { mutableStateOf(false) }
    var dialogoAyuda by remember { mutableStateOf(false) }
    var whatsapp by remember { mutableStateOf("") }
    var cargandoWhatsapp by remember { mutableStateOf(true) }
    var guardandoWhatsapp by remember { mutableStateOf(false) }

    // El teléfono se guarda en el documento de ESTA cuenta, no en preferencias locales
    // ni en un campo global compartido por todos los reportes.
    LaunchedEffect(uid) {
        if (uid.isBlank()) {
            whatsapp = ""
            cargandoWhatsapp = false
        } else {
            cargandoWhatsapp = true
            try {
                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { documento ->
                        whatsapp = documento.getString("whatsapp").orEmpty()
                        cargandoWhatsapp = false
                    }
                    .addOnFailureListener {
                        cargandoWhatsapp = false
                        // Un fallo al leer el perfil no debe cerrar la pantalla.
                        Toast.makeText(
                            contexto,
                            "No se pudo cargar tu WhatsApp. Puedes intentarlo de nuevo.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } catch (e: Exception) {
                cargandoWhatsapp = false
                whatsapp = ""
                Toast.makeText(
                    contexto,
                    "No se pudo abrir el perfil. Revisa la configuración de Firebase.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Scaffold(
        containerColor = PerfilFondo,
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
            // Encabezado limpio, sin banner grande.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PerfilFondo)
                    .statusBarsPadding()
                    .padding(start = 22.dp, end = 22.dp, top = 30.dp, bottom = 22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Mi perfil",
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PerfilTexto
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Aparecer(0) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .shadow(8.dp, CircleShape, ambientColor = PerfilVerde)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF20A574), Color(0xFF116847))
                                    ),
                                    CircleShape
                                )
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nombre.firstOrNull()?.uppercase() ?: "U",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = nombre,
                            fontSize = 20.sp,
                            lineHeight = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = PerfilTexto
                        )
                        Text(
                            text = correo,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = PerfilSecundario
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(7.dp)
                                    .background(PerfilVerde, CircleShape)
                            )
                            Text(
                                "Cuenta UniFind",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PerfilVerde
                            )
                        }
                    }
                }

                // Separador editorial fino.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(PerfilVerde, RoundedCornerShape(50))
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(PerfilBorde)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Aparecer(1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(22.dp))
                            .background(Color.White, RoundedCornerShape(22.dp))
                            .padding(vertical = 17.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Estadistica(
                            Modifier.weight(1f),
                            mios.size,
                            "Reportes",
                            PerfilVerde
                        )
                        DivisorEstadistica()
                        Estadistica(
                            Modifier.weight(1f),
                            recuperados,
                            "Recuperados",
                            Color(0xFF218A62)
                        )
                        DivisorEstadistica()
                        Estadistica(
                            Modifier.weight(1f),
                            contactos,
                            "Contactos",
                            PerfilNaranja
                        )
                    }
                }

                Text(
                    text = "MI CUENTA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                    color = PerfilSecundario,
                    modifier = Modifier.padding(start = 2.dp).offset(y = (-8).dp)
                )

                Aparecer(2) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(22.dp))
                            .background(Color.White, RoundedCornerShape(22.dp))
                    ) {
                        ItemPerfil(
                            icono = R.drawable.file_solid,
                            titulo = "Mis publicaciones",
                            detalle = "${mios.size} reportes publicados",
                            colorIcono = PerfilVerde
                        ) { onNavegar("Mis reportes") }

                        Divisor()

                        ItemPerfil(
                            icono = R.drawable.square_whatsapp_brands_solid,
                            titulo = "Mi WhatsApp",
                            detalle = when {
                                cargandoWhatsapp -> "Cargando número guardado…"
                                whatsapp.length == 10 -> whatsapp
                                else -> "Añade tu número personal"
                            },
                            colorIcono = Color(0xFF1C9D59),
                            sinTinte = true,
                            mostrarCargando = cargandoWhatsapp
                        ) { dialogoWhatsapp = true }

                        Divisor()

                        ItemPerfil(
                            icono = R.drawable.eye_solid,
                            titulo = "Cómo funciona UniFind",
                            detalle = "Aprende a reportar y recuperar objetos",
                            colorIcono = Color(0xFF4386D8)
                        ) { dialogoAyuda = true }

                        Divisor()

                        ItemPerfil(
                            icono = R.drawable.unlock_solid,
                            titulo = "Cerrar sesión",
                            detalle = "Salir de tu cuenta de forma segura",
                            colorIcono = UF.Rojo,
                            colorTitulo = UF.Rojo
                        ) { dialogoSalir = true }
                    }
                }

                Text(
                    text = "UniFind  ·  UT Santa Catarina",
                    fontSize = 12.sp,
                    color = PerfilSecundario,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (dialogoSalir) {
        AlertDialog(
            onDismissRequest = { dialogoSalir = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
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
                    Text("Cancelar", color = PerfilSecundario)
                }
            }
        )
    }

    if (dialogoWhatsapp) {
        var texto by remember(whatsapp) { mutableStateOf(whatsapp) }
        val telefonoValido = texto.length == 10

        AlertDialog(
            onDismissRequest = { if (!guardandoWhatsapp) dialogoWhatsapp = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(26.dp),
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Tu WhatsApp", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "Este número pertenece únicamente a tu cuenta.",
                        fontSize = 13.sp,
                        color = PerfilSecundario
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Se guardará en tu perfil de Firebase. No se cambiará el número de otras personas.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = PerfilSecundario
                    )
                    OutlinedTextField(
                        value = texto,
                        onValueChange = { nuevo ->
                            texto = nuevo.filter(Char::isDigit).take(10)
                        },
                        label = { Text("Número de celular") },
                        placeholder = { Text("Ej. 8123456789") },
                        leadingIcon = {
                            Text(
                                "+52",
                                fontWeight = FontWeight.Bold,
                                color = PerfilVerde,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        },
                        supportingText = {
                            Text(
                                if (telefonoValido) "Número listo para guardar"
                                else "Escribe los 10 dígitos de tu número"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PerfilTexto,
                            unfocusedTextColor = PerfilTexto,
                            focusedBorderColor = PerfilVerde,
                            unfocusedBorderColor = PerfilBorde,
                            cursorColor = PerfilVerde,
                            focusedLabelColor = PerfilVerde
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = telefonoValido && !guardandoWhatsapp && uid.isNotBlank(),
                    onClick = {
                        guardandoWhatsapp = true
                        // Firestore crea el documento/campo si aún no existe.
                        try {
                            FirebaseFirestore.getInstance()
                                .collection("usuarios")
                                .document(uid)
                                .set(
                                    mapOf(
                                        "whatsapp" to texto,
                                        "uid" to uid
                                    ),
                                    com.google.firebase.firestore.SetOptions.merge()
                                )
                                .addOnSuccessListener {
                                    whatsapp = texto
                                    guardandoWhatsapp = false
                                    dialogoWhatsapp = false
                                    Toast.makeText(
                                        contexto,
                                        "WhatsApp guardado en tu perfil",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { error ->
                                    guardandoWhatsapp = false
                                    Toast.makeText(
                                        contexto,
                                        "No se pudo guardar: ${error.localizedMessage ?: "intenta de nuevo"}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        } catch (e: Exception) {
                            guardandoWhatsapp = false
                            Toast.makeText(
                                contexto,
                                "No se pudo guardar el número. Revisa la configuración de Firebase.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                ) {
                    if (guardandoWhatsapp) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = PerfilVerde,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar número", color = PerfilVerde, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !guardandoWhatsapp,
                    onClick = { dialogoWhatsapp = false }
                ) {
                    Text("Cancelar", color = PerfilSecundario)
                }
            }
        )
    }

    if (dialogoAyuda) {
        AlertDialog(
            onDismissRequest = { dialogoAyuda = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("¿Cómo funciona UniFind?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "1. Publica un objeto perdido o encontrado.\n\n" +
                            "2. Busca reportes que coincidan con el objeto.\n\n" +
                            "3. Contacta a la otra persona para coordinar la entrega.\n\n" +
                            "4. Marca el reporte como recuperado cuando termine el proceso.",
                    lineHeight = 21.sp,
                    color = PerfilTexto
                )
            },
            confirmButton = {
                TextButton(onClick = { dialogoAyuda = false }) {
                    Text("Entendido", color = PerfilVerde, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun Estadistica(
    modifier: Modifier,
    valor: Int,
    texto: String,
    color: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        NumeroAnimado(valor = valor, color = color, tamano = 24.sp)
        Text(
            text = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = PerfilSecundario,
            maxLines = 1
        )
    }
}

@Composable
private fun DivisorEstadistica() {
    Box(
        Modifier
            .width(1.dp)
            .height(36.dp)
            .background(PerfilBorde)
    )
}

@Composable
private fun ItemPerfil(
    icono: Int,
    titulo: String,
    detalle: String,
    colorIcono: Color,
    colorTitulo: Color = PerfilTexto,
    sinTinte: Boolean = false,
    mostrarCargando: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alPresionar(onClick)
            .padding(horizontal = 15.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    colorIcono.copy(alpha = 0.11f),
                    RoundedCornerShape(15.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(21.dp),
                colorFilter = if (sinTinte) null else ColorFilter.tint(colorIcono)
            )
        }

        Spacer(Modifier.width(13.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorTitulo
            )
            Text(
                text = detalle,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = PerfilSecundario
            )
        }

        if (mostrarCargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(17.dp),
                color = PerfilVerde,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "›",
                fontSize = 26.sp,
                color = PerfilSecundario
            )
        }
    }
}

@Composable
private fun Divisor() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 74.dp, end = 16.dp)
            .height(1.dp)
            .background(PerfilBorde)
    )
}
