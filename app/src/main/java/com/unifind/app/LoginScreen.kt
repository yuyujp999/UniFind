package com.unifind.app

import android.util.Patterns

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width

// CORREGIDO: verticalScroll y rememberScrollState viven en androidx.compose.foundation
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.text.KeyboardOptions

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

    // =========================================================
    // COLORES UNIFIND
    // =========================================================

    val verdeUniFind = Color(0xFF006B50)
    val verdeOscuro = Color(0xFF00543F)

    val verdeClaro = Color(0xFFE5F5EE)
    val verdeSuave = Color(0xFFF1FAF6)
    val verdeNotificacion = Color(0xFF2E8B68)

    val textoNegro = Color(0xFF222222)
    val grisTexto = Color(0xFF687277)
    val grisPlaceholder = Color(0xFF9AA1A4)
    val grisBorde = Color(0xFFD8DDDF)

    val rojoError = Color(0xFFB3261E)
    val fondoError = Color(0xFFFFEDEA)

    val azulInfo = Color(0xFF356AE6)
    val fondoInfo = Color(0xFFEAF0FF)

    val fondoBlanco = Color(0xFFFFFEF9)


    // =========================================================
    // FIREBASE
    // =========================================================

    val auth = remember {
        FirebaseAuth.getInstance()
    }


    // =========================================================
    // VARIABLES
    // =========================================================

    var correo by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    var mostrarPassword by remember { mutableStateOf(false) }

    var procesando by remember { mutableStateOf(false) }

    var mensaje by remember { mutableStateOf("") }

    var tipoNotificacion by remember { mutableStateOf("error") }

    var mostrarNotificacion by remember { mutableStateOf(false) }

    // Contador para que la notificación se reinicie aunque el mensaje sea el mismo
    var notificacionId by remember { mutableStateOf(0) }

    var loginExitoso by remember { mutableStateOf(false) }


    // =========================================================
    // OCULTAR NOTIFICACIÓN AUTOMÁTICAMENTE
    // =========================================================

    LaunchedEffect(notificacionId) {

        if (mostrarNotificacion && mensaje.isNotEmpty()) {

            delay(2800)

            mostrarNotificacion = false
        }
    }


    // =========================================================
    // IR AL INICIO DESPUÉS DEL LOGIN
    // =========================================================

    LaunchedEffect(loginExitoso) {

        if (loginExitoso) {

            delay(1800)

            onLogin()
        }
    }


    // =========================================================
    // FUNCIÓN PARA MOSTRAR NOTIFICACIÓN
    // =========================================================

    fun mostrarMensaje(
        texto: String,
        tipo: String
    ) {

        mensaje = texto
        tipoNotificacion = tipo
        mostrarNotificacion = true
        notificacionId++
    }


    // =========================================================
    // FUNCIÓN: RECUPERAR CONTRASEÑA
    // =========================================================

    fun recuperarPassword() {

        if (procesando) {
            return
        }

        val correoLimpio = correo.trim().lowercase()

        if (correoLimpio.isBlank()) {
            mostrarMensaje("Ingresa tu correo institucional.", "error")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            mostrarMensaje("Ingresa un correo válido.", "error")
            return
        }

        val correoInstitucional =
            Regex("^[A-Za-z0-9._%+-]+@virtual\\.utsc\\.edu\\.mx$")

        if (!correoInstitucional.matches(correoLimpio)) {
            mostrarMensaje("Usa tu correo institucional UTSC.", "error")
            return
        }

        procesando = true

        auth
            .sendPasswordResetEmail(correoLimpio)
            .addOnCompleteListener { task ->

                procesando = false

                if (task.isSuccessful) {

                    mostrarMensaje(
                        "Revisa tu correo (también spam) para restablecer tu contraseña.",
                        "info"
                    )

                } else {

                    val detalle = task.exception?.message ?: ""

                    when {

                        detalle.contains("network", ignoreCase = true) -> {
                            mostrarMensaje("Revisa tu conexión a internet.", "error")
                        }

                        detalle.contains("too-many-requests", ignoreCase = true) ||
                                detalle.contains("blocked", ignoreCase = true) -> {
                            mostrarMensaje("Demasiados intentos. Intenta más tarde.", "error")
                        }

                        detalle.contains("no user record", ignoreCase = true) ||
                                detalle.contains("user-not-found", ignoreCase = true) -> {
                            mostrarMensaje("Ese correo no está registrado.", "error")
                        }

                        else -> {
                            mostrarMensaje(
                                "No se pudo enviar el correo. Intenta de nuevo.",
                                "error"
                            )
                        }
                    }
                }
            }
    }


    // =========================================================
    // PANTALLA PRINCIPAL
    // =========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoBlanco)
    ) {


        // =====================================================
        // FONDO
        // =====================================================

        Image(
            painter = painterResource(
                id = R.drawable.banner_login
            ),
            contentDescription = "Fondo de inicio de sesión",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        // =====================================================
        // CAPA SUAVE
        // =====================================================

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(
                        alpha = 0.10f
                    )
                )
        )


        // =====================================================
        // CONTENIDO
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(
                    rememberScrollState()
                )
                .imePadding()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 30.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // =================================================
            // ESPACIO SUPERIOR
            // =================================================

            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // BOTÓN REGRESAR
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {

                IconButton(
                    onClick = {
                        if (!procesando) {
                            onBack()
                        }
                    },

                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.White.copy(
                                alpha = 0.88f
                            ),
                            RoundedCornerShape(14.dp)
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,

                        contentDescription = "Regresar",

                        tint = Color(0xFF39454A),

                        modifier = Modifier.size(25.dp)
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =================================================
            // CARD LOGIN
            // =================================================

            Card(
                modifier = Modifier
                    .fillMaxWidth(),

                shape = RoundedCornerShape(
                    28.dp
                ),

                colors = CardDefaults.cardColors(
                    containerColor =
                        Color.White.copy(
                            alpha = 0.96f
                        )
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                )
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 22.dp,
                            vertical = 28.dp
                        ),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {


                    // =========================================
                    // TÍTULO
                    // =========================================

                    Text(
                        text = "¡Bienvenido a\nUniFind!",

                        fontSize = 29.sp,

                        lineHeight = 33.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            verdeUniFind,

                        textAlign =
                            TextAlign.Center
                    )


                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )


                    // =========================================
                    // SUBTÍTULO
                    // =========================================

                    Text(
                        text =
                            "Inicia sesión para continuar",

                        fontSize = 15.sp,

                        color =
                            grisTexto,

                        textAlign =
                            TextAlign.Center
                    )


                    Spacer(
                        modifier = Modifier.height(28.dp)
                    )


                    // =========================================
                    // CORREO
                    // =========================================

                    Text(
                        text =
                            "Correo institucional",

                        modifier =
                            Modifier.fillMaxWidth(),

                        fontSize = 14.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            verdeOscuro
                    )


                    Spacer(
                        modifier = Modifier.height(7.dp)
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
                            .height(58.dp),

                        singleLine = true,

                        leadingIcon = {

                            Image(

                                painter =
                                    painterResource(
                                        id =
                                            R.drawable.envelope_solid
                                    ),

                                contentDescription =
                                    "Correo institucional",

                                modifier =
                                    Modifier.size(20.dp)
                            )
                        },

                        textStyle = TextStyle(
                            color =
                                textoNegro,

                            fontSize =
                                15.sp
                        ),

                        placeholder = {

                            Text(

                                text =
                                    "Correo institucional",

                                fontSize =
                                    15.sp,

                                color =
                                    grisPlaceholder
                            )
                        },

                        shape =
                            RoundedCornerShape(
                                15.dp
                            ),

                        colors =
                            OutlinedTextFieldDefaults.colors(

                                focusedTextColor =
                                    textoNegro,

                                unfocusedTextColor =
                                    textoNegro,

                                focusedBorderColor =
                                    verdeUniFind,

                                unfocusedBorderColor =
                                    grisBorde,

                                cursorColor =
                                    verdeUniFind,

                                focusedContainerColor =
                                    verdeSuave,

                                unfocusedContainerColor =
                                    Color.White
                            ),

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Email
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.height(17.dp)
                    )


                    // =========================================
                    // CONTRASEÑA
                    // =========================================

                    Text(
                        text =
                            "Contraseña",

                        modifier =
                            Modifier.fillMaxWidth(),

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            verdeOscuro
                    )


                    Spacer(
                        modifier =
                            Modifier.height(7.dp)
                    )


                    OutlinedTextField(

                        value =
                            password,

                        onValueChange = {

                            password = it

                            mensaje = ""

                            mostrarNotificacion =
                                false
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),

                        singleLine = true,

                        leadingIcon = {

                            Image(

                                painter =
                                    painterResource(
                                        id =
                                            R.drawable.unlock_solid
                                    ),

                                contentDescription =
                                    "Contraseña",

                                modifier =
                                    Modifier.size(20.dp)
                            )
                        },

                        textStyle =
                            TextStyle(

                                color =
                                    textoNegro,

                                fontSize =
                                    15.sp
                            ),

                        placeholder = {

                            Text(

                                text =
                                    "Ingresa tu contraseña",

                                fontSize =
                                    15.sp,

                                color =
                                    grisPlaceholder
                            )
                        },

                        shape =
                            RoundedCornerShape(
                                15.dp
                            ),

                        visualTransformation =
                            if (mostrarPassword) {

                                VisualTransformation.None

                            } else {

                                PasswordVisualTransformation()
                            },


                        // =====================================
                        // MOSTRAR CONTRASEÑA
                        // =====================================

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

                                    tint =
                                        verdeUniFind,

                                    modifier =
                                        Modifier.size(23.dp)
                                )
                            }
                        },


                        colors =
                            OutlinedTextFieldDefaults.colors(

                                focusedTextColor =
                                    textoNegro,

                                unfocusedTextColor =
                                    textoNegro,

                                focusedBorderColor =
                                    verdeUniFind,

                                unfocusedBorderColor =
                                    grisBorde,

                                cursorColor =
                                    verdeUniFind,

                                focusedContainerColor =
                                    verdeSuave,

                                unfocusedContainerColor =
                                    Color.White
                            ),

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Password
                            )
                    )


                    // =========================================
                    // OLVIDASTE CONTRASEÑA
                    // =========================================

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 4.dp
                                ),

                        horizontalArrangement =
                            Arrangement.End
                    ) {

                        Text(

                            text =
                                "¿Olvidaste tu contraseña?",

                            modifier =
                                Modifier
                                    .clickable {
                                        recuperarPassword()
                                    }
                                    .padding(
                                        vertical = 8.dp,
                                        horizontal = 4.dp
                                    ),

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                verdeUniFind
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )


                    // =========================================
                    // BOTÓN INICIAR SESIÓN
                    // =========================================

                    Button(

                        onClick = {

                            if (procesando) {
                                return@Button
                            }


                            // =====================================
                            // NORMALIZAR CORREO
                            // =====================================

                            val correoLimpio =
                                correo
                                    .trim()
                                    .lowercase()


                            // =====================================
                            // VALIDAR CORREO VACÍO
                            // =====================================

                            if (
                                correoLimpio.isBlank()
                            ) {

                                mostrarMensaje(
                                    "Ingresa tu correo institucional.",
                                    "error"
                                )

                                return@Button
                            }


                            // =====================================
                            // VALIDAR FORMATO
                            // =====================================

                            if (
                                !Patterns
                                    .EMAIL_ADDRESS
                                    .matcher(
                                        correoLimpio
                                    )
                                    .matches()
                            ) {

                                mostrarMensaje(
                                    "Correo no válido.",
                                    "error"
                                )

                                return@Button
                            }


                            // =====================================
                            // VALIDAR UTSC
                            // =====================================

                            val correoInstitucional =
                                Regex(
                                    "^[A-Za-z0-9._%+-]+@virtual\\.utsc\\.edu\\.mx$"
                                )


                            if (
                                !correoInstitucional
                                    .matches(
                                        correoLimpio
                                    )
                            ) {

                                mostrarMensaje(
                                    "Usa tu correo institucional UTSC.",
                                    "error"
                                )

                                return@Button
                            }


                            // =====================================
                            // VALIDAR CONTRASEÑA
                            // =====================================

                            if (
                                password.isBlank()
                            ) {

                                mostrarMensaje(
                                    "Ingresa tu contraseña.",
                                    "error"
                                )

                                return@Button
                            }


                            // =====================================
                            // INICIAR SESIÓN
                            // =====================================

                            procesando = true

                            mensaje = ""

                            mostrarNotificacion = false

                            loginExitoso = false


                            auth
                                .signInWithEmailAndPassword(
                                    correoLimpio,
                                    password
                                )
                                .addOnCompleteListener { task ->

                                    procesando = false


                                    if (task.isSuccessful) {

                                        // =================================
                                        // LIMPIAR CAMPOS
                                        // =================================

                                        correo = ""

                                        password = ""

                                        mostrarPassword = false


                                        // =================================
                                        // NOTIFICACIÓN EXITOSA
                                        // =================================

                                        mostrarMensaje(
                                            "Inicio de sesión exitoso",
                                            "success"
                                        )

                                        loginExitoso =
                                            true

                                    } else {

                                        loginExitoso =
                                            false


                                        val error =
                                            task.exception


                                        val mensajeError =
                                            error?.message
                                                ?: ""


                                        // =============================
                                        // ERROR CORREO / CONTRASEÑA
                                        // =============================

                                        when {

                                            mensajeError.contains(
                                                "password is invalid",
                                                ignoreCase = true
                                            ) -> {

                                                mostrarMensaje(
                                                    "Correo o contraseña incorrectos.",
                                                    "error"
                                                )
                                            }


                                            mensajeError.contains(
                                                "invalid-credential",
                                                ignoreCase = true
                                            ) -> {

                                                mostrarMensaje(
                                                    "Correo o contraseña incorrectos.",
                                                    "error"
                                                )
                                            }


                                            mensajeError.contains(
                                                "user-not-found",
                                                ignoreCase = true
                                            ) -> {

                                                mostrarMensaje(
                                                    "Correo o contraseña incorrectos.",
                                                    "error"
                                                )
                                            }


                                            mensajeError.contains(
                                                "wrong-password",
                                                ignoreCase = true
                                            ) -> {

                                                mostrarMensaje(
                                                    "Correo o contraseña incorrectos.",
                                                    "error"
                                                )
                                            }


                                            mensajeError.contains(
                                                "network",
                                                ignoreCase = true
                                            ) -> {

                                                mostrarMensaje(
                                                    "Revisa tu conexión a internet.",
                                                    "error"
                                                )
                                            }


                                            mensajeError.contains(
                                                "too-many-requests",
                                                ignoreCase = true
                                            ) -> {

                                                mostrarMensaje(
                                                    "Demasiados intentos. Intenta más tarde.",
                                                    "error"
                                                )
                                            }


                                            else -> {

                                                mostrarMensaje(
                                                    "No se pudo iniciar sesión.",
                                                    "error"
                                                )
                                            }
                                        }
                                    }
                                }
                        },


                        enabled =
                            !procesando,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),

                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(

                                containerColor =
                                    verdeUniFind,

                                disabledContainerColor =
                                    verdeUniFind.copy(
                                        alpha = 0.60f
                                    )
                            )
                    ) {

                        if (procesando) {

                            CircularProgressIndicator(

                                modifier =
                                    Modifier.size(
                                        23.dp
                                    ),

                                color =
                                    Color.White,

                                strokeWidth =
                                    2.5.dp
                            )


                            Spacer(
                                modifier =
                                    Modifier.width(
                                        10.dp
                                    )
                            )


                            Text(

                                text =
                                    "Procesando...",

                                fontSize =
                                    16.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )

                        } else {

                            Text(

                                text =
                                    "Iniciar sesión",

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(22.dp)
                    )


                    // =========================================
                    // REGISTRO
                    // =========================================

                    Row(

                        horizontalArrangement =
                            Arrangement.Center,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(

                            text =
                                "¿No tienes cuenta? ",

                            fontSize =
                                13.sp,

                            color =
                                grisTexto
                        )


                        Text(

                            text =
                                "Regístrate",

                            modifier =
                                Modifier.clickable {

                                    if (!procesando) {
                                        onRegister()
                                    }
                                },

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                verdeUniFind
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )


                    // =========================================
                    // TEXTO INSTITUCIONAL
                    // =========================================

                    Text(

                        text =
                            "Acceso exclusivo para estudiantes UTSC",

                        fontSize =
                            11.sp,

                        color =
                            grisTexto.copy(
                                alpha = 0.75f
                            ),

                        textAlign =
                            TextAlign.Center
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(25.dp)
            )
        }


        // =====================================================
        // NOTIFICACIÓN SUPERIOR DERECHA
        // =====================================================

        AnimatedVisibility(

            visible =
                mostrarNotificacion,

            modifier =
                Modifier
                    .align(
                        Alignment.TopEnd
                    )
                    .statusBarsPadding()
                    .padding(
                        top = 10.dp,
                        end = 12.dp
                    ),

            enter =
                slideInHorizontally(
                    initialOffsetX = {
                        it
                    }
                ) + fadeIn(),

            exit =
                slideOutHorizontally(
                    targetOffsetX = {
                        it
                    }
                ) + fadeOut()
        ) {


            Card(

                modifier =
                    Modifier.width(
                        245.dp
                    ),

                shape =
                    RoundedCornerShape(
                        17.dp
                    ),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            when (tipoNotificacion) {

                                "success" -> verdeClaro

                                "info" -> fondoInfo

                                else -> fondoError
                            }
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation =
                            8.dp
                    )
            ) {


                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 13.dp,
                                vertical = 12.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    // =========================================
                    // ICONO
                    // =========================================

                    Box(

                        modifier =
                            Modifier
                                .size(38.dp)
                                .background(

                                    when (tipoNotificacion) {

                                        "success" ->
                                            verdeNotificacion.copy(alpha = 0.14f)

                                        "info" ->
                                            azulInfo.copy(alpha = 0.12f)

                                        else ->
                                            rojoError.copy(alpha = 0.12f)
                                    },

                                    RoundedCornerShape(
                                        12.dp
                                    )
                                ),

                        contentAlignment =
                            Alignment.Center
                    ) {


                        Icon(

                            imageVector =

                                when (tipoNotificacion) {

                                    "success" -> Icons.Default.CheckCircle

                                    "info" -> Icons.Default.Info

                                    else -> Icons.Default.Error
                                },

                            contentDescription =
                                null,

                            tint =

                                when (tipoNotificacion) {

                                    "success" -> verdeNotificacion

                                    "info" -> azulInfo

                                    else -> rojoError
                                },

                            modifier =
                                Modifier.size(
                                    23.dp
                                )
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(
                                10.dp
                            )
                    )


                    // =========================================
                    // MENSAJE
                    // =========================================

                    Text(

                        text =
                            mensaje,

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =

                            when (tipoNotificacion) {

                                "success" -> verdeOscuro

                                "info" -> Color(0xFF244A9B)

                                else -> rojoError
                            },

                        maxLines =
                            3
                    )
                }
            }
        }
    }
}