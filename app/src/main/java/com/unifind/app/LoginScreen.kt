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
import androidx.compose.material.icons.filled.Info
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

import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    // =====================================================
    // COLORES UNIFIND
    // =====================================================

    val verde = Color(0xFF006B50)
    val verdeOscuro = Color(0xFF004D3A)
    val verdeMedio = Color(0xFF168568)
    val verdeClaro = Color(0xFFE8F7F0)
    val verdeSuave = Color(0xFFF1FAF6)

    val texto = Color(0xFF26332F)
    val gris = Color(0xFF77827F)
    val borde = Color(0xFFDDE5E1)

    val rojo = Color(0xFFB3261E)
    val fondoRojo = Color(0xFFFFF0EE)

    val azul = Color(0xFF356AE6)
    val fondoAzul = Color(0xFFEAF0FF)

    val fondo = Color(0xFFFFFEF9)

    // =====================================================
    // FIREBASE
    // =====================================================

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    // =====================================================
    // ESTADOS
    // =====================================================

    var correo by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var mostrarPassword by remember {
        mutableStateOf(false)
    }

    var procesando by remember {
        mutableStateOf(false)
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    var tipoNotificacion by remember {
        mutableStateOf("error")
    }

    var mostrarNotificacion by remember {
        mutableStateOf(false)
    }

    var notificacionId by remember {
        mutableStateOf(0)
    }

    var loginExitoso by remember {
        mutableStateOf(false)
    }

    var mostrarPanel by remember {
        mutableStateOf(false)
    }

    // =====================================================
    // ANIMACIÓN INICIAL
    // =====================================================

    LaunchedEffect(Unit) {
        mostrarPanel = true
    }

    // =====================================================
    // NOTIFICACIONES AUTOMÁTICAS
    // =====================================================

    LaunchedEffect(notificacionId) {
        if (mostrarNotificacion && mensaje.isNotBlank()) {
            delay(3200)
            mostrarNotificacion = false
        }
    }

    // =====================================================
    // NAVEGACIÓN DESPUÉS DEL LOGIN
    // =====================================================

    LaunchedEffect(loginExitoso) {
        if (loginExitoso) {
            delay(1400)
            onLogin()
        }
    }

    // =====================================================
    // FUNCIÓN PARA MOSTRAR ALERTAS
    // =====================================================

    fun mostrarMensaje(
        textoMensaje: String,
        tipo: String
    ) {
        mensaje = textoMensaje
        tipoNotificacion = tipo
        mostrarNotificacion = true
        notificacionId++
    }

    // =====================================================
    // RECUPERAR CONTRASEÑA
    // =====================================================

    fun recuperarPassword() {
        if (procesando) {
            return
        }

        val correoLimpio = correo.trim().lowercase()

        if (correoLimpio.isBlank()) {
            mostrarMensaje(
                "Escribe tu correo para recuperar tu contraseña.",
                "error"
            )
            return
        }

        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(correoLimpio)
                .matches()
        ) {
            mostrarMensaje(
                "Ingresa un correo electrónico válido.",
                "error"
            )
            return
        }

        val correoInstitucional = Regex(
            "^[A-Za-z0-9._%+-]+@virtual\\.utsc\\.edu\\.mx$"
        )

        if (!correoInstitucional.matches(correoLimpio)) {
            mostrarMensaje(
                "Utiliza tu correo institucional UTSC.",
                "error"
            )
            return
        }

        procesando = true

        auth.sendPasswordResetEmail(correoLimpio)
            .addOnCompleteListener { task ->

                procesando = false

                if (task.isSuccessful) {
                    mostrarMensaje(
                        "Si la cuenta está registrada, revisa tu correo y la carpeta de spam.",
                        "info"
                    )
                } else {
                    val codigo = (
                            task.exception?.message ?: ""
                            ).lowercase()

                    when {
                        codigo.contains("network") -> {
                            mostrarMensaje(
                                "No hay conexión. Comprueba tu internet.",
                                "error"
                            )
                        }

                        codigo.contains("too-many-requests") ||
                                codigo.contains("blocked") -> {
                            mostrarMensaje(
                                "Demasiados intentos. Espera un momento.",
                                "error"
                            )
                        }

                        else -> {
                            mostrarMensaje(
                                "No se pudo procesar la solicitud. Inténtalo más tarde.",
                                "error"
                            )
                        }
                    }
                }
            }
    }

    // =====================================================
    // PANTALLA PRINCIPAL
    // =====================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
    ) {
        // =================================================
        // FONDO ILUSTRADO
        // =================================================

        Image(
            painter = painterResource(
                id = R.drawable.banner_login
            ),
            contentDescription = "Fondo de inicio de sesión UniFind",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa ligera para suavizar el fondo.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(alpha = 0.035f)
                )
        )

        // =================================================
        // PANEL ANIMADO DESDE ABAJO
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
                                vertical = 25.dp
                            )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "¡Bienvenido a UniFind!",
                                fontSize = 25.sp,
                                lineHeight = 31.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(
                                modifier = Modifier.height(7.dp)
                            )

                            Text(
                                text = "Encuentra lo que importa",
                                fontSize = 14.sp,
                                color = Color.White.copy(
                                    alpha = 0.90f
                                ),
                                textAlign = TextAlign.Center
                            )
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
                                vertical = 25.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Inicia sesión para continuar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = texto,
                            textAlign = TextAlign.Center
                        )

                        Spacer(
                            modifier = Modifier.height(7.dp)
                        )

                        Text(
                            text = "Accede a tu comunidad universitaria",
                            fontSize = 13.sp,
                            color = gris,
                            textAlign = TextAlign.Center
                        )

                        Spacer(
                            modifier = Modifier.height(26.dp)
                        )

                        // =================================
                        // CORREO
                        // =================================

                        Text(
                            text = "Correo institucional",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = verdeOscuro
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        OutlinedTextField(
                            value = correo,
                            onValueChange = {
                                correo = it
                                mensaje = ""
                                mostrarNotificacion = false
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
                                    contentDescription = "Correo institucional",
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Correo institucional",
                                    fontSize = 14.sp,
                                    color = gris
                                )
                            },
                            textStyle = TextStyle(
                                color = texto,
                                fontSize = 15.sp
                            ),
                            shape = RoundedCornerShape(17.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = texto,
                                unfocusedTextColor = texto,
                                focusedBorderColor = verde,
                                unfocusedBorderColor = borde,
                                cursorColor = verde,
                                focusedContainerColor = verdeSuave,
                                unfocusedContainerColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            )
                        )

                        Spacer(
                            modifier = Modifier.height(19.dp)
                        )

                        // =================================
                        // CONTRASEÑA
                        // =================================

                        Text(
                            text = "Contraseña",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = verdeOscuro
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                mensaje = ""
                                mostrarNotificacion = false
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
                                    contentDescription = "Contraseña",
                                    modifier = Modifier.size(19.dp)
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Ingresa tu contraseña",
                                    fontSize = 14.sp,
                                    color = gris
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
                                focusedTextColor = texto,
                                unfocusedTextColor = texto,
                                focusedBorderColor = verde,
                                unfocusedBorderColor = borde,
                                cursorColor = verde,
                                focusedContainerColor = verdeSuave,
                                unfocusedContainerColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            )
                        )

                        // =================================
                        // RECUPERAR CONTRASEÑA
                        // =================================

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                modifier = Modifier
                                    .clickable {
                                        recuperarPassword()
                                    }
                                    .padding(
                                        horizontal = 4.dp,
                                        vertical = 9.dp
                                    ),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = verde
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        // =================================
                        // BOTÓN INICIAR SESIÓN
                        // =================================

                        Button(
                            onClick = {
                                if (procesando) {
                                    return@Button
                                }

                                val correoLimpio = correo
                                    .trim()
                                    .lowercase()

                                if (correoLimpio.isBlank()) {
                                    mostrarMensaje(
                                        "Ingresa tu correo institucional.",
                                        "error"
                                    )
                                    return@Button
                                }

                                if (
                                    !Patterns.EMAIL_ADDRESS
                                        .matcher(correoLimpio)
                                        .matches()
                                ) {
                                    mostrarMensaje(
                                        "Ingresa un correo válido.",
                                        "error"
                                    )
                                    return@Button
                                }

                                val correoInstitucional = Regex(
                                    "^[A-Za-z0-9._%+-]+@virtual\\.utsc\\.edu\\.mx$"
                                )

                                if (
                                    !correoInstitucional.matches(
                                        correoLimpio
                                    )
                                ) {
                                    mostrarMensaje(
                                        "Utiliza tu correo institucional UTSC.",
                                        "error"
                                    )
                                    return@Button
                                }

                                if (password.isBlank()) {
                                    mostrarMensaje(
                                        "Ingresa tu contraseña.",
                                        "error"
                                    )
                                    return@Button
                                }

                                procesando = true
                                mensaje = ""
                                mostrarNotificacion = false
                                loginExitoso = false

                                auth.signInWithEmailAndPassword(
                                    correoLimpio,
                                    password
                                ).addOnCompleteListener { task ->
                                    procesando = false

                                    if (task.isSuccessful) {
                                        correo = ""
                                        password = ""
                                        mostrarPassword = false

                                        mostrarMensaje(
                                            "¡Bienvenido! Has iniciado sesión correctamente.",
                                            "success"
                                        )

                                        loginExitoso = true
                                    } else {
                                        loginExitoso = false

                                        val error = (
                                                task.exception?.message ?: ""
                                                ).lowercase()

                                        when {
                                            error.contains("invalid-credential") ||
                                                    error.contains("invalid credential") ||
                                                    error.contains("wrong-password") ||
                                                    error.contains("user-not-found") ||
                                                    error.contains("password is invalid") -> {
                                                mostrarMensaje(
                                                    "Correo o contraseña incorrectos.",
                                                    "error"
                                                )
                                            }

                                            error.contains("network") -> {
                                                mostrarMensaje(
                                                    "Comprueba tu conexión a internet.",
                                                    "error"
                                                )
                                            }

                                            error.contains("too-many-requests") -> {
                                                mostrarMensaje(
                                                    "Demasiados intentos. Espera antes de volver a intentarlo.",
                                                    "error"
                                                )
                                            }

                                            error.contains("user-disabled") -> {
                                                mostrarMensaje(
                                                    "Esta cuenta está deshabilitada.",
                                                    "error"
                                                )
                                            }

                                            else -> {
                                                mostrarMensaje(
                                                    "No se pudo iniciar sesión. Inténtalo nuevamente.",
                                                    "error"
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !procesando,
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
                            if (procesando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Text(
                                    text = "Verificando...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "Iniciar sesión",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(23.dp)
                        )

                        // =================================
                        // REGISTRO
                        // =================================

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "¿No tienes cuenta? ",
                                fontSize = 13.sp,
                                color = gris
                            )

                            Text(
                                text = "Regístrate",
                                modifier = Modifier.clickable {
                                    if (!procesando) {
                                        onRegister()
                                    }
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = verde
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        // =================================
                        // TEXTO INSTITUCIONAL
                        // =================================

                        Text(
                            text = "Acceso exclusivo para estudiantes UTSC",
                            fontSize = 12.sp,
                            color = gris,
                            textAlign = TextAlign.Center
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
                    if (!procesando) {
                        onBack()
                    }
                },
                enabled = !procesando
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
        // NOTIFICACIÓN ANIMADA
        // =================================================

        AnimatedVisibility(
            visible = mostrarNotificacion,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(
                    top = 72.dp,
                    start = 18.dp,
                    end = 18.dp
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
                    containerColor = when (tipoNotificacion) {
                        "success" -> verdeClaro
                        "info" -> fondoAzul
                        else -> fondoRojo
                    }
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = when (tipoNotificacion) {
                        "success" -> verde.copy(alpha = 0.18f)
                        "info" -> azul.copy(alpha = 0.18f)
                        else -> rojo.copy(alpha = 0.15f)
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
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
                                color = when (tipoNotificacion) {
                                    "success" -> verde.copy(
                                        alpha = 0.12f
                                    )
                                    "info" -> azul.copy(
                                        alpha = 0.12f
                                    )
                                    else -> rojo.copy(
                                        alpha = 0.09f
                                    )
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (tipoNotificacion) {
                                "success" -> Icons.Default.CheckCircle
                                "info" -> Icons.Default.Info
                                else -> Icons.Default.Error
                            },
                            contentDescription = null,
                            tint = when (tipoNotificacion) {
                                "success" -> verde
                                "info" -> azul
                                else -> rojo
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
                            text = when (tipoNotificacion) {
                                "success" -> "¡Todo listo!"
                                "info" -> "Revisa tu correo"
                                else -> "Revisa este detalle"
                            },
                            color = when (tipoNotificacion) {
                                "success" -> verdeOscuro
                                "info" -> Color(0xFF244A9B)
                                else -> rojo
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