package com.unifind.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.unifind.app.data.UniFindRepository
import com.unifind.app.ui.theme.UniFindTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            UniFindTheme {

                // Si ya había una sesión iniciada, entra directo a la app.
                var pantallaActual by rememberSaveable {
                    mutableStateOf(
                        if (FirebaseAuth.getInstance().currentUser != null) "home"
                        else "inicio"
                    )
                }

                // ==========================================
                // AL ENTRAR A LA APP:
                //  - empieza a escuchar los reportes
                //  - crea/actualiza el perfil en "usuarios"
                //  - crea las colecciones base si no existen
                // ==========================================

                LaunchedEffect(pantallaActual) {

                    if (pantallaActual == "home") {

                        UniFindRepository.iniciar()
                        UniFindRepository.asegurarPerfil()
                        UniFindRepository.inicializarBaseDeDatos()
                    }
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
                            },

                            onLogin = {
                                pantallaActual = "home"
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

                    // ==========================================
                    // APP PRINCIPAL
                    // ==========================================

                    "home" -> {

                        PantallaPrincipal(

                            onCerrarSesion = {

                                UniFindRepository.detener()

                                FirebaseAuth.getInstance().signOut()

                                pantallaActual = "inicio"
                            }
                        )
                    }
                }
            }
        }
    }
}
