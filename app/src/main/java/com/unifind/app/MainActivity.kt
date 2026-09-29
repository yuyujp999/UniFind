package com.unifind.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.unifind.app.ui.theme.UniFindTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UniFindTheme {

                var pantallaActual by remember {
                    mutableStateOf("inicio")
                }

                when (pantallaActual) {

                    // ==========================================
                    // PANTALLA DE BIENVENIDA
                    // ==========================================

                    "inicio" -> {

                        WelcomeScreen(

                            onComenzarClick = {
                                pantallaActual = "registro"
                            },

                            onLoginClick = {
                                pantallaActual = "login"
                            }
                        )
                    }


                    // ==========================================
                    // PANTALLA DE INICIAR SESIÓN
                    // ==========================================

                    "login" -> {

                        LoginScreen(

                            onBack = {
                                pantallaActual = "inicio"
                            },

                            onRegister = {
                                pantallaActual = "registro"
                            }
                        )
                    }


                    // ==========================================
                    // PANTALLA DE REGISTRO
                    // ==========================================

                    "registro" -> {

                        RegistroScreen(

                            onBack = {
                                pantallaActual = "inicio"
                            }
                        )
                    }
                }
            }
        }
    }
}