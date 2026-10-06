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
import androidx.compose.foundation.rememberScrollState
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

    // =========================================================
    // COLORES UNIFIND
    // =========================================================

    val verdeUniFind = Color(0xFF006B50)
    val verdeOscuro = Color(0xFF004D3A)
    val verdeMedio = Color(0xFF178364)

    val verdeClaro = Color(0xFFE8F7F0)
    val verdeSuave = Color(0xFFF4FBF8)

    val textoNegro = Color(0xFF202729)
    val grisTexto = Color(0xFF687277)
    val grisPlaceholder = Color(0xFF9AA3A6)
    val grisBorde = Color(0xFFD9E0DE)

    val rojoError = Color(0xFFB3261E)
    val fondoError = Color(0xFFFFEFED)

    val fondoBlanco = Color(0xFFFFFEF9)


    // =========================================================
    // FIREBASE
    // =========================================================

    val auth = remember {
        FirebaseAuth.getInstance()
    }


    // =========================================================
    // CAMPOS
    // =========================================================

    var nombre by remember {
        mutableStateOf("")
    }

    var correo by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmarPassword by remember {
        mutableStateOf("")
    }


    // =========================================================
    // ESTADOS
    // =========================================================

    var mostrarPassword by remember {
        mutableStateOf(false)
    }

    var mostrarConfirmarPassword by remember {
        mutableStateOf(false)
    }

    var registrando by remember {
        mutableStateOf(false)
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    var registroExitoso by remember {
        mutableStateOf(false)
    }


    // =========================================================
    // NOTIFICACIONES AUTOMÁTICAS
    // =========================================================
    //
    // Todas las notificaciones desaparecen solas.
    //
    // Si es un registro exitoso:
    // 1. Muestra la notificación.
    // 2. Espera 2.5 segundos.
    // 3. Quita la notificación.
    // 4. Regresa al inicio.
    //
    // Si es un error:
    // 1. Muestra el error.
    // 2. Espera 2.8 segundos.
    // 3. Lo desaparece.
    // =========================================================

    LaunchedEffect(mensaje, registroExitoso) {

        if (mensaje.isNotEmpty()) {

            if (registroExitoso) {

                delay(2500)

                mensaje = ""
                registroExitoso = false

                // =============================================
                // REGRESAR AL INICIO
                // =============================================

                onBack()

            } else {

                delay(2800)

                mensaje = ""
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
                id = R.drawable.banner_registro
            ),
            contentDescription = "Fondo de registro",
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
                    Color.White.copy(alpha = 0.10f)
                )
        )


        // =====================================================
        // CONTENIDO SCROLLABLE
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
                    bottom = 35.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            // =================================================
            // ESPACIO SUPERIOR
            // =================================================

            Spacer(
                modifier = Modifier.height(32.dp)
            )


            // =================================================
            // BOTÓN REGRESAR
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.Start
            ) {

                IconButton(
                    onClick = {

                        if (!registrando) {
                            onBack()
                        }

                    },

                    modifier = Modifier
                        .size(46.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape =
                                RoundedCornerShape(14.dp)
                        )
                        .background(
                            Color.White.copy(
                                alpha = 0.94f
                            ),
                            RoundedCornerShape(14.dp)
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,

                        contentDescription =
                            "Regresar",

                        tint =
                            Color(0xFF354047),

                        modifier =
                            Modifier.size(23.dp)
                    )
                }
            }


            // =================================================
            // ESPACIO
            // =================================================

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =================================================
            // CARD PRINCIPAL
            // =================================================

            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 18.dp,
                        shape =
                            RoundedCornerShape(30.dp),
                        clip = false
                    ),

                shape =
                    RoundedCornerShape(30.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White.copy(
                                alpha = 0.97f
                            )
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {


                    // =================================================
                    // CABECERA VERDE
                    // =================================================

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(105.dp)
                            .background(

                                brush =
                                    Brush.linearGradient(
                                        colors =
                                            listOf(
                                                verdeOscuro,
                                                verdeUniFind,
                                                verdeMedio
                                            )
                                    ),

                                shape =
                                    RoundedCornerShape(
                                        topStart = 30.dp,
                                        topEnd = 30.dp,
                                        bottomStart = 25.dp,
                                        bottomEnd = 25.dp
                                    )
                            )
                    ) {


                        // =================================================
                        // DECORACIÓN
                        // =================================================

                        Box(
                            modifier = Modifier
                                .size(105.dp)
                                .align(
                                    Alignment.TopStart
                                )
                                .background(
                                    Color.White.copy(
                                        alpha = 0.055f
                                    ),
                                    RoundedCornerShape(
                                        bottomEnd = 80.dp
                                    )
                                )
                        )


                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .align(
                                    Alignment.BottomEnd
                                )
                                .background(
                                    Color.White.copy(
                                        alpha = 0.06f
                                    ),
                                    RoundedCornerShape(
                                        topStart = 60.dp
                                    )
                                )
                        )


                        // =================================================
                        // CONTENIDO CABECERA
                        // =================================================

                        Row(

                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 22.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {


                            // =================================================
                            // ICONO
                            // =================================================

                            Box(

                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        Color.White.copy(
                                            alpha = 0.15f
                                        ),
                                        RoundedCornerShape(
                                            17.dp
                                        )
                                    ),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Image(

                                    painter =
                                        painterResource(
                                            id =
                                                R.drawable.user_solid
                                        ),

                                    contentDescription =
                                        null,

                                    modifier =
                                        Modifier.size(27.dp)
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.width(15.dp)
                            )


                            // =================================================
                            // TEXTO
                            // =================================================

                            Column {

                                Text(
                                    text =
                                        "Crear cuenta",

                                    fontSize =
                                        24.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        Color.White
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(3.dp)
                                )


                                Text(
                                    text =
                                        "Únete a la comunidad UniFind",

                                    fontSize =
                                        13.sp,

                                    color =
                                        Color.White.copy(
                                            alpha = 0.84f
                                        )
                                )
                            }
                        }
                    }


                    // =================================================
                    // CONTENIDO DEL CARD
                    // =================================================

                    Column(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 22.dp,
                                    vertical = 24.dp
                                ),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {


                        // =================================================
                        // TEXTO INTRODUCTORIO
                        // =================================================

                        Text(

                            text =
                                "Crea tu cuenta para comenzar",

                            fontSize =
                                14.sp,

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                textoNegro,

                            textAlign =
                                TextAlign.Center
                        )


                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )


                        Text(

                            text =
                                "Utiliza tu correo institucional UTSC.",

                            fontSize =
                                12.sp,

                            color =
                                grisTexto,

                            textAlign =
                                TextAlign.Center
                        )


                        Spacer(
                            modifier =
                                Modifier.height(23.dp)
                        )


                        // =================================================
                        // NOMBRE
                        // =================================================

                        OutlinedTextField(

                            value =
                                nombre,

                            onValueChange = {

                                nombre = it
                                mensaje = ""
                                registroExitoso = false
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(58.dp),

                            singleLine =
                                true,

                            leadingIcon = {

                                Image(

                                    painter =
                                        painterResource(
                                            id =
                                                R.drawable.user_solid
                                        ),

                                    contentDescription =
                                        "Nombre completo",

                                    modifier =
                                        Modifier.size(19.dp)
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
                                        "Nombre completo",

                                    fontSize =
                                        14.sp,

                                    color =
                                        grisPlaceholder
                                )
                            },

                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),

                            colors =
                                OutlinedTextFieldDefaults
                                    .colors(

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
                                        KeyboardType.Text
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )


                        // =================================================
                        // CORREO
                        // =================================================

                        OutlinedTextField(

                            value =
                                correo,

                            onValueChange = {

                                correo = it
                                mensaje = ""
                                registroExitoso = false
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(58.dp),

                            singleLine =
                                true,

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
                                        Modifier.size(19.dp)
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
                                        "Correo institucional",

                                    fontSize =
                                        14.sp,

                                    color =
                                        grisPlaceholder
                                )
                            },

                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),

                            colors =
                                OutlinedTextFieldDefaults
                                    .colors(

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
                                Modifier.height(14.dp)
                        )


                        // =================================================
                        // CONTRASEÑA
                        // =================================================

                        OutlinedTextField(

                            value =
                                password,

                            onValueChange = {

                                password = it
                                mensaje = ""
                                registroExitoso = false
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(58.dp),

                            singleLine =
                                true,

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
                                        Modifier.size(19.dp)
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
                                        "Contraseña",

                                    fontSize =
                                        14.sp,

                                    color =
                                        grisPlaceholder
                                )
                            },

                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),

                            visualTransformation =

                                if (
                                    mostrarPassword
                                ) {

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

                                            if (
                                                mostrarPassword
                                            ) {

                                                Icons.Default
                                                    .VisibilityOff

                                            } else {

                                                Icons.Default
                                                    .Visibility
                                            },

                                        contentDescription =

                                            if (
                                                mostrarPassword
                                            ) {

                                                "Ocultar contraseña"

                                            } else {

                                                "Mostrar contraseña"
                                            },

                                        tint =
                                            verdeUniFind,

                                        modifier =
                                            Modifier.size(22.dp)
                                    )
                                }
                            },

                            colors =
                                OutlinedTextFieldDefaults
                                    .colors(

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


                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )


                        // =================================================
                        // CONFIRMAR CONTRASEÑA
                        // =================================================

                        OutlinedTextField(

                            value =
                                confirmarPassword,

                            onValueChange = {

                                confirmarPassword = it
                                mensaje = ""
                                registroExitoso = false
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(58.dp),

                            singleLine =
                                true,

                            leadingIcon = {

                                Image(

                                    painter =
                                        painterResource(
                                            id =
                                                R.drawable.unlock_solid
                                        ),

                                    contentDescription =
                                        "Confirmar contraseña",

                                    modifier =
                                        Modifier.size(19.dp)
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
                                        "Confirmar contraseña",

                                    fontSize =
                                        14.sp,

                                    color =
                                        grisPlaceholder
                                )
                            },

                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),

                            visualTransformation =

                                if (
                                    mostrarConfirmarPassword
                                ) {

                                    VisualTransformation.None

                                } else {

                                    PasswordVisualTransformation()
                                },

                            trailingIcon = {

                                IconButton(

                                    onClick = {

                                        mostrarConfirmarPassword =
                                            !mostrarConfirmarPassword
                                    }
                                ) {

                                    Icon(

                                        imageVector =

                                            if (
                                                mostrarConfirmarPassword
                                            ) {

                                                Icons.Default
                                                    .VisibilityOff

                                            } else {

                                                Icons.Default
                                                    .Visibility
                                            },

                                        contentDescription =

                                            if (
                                                mostrarConfirmarPassword
                                            ) {

                                                "Ocultar contraseña"

                                            } else {

                                                "Mostrar contraseña"
                                            },

                                        tint =
                                            verdeUniFind,

                                        modifier =
                                            Modifier.size(22.dp)
                                    )
                                }
                            },

                            colors =
                                OutlinedTextFieldDefaults
                                    .colors(

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


                        Spacer(
                            modifier =
                                Modifier.height(23.dp)
                        )


                        // =================================================
                        // BOTÓN CREAR CUENTA
                        // =================================================

                        Button(

                            onClick = {

                                if (registrando) {
                                    return@Button
                                }


                                // =============================================
                                // VALIDAR NOMBRE
                                // =============================================

                                if (
                                    nombre
                                        .trim()
                                        .length < 2
                                ) {

                                    mensaje =
                                        "Ingresa tu nombre completo."

                                    registroExitoso =
                                        false

                                    return@Button
                                }


                                // =============================================
                                // NORMALIZAR CORREO
                                // =============================================

                                val correoLimpio =
                                    correo
                                        .trim()
                                        .lowercase()


                                // =============================================
                                // VALIDAR CORREO
                                // =============================================

                                val correoInstitucional =
                                    Regex(
                                        "^[A-Za-z0-9._%+-]+@virtual\\.utsc\\.edu\\.mx$"
                                    )


                                if (
                                    correoLimpio.isBlank() ||
                                    !Patterns.EMAIL_ADDRESS
                                        .matcher(
                                            correoLimpio
                                        )
                                        .matches()
                                ) {

                                    mensaje =
                                        "Correo no válido."

                                    registroExitoso =
                                        false

                                    return@Button
                                }


                                if (
                                    !correoInstitucional
                                        .matches(
                                            correoLimpio
                                        )
                                ) {

                                    mensaje =
                                        "Usa tu correo institucional."

                                    registroExitoso =
                                        false

                                    return@Button
                                }


                                // =============================================
                                // VALIDAR CONTRASEÑA
                                // =============================================

                                val tieneMayuscula =
                                    password.any {
                                        it.isUpperCase()
                                    }


                                if (
                                    password.length < 8
                                ) {

                                    mensaje =
                                        "La contraseña debe tener 8 caracteres."

                                    registroExitoso =
                                        false

                                    return@Button
                                }


                                if (
                                    !tieneMayuscula
                                ) {

                                    mensaje =
                                        "Agrega al menos una mayúscula."

                                    registroExitoso =
                                        false

                                    return@Button
                                }


                                // =============================================
                                // CONFIRMAR CONTRASEÑA
                                // =============================================

                                if (
                                    password !=
                                    confirmarPassword
                                ) {

                                    mensaje =
                                        "Las contraseñas no coinciden."

                                    registroExitoso =
                                        false

                                    return@Button
                                }


                                // =============================================
                                // INICIAR REGISTRO
                                // =============================================

                                registrando =
                                    true

                                mensaje =
                                    ""

                                registroExitoso =
                                    false


                                // =============================================
                                // FIREBASE
                                // =============================================

                                auth
                                    .createUserWithEmailAndPassword(
                                        correoLimpio,
                                        password
                                    )
                                    .addOnCompleteListener { task ->


                                        // =====================================
                                        // REGISTRO EXITOSO
                                        // =====================================

                                        if (
                                            task.isSuccessful
                                        ) {

                                            val usuario =
                                                auth.currentUser


                                            // =================================
                                            // ACTUALIZAR NOMBRE
                                            // =================================

                                            if (
                                                usuario != null
                                            ) {

                                                val perfil =
                                                    userProfileChangeRequest {

                                                        displayName =
                                                            nombre
                                                                .trim()
                                                    }


                                                usuario
                                                    .updateProfile(
                                                        perfil
                                                    )
                                                    .addOnCompleteListener {

                                                        // =================================
                                                        // LIMPIAR TODOS LOS CAMPOS
                                                        // =================================

                                                        nombre =
                                                            ""

                                                        correo =
                                                            ""

                                                        password =
                                                            ""

                                                        confirmarPassword =
                                                            ""

                                                        mostrarPassword =
                                                            false

                                                        mostrarConfirmarPassword =
                                                            false


                                                        // =================================
                                                        // FINALIZAR REGISTRO
                                                        // =================================

                                                        registrando =
                                                            false

                                                        mensaje =
                                                            "Registro exitoso"

                                                        registroExitoso =
                                                            true
                                                    }

                                            } else {

                                                // =================================
                                                // SI NO SE OBTUVO USUARIO,
                                                // PERO FIREBASE CREÓ LA CUENTA
                                                // =================================

                                                nombre =
                                                    ""

                                                correo =
                                                    ""

                                                password =
                                                    ""

                                                confirmarPassword =
                                                    ""

                                                mostrarPassword =
                                                    false

                                                mostrarConfirmarPassword =
                                                    false

                                                registrando =
                                                    false

                                                mensaje =
                                                    "Registro exitoso"

                                                registroExitoso =
                                                    true
                                            }


                                        } else {

                                            // =====================================
                                            // ERROR
                                            // =====================================

                                            registrando =
                                                false

                                            val error =
                                                task.exception


                                            mensaje =
                                                when {

                                                    error
                                                        ?.message
                                                        ?.contains(
                                                            "already in use",
                                                            ignoreCase = true
                                                        ) == true -> {

                                                        "Este correo ya está registrado."
                                                    }


                                                    error
                                                        ?.message
                                                        ?.contains(
                                                            "email address is already",
                                                            ignoreCase = true
                                                        ) == true -> {

                                                        "Este correo ya está registrado."
                                                    }


                                                    error
                                                        ?.message
                                                        ?.contains(
                                                            "badly formatted",
                                                            ignoreCase = true
                                                        ) == true -> {

                                                        "Correo no válido."
                                                    }


                                                    error
                                                        ?.message
                                                        ?.contains(
                                                            "network",
                                                            ignoreCase = true
                                                        ) == true -> {

                                                        "Revisa tu conexión."
                                                    }


                                                    else -> {

                                                        "No se pudo crear la cuenta."
                                                    }
                                                }

                                            registroExitoso =
                                                false
                                        }
                                    }
                            },

                            enabled =
                                !registrando,

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),

                            shape =
                                RoundedCornerShape(
                                    17.dp
                                ),

                            colors =
                                ButtonDefaults
                                    .buttonColors(

                                        containerColor =
                                            verdeUniFind,

                                        disabledContainerColor =
                                            verdeUniFind.copy(
                                                alpha = 0.55f
                                            )
                                    )
                        ) {


                            if (
                                registrando
                            ) {

                                CircularProgressIndicator(

                                    modifier =
                                        Modifier.size(22.dp),

                                    color =
                                        Color.White,

                                    strokeWidth =
                                        2.5.dp
                                )


                                Spacer(
                                    modifier =
                                        Modifier.width(10.dp)
                                )


                                Text(

                                    text =
                                        "Creando cuenta...",

                                    fontSize =
                                        15.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        Color.White
                                )

                            } else {

                                Text(

                                    text =
                                        "Crear mi cuenta",

                                    fontSize =
                                        16.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        Color.White
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.height(19.dp)
                        )


                        // =================================================
                        // SEPARADOR
                        // =================================================

                        Row(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 15.dp
                                    ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(
                                            grisBorde
                                        )
                            )


                            Text(

                                text =
                                    "  UniFind  ",

                                fontSize =
                                    11.sp,

                                fontWeight =
                                    FontWeight.SemiBold,

                                color =
                                    grisPlaceholder
                            )


                            Box(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(
                                            grisBorde
                                        )
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(17.dp)
                        )


                        // =================================================
                        // TÉRMINOS
                        // =================================================

                        Text(

                            text =
                                "Al registrarte aceptas nuestros",

                            fontSize =
                                12.sp,

                            color =
                                grisTexto,

                            textAlign =
                                TextAlign.Center
                        )


                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )


                        Text(

                            text =
                                "Términos y Condiciones",

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                verdeUniFind,

                            textAlign =
                                TextAlign.Center,

                            modifier =
                                Modifier.clickable {

                                    // Aquí posteriormente
                                    // agregaremos la pantalla
                                    // de términos.
                                }
                        )
                    }
                }
            }


            // =================================================
            // ESPACIO FINAL
            // =================================================

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )
        }


        // =====================================================
        // NOTIFICACIÓN
        // =====================================================

        AnimatedVisibility(

            visible =
                mensaje.isNotEmpty(),

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
                    Modifier
                        .width(245.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape =
                                RoundedCornerShape(
                                    18.dp
                                )
                        ),

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

                colors =
                    CardDefaults.cardColors(

                        containerColor =

                            if (
                                registroExitoso
                            ) {

                                verdeClaro

                            } else {

                                fondoError
                            }
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 0.dp
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


                    // =============================================
                    // ICONO
                    // =============================================

                    Box(

                        modifier =
                            Modifier
                                .size(38.dp)
                                .background(

                                    if (
                                        registroExitoso
                                    ) {

                                        verdeUniFind.copy(
                                            alpha = 0.13f
                                        )

                                    } else {

                                        rojoError.copy(
                                            alpha = 0.10f
                                        )
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

                                if (
                                    registroExitoso
                                ) {

                                    Icons.Default
                                        .CheckCircle

                                } else {

                                    Icons.Default
                                        .Error
                                },

                            contentDescription =
                                null,

                            tint =

                                if (
                                    registroExitoso
                                ) {

                                    verdeUniFind

                                } else {

                                    rojoError
                                },

                            modifier =
                                Modifier.size(22.dp)
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )


                    // =============================================
                    // TEXTO
                    // =============================================

                    Text(

                        text =

                            if (
                                registroExitoso
                            ) {

                                "¡Registro exitoso!"

                            } else {

                                mensaje
                            },

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =

                            if (
                                registroExitoso
                            ) {

                                verdeOscuro

                            } else {

                                rojoError
                            },

                        maxLines =
                            2
                    )
                }
            }
        }
    }
}