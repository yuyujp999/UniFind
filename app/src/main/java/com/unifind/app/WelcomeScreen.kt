package com.unifind.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onComenzarClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val verdeUniFind = Color(0xFF006B50)
    val verdeOscuro = Color(0xFF005640)
    val blancoCrema = Color(0xFFFFFEF5)

    var mostrarContenido by remember {
        mutableStateOf(false)
    }

    var presionComenzar by remember {
        mutableStateOf(false)
    }

    var presionLogin by remember {
        mutableStateOf(false)
    }

    val escalaComenzar by animateFloatAsState(
        targetValue = if (presionComenzar) 0.96f else 1f,
        animationSpec = tween(
            durationMillis = 120,
            easing = FastOutSlowInEasing
        ),
        label = "escalaComenzar"
    )

    val escalaLogin by animateFloatAsState(
        targetValue = if (presionLogin) 0.96f else 1f,
        animationSpec = tween(
            durationMillis = 120,
            easing = FastOutSlowInEasing
        ),
        label = "escalaLogin"
    )

    LaunchedEffect(Unit) {
        delay(250)
        mostrarContenido = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo completo de bienvenida
        Image(
            painter = painterResource(
                id = R.drawable.pantalla_de_inicio
            ),
            contentDescription = "Bienvenida a UniFind",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido inferior con entrada animada
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 0.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Frase de bienvenida
            AnimatedVisibility(
                visible = mostrarContenido,
                enter = fadeIn(
                    animationSpec = tween(550)
                ) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(
                        durationMillis = 550,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Text(
                    text = "Juntos encontramos\nlo que importa",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = verdeOscuro,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // Botón principal
            AnimatedVisibility(
                visible = mostrarContenido,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 450,
                        delayMillis = 120
                    )
                ) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = 120,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Button(
                    onClick = {
                        presionComenzar = true
                        onComenzarClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .scale(escalaComenzar),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = verdeUniFind,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 5.dp,
                        pressedElevation = 1.dp
                    )
                ) {
                    Text(
                        text = "Comenzar",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Botón secundario
            AnimatedVisibility(
                visible = mostrarContenido,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 450,
                        delayMillis = 220
                    )
                ) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = 220,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                OutlinedButton(
                    onClick = {
                        presionLogin = true
                        onLoginClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(escalaLogin),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = blancoCrema.copy(
                            alpha = 0.72f
                        ),
                        contentColor = verdeOscuro
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.5.dp,
                        color = verdeUniFind.copy(
                            alpha = 0.75f
                        )
                    )
                ) {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Espacio inferior para elevar los botones
            Spacer(
                modifier = Modifier
                    .height(42.dp)
                    .offset(y = (-8).dp)
            )
        }
    }
}