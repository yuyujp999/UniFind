package com.unifind.app

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
fun LoginScreen(
    onBack: () -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {

    // =========================================
    // COLORES UNIFIND
    // =========================================

    val verdeUniFind = Color(0xFF006B50)
    val verdeOscuro = Color(0xFF00543F)
    val textoNegro = Color(0xFF222222)
    val textoGris = Color(0xFF687277)
    val bordeGris = Color(0xFFD8DDDF)
    val fondoBlanco = Color(0xFFFFFEF9)


    // =========================================
    // VARIABLES
    // =========================================

    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }


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
            contentDescription = "Fondo de inicio de sesión",
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
                .padding(
                    start = 26.dp,
                    end = 26.dp,
                    bottom = 20.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // =========================================
            // REGRESAR
            // =========================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {

                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(45.dp)
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color(0xFF39454A),
                        modifier = Modifier.size(27.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(13.dp))


            // =========================================
            // TÍTULO
            // =========================================

            Text(
                text = "¡Bienvenido a\nUniFind!",
                fontSize = 28.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                color = verdeUniFind,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(7.dp))


            // =========================================
            // SUBTÍTULO
            // =========================================

            Text(
                text = "Inicia sesión para continuar",
                fontSize = 16.sp,
                color = textoGris,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))


            // =========================================
            // CORREO
            // =========================================

            Text(
                text = "Correo electrónico",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = verdeOscuro
            )

            Spacer(modifier = Modifier.height(7.dp))

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
                        contentDescription = "Correo",
                        modifier = Modifier.size(21.dp)
                    )
                },

                textStyle = TextStyle(
                    color = textoNegro,
                    fontSize = 15.sp
                ),

                placeholder = {
                    Text(
                        text = "Ingresa tu correo personal o institucional",
                        fontSize = 15.sp,
                        color = Color(0xFF9AA1A4)
                    )
                },

                shape = RoundedCornerShape(14.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textoNegro,
                    unfocusedTextColor = textoNegro,
                    focusedBorderColor = verdeUniFind,
                    unfocusedBorderColor = bordeGris,
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

            Text(
                text = "Contraseña",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = verdeOscuro
            )

            Spacer(modifier = Modifier.height(7.dp))

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
                        text = "Ingresa tu contraseña",
                        fontSize = 15.sp,
                        color = Color(0xFF9AA1A4)
                    )
                },

                shape = RoundedCornerShape(14.dp),

                visualTransformation = if (mostrarPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

                // =========================================
                // MOSTRAR / OCULTAR CONTRASEÑA
                // =========================================

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
                    unfocusedBorderColor = bordeGris,
                    cursorColor = verdeUniFind,
                    focusedContainerColor = Color.White.copy(alpha = 0.94f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.94f)
                ),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )


            // =========================================
            // OLVIDASTE CONTRASEÑA
            // =========================================

            Text(
                text = "¿Olvidaste tu contraseña?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 10.dp,
                        end = 2.dp
                    ),
                textAlign = TextAlign.End,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = verdeUniFind
            )

            Spacer(modifier = Modifier.height(18.dp))


            // =========================================
            // INICIAR SESIÓN
            // =========================================

            Button(
                onClick = {
                    // POR AHORA NO VALIDAMOS CORREO NI CONTRASEÑA.
                    // Simplemente pasamos a la pantalla principal.
                    onLogin()
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
                    text = "Iniciar sesión",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))


            // =========================================
            // SEPARADOR
            // =========================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFD9DEDF))
                )

                Text(
                    text = "  o continúa con  ",
                    fontSize = 13.sp,
                    color = Color(0xFF7C8589)
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFD9DEDF))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            // =========================================
            // GOOGLE
            // =========================================

            OutlinedButton(
                onClick = {
                    // Google se conectará después.
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {

                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google",
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = "Continuar con Google",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textoNegro
                )
            }

            Spacer(modifier = Modifier.height(18.dp))


            // =========================================
            // REGISTRO
            // =========================================

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "¿No tienes cuenta? ",
                    fontSize = 13.sp,
                    color = textoGris
                )

                Text(
                    text = "Regístrate",
                    modifier = Modifier.clickable { onRegister() },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = verdeUniFind
                )
            }
        }
    }
}