package com.unifind.app

import android.util.Patterns

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

import kotlinx.coroutines.delay

@Composable
fun RegistroScreen(
    onBack: () -> Unit
) {
    // =====================================================
    // COLORES
    // =====================================================

    val verde = Color(0xFF006B50)
    val verdeOscuro = Color(0xFF004D3A)
    val verdeMedio = Color(0xFF168568)
    val verdeClaro = Color(0xFFE8F7F0)
    val fondo = Color(0xFFFFFEF9)
    val texto = Color(0xFF26332F)
    val gris = Color(0xFF77827F)
    val borde = Color(0xFFDDE5E1)
    val rojo = Color(0xFFB3261E)
    val fondoRojo = Color(0xFFFFF0EE)

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    // =====================================================
    // ESTADOS
    // =====================================================

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarConfirmar by remember { mutableStateOf(false) }

    var registrando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var registroExitoso by remember { mutableStateOf(false) }

    // El panel empieza a aparecer inmediatamente.
    var mostrarPanel by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        mostrarPanel = true
    }

    // =====================================================
    // ALERTAS AUTOMÁTICAS
    // =====================================================

    LaunchedEffect(mensaje, registroExitoso) {
        if (mensaje.isNotBlank()) {
            if (registroExitoso) {
                delay(2500)
                mensaje = ""
                registroExitoso = false
                onBack()
            } else {
                delay(3500)
                mensaje = ""
            }
        }
    }

    // =====================================================
    // PANTALLA
    // =====================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
    ) {
        // Fondo ilustrado.
        Image(
            painter = painterResource(
                id = R.drawable.banner_registro
            ),
            contentDescription = "Fondo ilustrado de UniFind",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Velo muy ligero para conservar los colores.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(alpha = 0.035f)
                )
        )

        // =================================================
        // PANEL INFERIOR ANIMADO
        // =================================================

        AnimatedVisibility(
            visible = mostrarPanel,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.84f),
            enter = slideInVertically(
                initialOffsetY = { altura -> altura },
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 350
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { altura -> altura },
                animationSpec = tween(
                    durationMillis = 350
                )
            ) + fadeOut(
                animationSpec = tween(200)
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = 22.dp,
                        shape = RoundedCornerShape(
                            topStart = 44.dp,
                            topEnd = 44.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        clip = false
                    ),
                shape = RoundedCornerShape(
                    topStart = 44.dp,
                    topEnd = 44.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .imePadding()
                ) {
                    // =====================================
                    // CABECERA VERDE
                    // =====================================

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        verdeOscuro,
                                        verde,
                                        verdeMedio
                                    )
                                ),
                                shape = RoundedCornerShape(
                                    topStart = 44.dp,
                                    topEnd = 44.dp
                                )
                            )
                            .padding(
                                horizontal = 24.dp,
                                vertical = 24.dp
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ICONO BLANCO
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .background(
                                        Color.White.copy(
                                            alpha = 0.16f
                                        ),
                                        RoundedCornerShape(19.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = R.drawable.user_solid
                                    ),
                                    contentDescription = "Usuario",
                                    modifier = Modifier.size(29.dp),
                                    colorFilter = ColorFilter.tint(
                                        Color.White
                                    )
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(16.dp)
                            )

                            Column {
                                Text(
                                    text = "Crear cuenta",
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(
                                    modifier = Modifier.height(5.dp)
                                )

                                Text(
                                    text = "Únete a la comunidad UniFind",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(
                                        alpha = 0.9f
                                    )
                                )
                            }
                        }
                    }

                    // =====================================
                    // FORMULARIO
                    // =====================================

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 22.dp,
                                vertical = 24.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Crea tu cuenta para comenzar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = texto,
                            textAlign = TextAlign.Center
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Utiliza tu correo institucional UTSC.",
                            fontSize = 13.sp,
                            color = gris,
                            textAlign = TextAlign.Center
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        // =================================
                        // NOMBRE
                        // =================================

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = {
                                nombre = it
                                mensaje = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            singleLine = true,
                            leadingIcon = {
                                Image(
                                    painter = painterResource(
                                        id = R.drawable.user_solid
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Nombre completo",
                                    color = gris,
                                    fontSize = 14.sp
                                )
                            },
                            textStyle = TextStyle(
                                color = texto,
                                fontSize = 15.sp
                            ),
                            shape = RoundedCornerShape(17.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = verde,
                                unfocusedBorderColor = borde,
                                focusedContainerColor = verdeClaro.copy(
                                    alpha = 0.35f
                                ),
                                unfocusedContainerColor = Color.White,
                                cursorColor = verde
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            )
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        // =================================
                        // CORREO
                        // =================================

                        OutlinedTextField(
                            value = correo,
                            onValueChange = {
                                correo = it
                                mensaje = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            singleLine = true,
                            leadingIcon = {
                                Image(
                                    painter = painterResource(
                                        id = R.drawable.envelope_solid
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Correo institucional",
                                    color = gris,
                                    fontSize = 14.sp
                                )
                            },
                            textStyle = TextStyle(
                                color = texto,
                                fontSize = 15.sp
                            ),
                            shape = RoundedCornerShape(17.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = verde,
                                unfocusedBorderColor = borde,
                                focusedContainerColor = verdeClaro.copy(
                                    alpha = 0.35f
                                ),
                                unfocusedContainerColor = Color.White,
                                cursorColor = verde
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            )
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        // =================================
                        // CONTRASEÑA
                        // =================================

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                mensaje = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            singleLine = true,
                            leadingIcon = {
                                Image(
                                    painter = painterResource(
                                        id = R.drawable.unlock_solid
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Contraseña",
                                    color = gris,
                                    fontSize = 14.sp
                                )
                            },
                            textStyle = TextStyle(
                                color = texto,
                                fontSize = 15.sp
                            ),
                            visualTransformation =
                                if (mostrarPassword) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        mostrarPassword =
                                            !mostrarPassword
                                    }
                                ) {
                                    Icon(
                                        imageVector =
                                            if (mostrarPassword) {
                                                Icons.Default.VisibilityOff
                                            } else {
                                                Icons.Default.Visibility
                                            },
                                        contentDescription =
                                            if (mostrarPassword) {
                                                "Ocultar contraseña"
                                            } else {
                                                "Mostrar contraseña"
                                            },
                                        tint = verde
                                    )
                                }
                            },
                            shape = RoundedCornerShape(17.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = verde,
                                unfocusedBorderColor = borde,
                                focusedContainerColor = verdeClaro.copy(
                                    alpha = 0.35f
                                ),
                                unfocusedContainerColor = Color.White,
                                cursorColor = verde
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            )
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        // =================================
                        // CONFIRMAR CONTRASEÑA
                        // =================================

                        OutlinedTextField(
                            value = confirmarPassword,
                            onValueChange = {
                                confirmarPassword = it
                                mensaje = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            singleLine = true,
                            leadingIcon = {
                                Image(
                                    painter = painterResource(
                                        id = R.drawable.unlock_solid
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Confirmar contraseña",
                                    color = gris,
                                    fontSize = 14.sp
                                )
                            },
                            textStyle = TextStyle(
                                color = texto,
                                fontSize = 15.sp
                            ),
                            visualTransformation =
                                if (mostrarConfirmar) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        mostrarConfirmar =
                                            !mostrarConfirmar
                                    }
                                ) {
                                    Icon(
                                        imageVector =
                                            if (mostrarConfirmar) {
                                                Icons.Default.VisibilityOff
                                            } else {
                                                Icons.Default.Visibility
                                            },
                                        contentDescription =
                                            if (mostrarConfirmar) {
                                                "Ocultar contraseña"
                                            } else {
                                                "Mostrar contraseña"
                                            },
                                        tint = verde
                                    )
                                }
                            },
                            shape = RoundedCornerShape(17.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = verde,
                                unfocusedBorderColor = borde,
                                focusedContainerColor = verdeClaro.copy(
                                    alpha = 0.35f
                                ),
                                unfocusedContainerColor = Color.White,
                                cursorColor = verde
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            )
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        // =================================
                        // BOTÓN CREAR CUENTA
                        // =================================

                        Button(
                            onClick = {
                                if (registrando) {
                                    return@Button
                                }

                                val nombreLimpio = nombre.trim()
                                val correoLimpio = correo
                                    .trim()
                                    .lowercase()

                                if (nombreLimpio.length < 2) {
                                    mensaje =
                                        "Ingresa tu nombre completo."
                                    registroExitoso = false
                                    return@Button
                                }

                                if (
                                    !Patterns.EMAIL_ADDRESS
                                        .matcher(correoLimpio)
                                        .matches()
                                ) {
                                    mensaje =
                                        "Ingresa un correo válido."
                                    registroExitoso = false
                                    return@Button
                                }

                                val dominioInstitucional = Regex(
                                    "^[A-Za-z0-9._%+-]+@virtual\\.utsc\\.edu\\.mx$"
                                )

                                if (
                                    !dominioInstitucional.matches(
                                        correoLimpio
                                    )
                                ) {
                                    mensaje =
                                        "Utiliza tu correo institucional UTSC."
                                    registroExitoso = false
                                    return@Button
                                }

                                if (password.length < 8) {
                                    mensaje =
                                        "Usa al menos 8 caracteres."
                                    registroExitoso = false
                                    return@Button
                                }

                                if (password.none { it.isUpperCase() }) {
                                    mensaje =
                                        "Agrega al menos una mayúscula."
                                    registroExitoso = false
                                    return@Button
                                }

                                if (password != confirmarPassword) {
                                    mensaje =
                                        "Las contraseñas no coinciden."
                                    registroExitoso = false
                                    return@Button
                                }

                                registrando = true
                                mensaje = ""
                                registroExitoso = false

                                auth.createUserWithEmailAndPassword(
                                    correoLimpio,
                                    password
                                ).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val usuario = auth.currentUser

                                        if (usuario == null) {
                                            registrando = false
                                            mensaje =
                                                "Cuenta creada, pero no se pudo obtener el usuario."
                                            registroExitoso = false
                                        } else {
                                            val perfil =
                                                userProfileChangeRequest {
                                                    displayName = nombreLimpio
                                                }

                                            usuario.updateProfile(perfil)
                                                .addOnCompleteListener { perfilTask ->
                                                    registrando = false

                                                    if (perfilTask.isSuccessful) {
                                                        nombre = ""
                                                        correo = ""
                                                        password = ""
                                                        confirmarPassword = ""
                                                        mostrarPassword = false
                                                        mostrarConfirmar = false

                                                        mensaje =
                                                            "¡Cuenta creada correctamente!"
                                                        registroExitoso = true
                                                    } else {
                                                        // Firebase ya creó la cuenta.
                                                        mensaje =
                                                            "Cuenta creada, pero no se pudo guardar el nombre."
                                                        registroExitoso = false
                                                    }
                                                }
                                        }
                                    } else {
                                        registrando = false

                                        val error = task.exception
                                            ?.message.orEmpty()

                                        mensaje = when {
                                            error.contains(
                                                "already in use",
                                                ignoreCase = true
                                            ) ||
                                                    error.contains(
                                                        "email address is already",
                                                        ignoreCase = true
                                                    ) ->
                                                "Este correo ya está registrado."

                                            error.contains(
                                                "network",
                                                ignoreCase = true
                                            ) ->
                                                "Comprueba tu conexión a internet."

                                            error.contains(
                                                "weak password",
                                                ignoreCase = true
                                            ) ->
                                                "La contraseña es demasiado débil."

                                            else ->
                                                "No se pudo crear la cuenta. Inténtalo de nuevo."
                                        }

                                        registroExitoso = false
                                    }
                                }
                            },
                            enabled = !registrando,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = verde,
                                disabledContainerColor = verde.copy(
                                    alpha = 0.55f
                                )
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 1.dp
                            )
                        ) {
                            if (registrando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Text(
                                    text = "Creando cuenta...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "Crear mi cuenta",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(22.dp)
                        )

                        // =================================
                        // SEPARADOR
                        // =================================

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(borde)
                            )

                            Text(
                                text = "  UniFind  ",
                                fontSize = 12.sp,
                                color = gris,
                                fontWeight = FontWeight.SemiBold
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(borde)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        Text(
                            text = "Al registrarte aceptas nuestros",
                            fontSize = 13.sp,
                            color = gris,
                            textAlign = TextAlign.Center
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text = "Términos y Condiciones",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = verde,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.clickable {
                                // Conecta aquí la pantalla de términos.
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }

        // =================================================
        // BOTÓN REGRESAR
        // =================================================

        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    start = 20.dp,
                    top = 14.dp
                )
                .size(48.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    Color.White.copy(alpha = 0.97f),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    if (!registrando) {
                        onBack()
                    }
                },
                enabled = !registrando
            ) {
                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = verdeOscuro,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // =================================================
        // ALERTA FLOTANTE MEJORADA
        // =================================================

        AnimatedVisibility(
            visible = mensaje.isNotBlank(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 72.dp
                ),
            enter = fadeIn(
                animationSpec = tween(220)
            ) + slideInHorizontally(
                initialOffsetX = { it / 3 },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = fadeOut(
                animationSpec = tween(180)
            ) + slideOutHorizontally(
                targetOffsetX = { it / 3 },
                animationSpec = tween(180)
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (registroExitoso) {
                        verdeClaro
                    } else {
                        fondoRojo
                    }
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (registroExitoso) {
                        verde.copy(alpha = 0.18f)
                    } else {
                        rojo.copy(alpha = 0.15f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 15.dp,
                            vertical = 14.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = if (registroExitoso) {
                                    verde.copy(alpha = 0.12f)
                                } else {
                                    rojo.copy(alpha = 0.09f)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (registroExitoso) {
                                Icons.Default.CheckCircle
                            } else {
                                Icons.Default.Error
                            },
                            contentDescription = null,
                            tint = if (registroExitoso) {
                                verde
                            } else {
                                rojo
                            },
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (registroExitoso) {
                                "¡Todo listo!"
                            } else {
                                "Revisa este detalle"
                            },
                            color = if (registroExitoso) {
                                verdeOscuro
                            } else {
                                rojo
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = mensaje,
                            color = texto,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}