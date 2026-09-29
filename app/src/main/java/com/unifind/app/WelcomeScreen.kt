package com.unifind.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun WelcomeScreen(
    onComenzarClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    val verdeUniFind = Color(0xFF006B50)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // =========================================
        // FONDO DE BIENVENIDA
        // =========================================

        Image(
            painter = painterResource(
                id = R.drawable.pantalla_de_inicio
            ),
            contentDescription = "Pantalla de bienvenida",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        // =========================================
        // CONTENIDO
        // =========================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 30.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Bottom
        ) {

            // =========================================
            // FRASE
            // =========================================

            Text(
                text = "Juntos encontramos\nlo que importa",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = verdeUniFind,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =========================================
            // COMENZAR
            // =========================================

            Button(
                onClick = onComenzarClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                shape = RoundedCornerShape(14.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = verdeUniFind
                )
            ) {

                Text(
                    text = "Comenzar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =========================================
            // INICIAR SESIÓN
            // =========================================

            OutlinedButton(
                onClick = onLoginClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = "Iniciar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = verdeUniFind
                )
            }
        }
    }
}