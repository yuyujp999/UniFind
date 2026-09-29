package com.unifind.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@Composable
fun RegistroScreen(
    onBack: () -> Unit
) {

    // =========================================
    // COLORES UNIFIND
    // =========================================

    val verdeUniFind = Color(0xFF006B50)
    val verdeOscuro = Color(0xFF00543F)
    val textoNegro = Color(0xFF222222)
    val grisTexto = Color(0xFF687277)
    val grisPlaceholder = Color(0xFF9AA1A4)
    val grisBorde = Color(0xFFD8DDDF)
    val fondoBlanco = Color(0xFFFFFEF9)


    // =========================================
    // VARIABLES
    // =========================================

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarConfirmarPassword by remember { mutableStateOf(false) }


    // =========================================
    // PANTALLA
    // =========================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoBlanco)
    ) {

        // =========================================
        // FONDO
        // =========================================

        Image(
            painter = painterResource(id = R.drawable.pantalla_dos),
            contentDescription = "Fondo de registro",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        // =========================================
        // CONTENIDO
        // =========================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 26.dp,
                    end = 26.dp,
                    bottom = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // =========================================
            // FLECHA REGRESAR
            // =========================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {

                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color(0xFF39454A),
                        modifier = Modifier.size(27.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.height(14.dp))


            // =========================================
            // TÍTULO
            // =========================================

            Text(
                text = "Crear cuenta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = verdeUniFind,
                textAlign = TextAlign.Center
            )


            Spacer(modifier = Modifier.height(6.dp))


            // =========================================
            // SUBTÍTULO
            // =========================================

            Text(
                text = "Únete a UniFind",
                fontSize = 16.sp,
                color = grisTexto,
                textAlign = TextAlign.Center
            )


            Spacer(modifier = Modifier.height(32.dp))


            // =========================================
            // NOMBRE COMPLETO
            // =========================================

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,

                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.user_solid),
                        contentDescription = "Nombre completo",
                        modifier = Modifier.size(21.dp)
                    )
                },

                textStyle = TextStyle(
                    color = textoNegro,
                    fontSize = 15.sp
                ),

                placeholder = {
                    Text(
                        text = "Nombre completo",
                        fontSize = 15.sp,
                        color = grisPlaceholder
                    )
                },

                shape = RoundedCornerShape(14.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textoNegro,
                    unfocusedTextColor = textoNegro,
                    focusedBorderColor = verdeUniFind,
                    unfocusedBorderColor = grisBorde,
                    cursorColor = verdeUniFind,
                    focusedContainerColor = Color.White.copy(alpha = 0.94f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.94f)
                )
            )


            Spacer(modifier = Modifier.height(18.dp))


            // =========================================
            // CORREO INSTITUCIONAL
            // =========================================

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,

                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.envelope_solid),
                        contentDescription = "Correo institucional",
                        modifier = Modifier.size(21.dp)
                    )
                },

                textStyle = TextStyle(
                    color = textoNegro,
                    fontSize = 15.sp
                ),

                placeholder = {
                    Text(
                        text = "Correo institucional",
                        fontSize = 15.sp,
                        color = grisPlaceholder
                    )
                },

                shape = RoundedCornerShape(14.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textoNegro,
                    unfocusedTextColor = textoNegro,
                    focusedBorderColor = verdeUniFind,
                    unfocusedBorderColor = grisBorde,
                    cursorColor = verdeUniFind,
                    focusedContainerColor = Color.White.copy(alpha = 0.94f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.94f)
                ),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )


            Spacer(modifier = Modifier.height(18.dp))


            // =========================================
            // CONTRASEÑA
            // =========================================

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,

                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.unlock_solid),
                        contentDescription = "Contraseña",
                        modifier = Modifier.size(21.dp)
                    )
                },

                textStyle = TextStyle(
                    color = textoNegro,
                    fontSize = 15.sp
                ),

                placeholder = {
                    Text(
                        text = "Contraseña",
                        fontSize = 15.sp,
                        color = grisPlaceholder
                    )
                },

                shape = RoundedCornerShape(14.dp),

                visualTransformation = if (mostrarPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

                trailingIcon = {
                    IconButton(
                        onClick = { mostrarPassword = !mostrarPassword }
                    ) {
                        Icon(
                            imageVector = if (mostrarPassword) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (mostrarPassword) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                            tint = verdeUniFind,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textoNegro,
                    unfocusedTextColor = textoNegro,
                    focusedBorderColor = verdeUniFind,
                    unfocusedBorderColor = grisBorde,
                    cursorColor = verdeUniFind,
                    focusedContainerColor = Color.White.copy(alpha = 0.94f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.94f)
                ),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )


            Spacer(modifier = Modifier.height(18.dp))


            // =========================================
            // CONFIRMAR CONTRASEÑA
            // =========================================

            OutlinedTextField(
                value = confirmarPassword,
                onValueChange = { confirmarPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                singleLine = true,

                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.unlock_solid),
                        contentDescription = "Confirmar contraseña",
                        modifier = Modifier.size(21.dp)
                    )
                },

                textStyle = TextStyle(
                    color = textoNegro,
                    fontSize = 15.sp
                ),

                placeholder = {
                    Text(
                        text = "Confirmar contraseña",
                        fontSize = 15.sp,
                        color = grisPlaceholder
                    )
                },

                shape = RoundedCornerShape(14.dp),

                visualTransformation = if (mostrarConfirmarPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

                trailingIcon = {
                    IconButton(
                        onClick = {
                            mostrarConfirmarPassword = !mostrarConfirmarPassword
                        }
                    ) {
                        Icon(
                            imageVector = if (mostrarConfirmarPassword) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (mostrarConfirmarPassword) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                            tint = verdeUniFind,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textoNegro,
                    unfocusedTextColor = textoNegro,
                    focusedBorderColor = verdeUniFind,
                    unfocusedBorderColor = grisBorde,
                    cursorColor = verdeUniFind,
                    focusedContainerColor = Color.White.copy(alpha = 0.94f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.94f)
                ),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )


            Spacer(modifier = Modifier.height(32.dp))


            // =========================================
            // BOTÓN REGISTRARSE
            // =========================================

            Button(
                onClick = {
                    // Aquí después conectaremos el registro
                    // con la base de datos.
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = verdeUniFind
                )
            ) {
                Text(
                    text = "Registrarse",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }


            Spacer(modifier = Modifier.height(20.dp))


            // =========================================
            // TÉRMINOS
            // =========================================

            Text(
                text = "Al registrarte aceptas nuestros",
                fontSize = 13.sp,
                color = grisTexto,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Términos y Condiciones",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = verdeUniFind,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}