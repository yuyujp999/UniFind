package com.unifind.app.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Acceso único a Firebase para toda la app.
 *
 * Colecciones que maneja (se crean desde el código):
 *
 *  usuarios/{uid}                    perfil de cada persona
 *  reportes/{id}                     objetos perdidos / encontrados
 *  reportes/{id}/contactos/{id}      quién se contactó por el objeto (F08)
 *  reportes/{id}/historial/{id}      cambios de estado (F07)
 *  categorias/{id}                   catálogo de categorías
 *  ubicaciones/{id}                  catálogo de ubicaciones aproximadas
 *  estados/{id}                      catálogo de estados
 *  config/app                        versión del esquema
 */
object UniFindRepository {

    private const val TAG = "UniFindRepo"

    private val db get() = FirebaseFirestore.getInstance()
    private val auth get() = FirebaseAuth.getInstance()

    val miUid: String
        get() = auth.currentUser?.uid.orEmpty()

    // ------------------------------------------------------
    // ESTADO COMPARTIDO (todas las pantallas lo observan)
    // ------------------------------------------------------

    private val _reportes = MutableStateFlow<List<Reporte>>(emptyList())
    val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _nombres = MutableStateFlow<Map<String, String>>(emptyMap())
    val nombres: StateFlow<Map<String, String>> = _nombres.asStateFlow()

    private var listenerReportes: ListenerRegistration? = null

    // ------------------------------------------------------
    // INICIO / CIERRE DE SESIÓN
    // ------------------------------------------------------

    /** Empieza a escuchar los reportes en tiempo real. */
    fun iniciar() {

        if (listenerReportes != null) return

        _cargando.value = true

        listenerReportes = db.collection("reportes")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .limit(500)
            .addSnapshotListener { snapshot, e ->

                if (e != null) {
                    Log.e(TAG, "Error escuchando reportes", e)
                    _error.value =
                        "No se pudieron cargar los reportes. Revisa tu conexión."
                    _cargando.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _reportes.value =
                        snapshot.documents.mapNotNull { it.aReporte() }
                    _error.value = null
                    _cargando.value = false
                }
            }
    }

    fun reintentar() {
        detener()
        iniciar()
    }

    fun detener() {
        listenerReportes?.remove()
        listenerReportes = null
        _reportes.value = emptyList()
        _cargando.value = true
        _error.value = null
    }

    // ------------------------------------------------------
    // CREA LAS "TABLAS" (COLECCIONES) DE LA BASE DE DATOS
    //
    // Firestore crea una colección cuando se escribe su primer
    // documento. Esta función se ejecuta al iniciar sesión y,
    // solo la primera vez (si no existe config/app), escribe:
    // categorías, ubicaciones, estados y la configuración.
    // ------------------------------------------------------

    fun inicializarBaseDeDatos() {

        if (auth.currentUser == null) return

        val config = db.collection("config").document("app")

        config.get()
            .addOnSuccessListener { doc ->

                if (doc.exists()) return@addOnSuccessListener

                val lote = db.batch()

                Catalogo.categorias.forEachIndexed { i, c ->
                    lote.set(
                        db.collection("categorias").document(idSeguro(c.nombre)),
                        mapOf(
                            "nombre" to c.nombre,
                            "emoji" to c.emoji,
                            "orden" to i
                        )
                    )
                }

                Catalogo.ubicaciones.forEachIndexed { i, u ->
                    lote.set(
                        db.collection("ubicaciones").document(idSeguro(u)),
                        mapOf("nombre" to u, "orden" to i)
                    )
                }

                EstadoReporte.entries.forEach { e ->
                    lote.set(
                        db.collection("estados").document(e.name.lowercase()),
                        mapOf(
                            "nombre" to e.etiqueta,
                            "corto" to e.corto,
                            "orden" to e.orden
                        )
                    )
                }

                lote.set(
                    config,
                    mapOf(
                        "app" to "UniFind",
                        "institucion" to "UT Santa Catarina - Campus Montemorelos",
                        "schemaVersion" to 1,
                        "creado" to FieldValue.serverTimestamp()
                    )
                )

                lote.commit().addOnFailureListener {
                    Log.w(TAG, "No se pudieron crear los catálogos", it)
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "No se pudo leer config/app", it)
            }
    }

    // ------------------------------------------------------
    // PERFIL DE USUARIO (colección "usuarios")
    // ------------------------------------------------------

    fun asegurarPerfil() {

        val usuario = auth.currentUser ?: return

        val ref = db.collection("usuarios").document(usuario.uid)

        ref.get().addOnSuccessListener { doc ->

            val datos = mutableMapOf<String, Any>(
                "ultimoAcceso" to FieldValue.serverTimestamp()
            )

            if (!doc.exists()) {

                datos["nombre"] =
                    usuario.displayName?.takeIf { it.isNotBlank() }
                        ?: usuario.email?.substringBefore("@")
                        ?: "Usuario"
                datos["correo"] = usuario.email.orEmpty()
                datos["fotoUrl"] = usuario.photoUrl?.toString().orEmpty()
                datos["fechaRegistro"] = FieldValue.serverTimestamp()
            }

            ref.set(datos, SetOptions.merge())
        }
    }

    fun guardarWhatsapp(numero: String) {

        val uid = auth.currentUser?.uid ?: return

        db.collection("usuarios").document(uid)
            .set(mapOf("whatsapp" to numero), SetOptions.merge())
    }

    /** Pide el nombre de otra persona (una sola vez, queda en caché). */
    fun pedirNombre(uid: String) {

        if (uid.isBlank() || _nombres.value.containsKey(uid)) return

        // Marca provisional para no repetir la consulta.
        _nombres.value = _nombres.value + (uid to "")

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val nombre = doc.getString("nombre")
                    ?.takeIf { it.isNotBlank() }
                    ?: "Usuario UniFind"
                _nombres.value = _nombres.value + (uid to nombre)
            }
            .addOnFailureListener {
                _nombres.value = _nombres.value + (uid to "Usuario UniFind")
            }
    }

    // ------------------------------------------------------
    // F07 - CAMBIAR EL ESTADO DE UN REPORTE
    // ------------------------------------------------------

    fun cambiarEstado(
        reporte: Reporte,
        nuevo: EstadoReporte,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {

        val uid = auth.currentUser?.uid

        if (uid == null) {
            onError("Debes iniciar sesión para continuar")
            return
        }

        val ref = db.collection("reportes").document(reporte.id)
        val historial = ref.collection("historial").document()

        db.runTransaction { tr ->

            val actual = tr.get(ref)

            if (!actual.exists()) {
                throw FirebaseFirestoreException(
                    "Este reporte ya no existe",
                    FirebaseFirestoreException.Code.NOT_FOUND
                )
            }

            val estadoActual = actual.getString("estado").orEmpty()

            val campos = mutableMapOf<String, Any>(
                "estado" to nuevo.etiqueta,
                "fechaActualizacion" to FieldValue.serverTimestamp()
            )

            when (nuevo) {

                EstadoReporte.RECUPERADO ->
                    campos["fechaRecuperado"] = FieldValue.serverTimestamp()

                // Reabrir: se quita a la persona que lo había atendido.
                EstadoReporte.PERDIDO, EstadoReporte.ENCONTRADO -> {
                    campos["reclamadoPor"] = FieldValue.delete()
                    campos["encontradoPor"] = FieldValue.delete()
                    campos["fechaRecuperado"] = FieldValue.delete()
                }

                else -> Unit
            }

            tr.update(ref, campos)

            tr.set(
                historial,
                mapOf(
                    "estadoAnterior" to estadoActual,
                    "estadoNuevo" to nuevo.etiqueta,
                    "usuarioId" to uid,
                    "fecha" to FieldValue.serverTimestamp()
                )
            )

            null

        }.addOnSuccessListener {
            onExito()
        }.addOnFailureListener { e ->
            onError(e.localizedMessage ?: "No se pudo actualizar el estado")
        }
    }

    // ------------------------------------------------------
    // "LO ENCONTRÉ" / "ES MÍO"
    // Misma lógica que usa PublicarScreen al sugerir coincidencias.
    // ------------------------------------------------------

    fun atenderReporte(
        reporte: Reporte,
        mensaje: String,
        telefono: String,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {

        val uid = auth.currentUser?.uid

        if (uid == null) {
            onError("Debes iniciar sesión para continuar")
            return
        }

        val ref = db.collection("reportes").document(reporte.id)
        val contacto = ref.collection("contactos").document()
        val historial = ref.collection("historial").document()

        val esperado = reporte.estado.etiqueta

        val campos: Map<String, Any> =
            if (reporte.esPerdido) {
                mapOf(
                    "estado" to EstadoReporte.EN_RECUPERACION.etiqueta,
                    "encontradoPor" to uid,
                    "fechaHallazgo" to FieldValue.serverTimestamp(),
                    "fechaActualizacion" to FieldValue.serverTimestamp(),
                    "numContactos" to FieldValue.increment(1)
                )
            } else {
                mapOf(
                    "estado" to EstadoReporte.EN_RECUPERACION.etiqueta,
                    "reclamadoPor" to uid,
                    "fechaReclamo" to FieldValue.serverTimestamp(),
                    "fechaActualizacion" to FieldValue.serverTimestamp(),
                    "numContactos" to FieldValue.increment(1)
                )
            }

        db.runTransaction { tr ->

            val actual = tr.get(ref)

            if (!actual.exists() || actual.getString("estado") != esperado) {
                throw FirebaseFirestoreException(
                    "Este reporte ya fue atendido por alguien más",
                    FirebaseFirestoreException.Code.FAILED_PRECONDITION
                )
            }

            tr.update(ref, campos)

            tr.set(
                contacto,
                mapOf(
                    "usuarioId" to uid,
                    "tipo" to if (reporte.esPerdido) "hallazgo" else "reclamo",
                    "mensaje" to mensaje,
                    "whatsapp" to telefono,
                    "fecha" to FieldValue.serverTimestamp()
                )
            )

            tr.set(
                historial,
                mapOf(
                    "estadoAnterior" to esperado,
                    "estadoNuevo" to EstadoReporte.EN_RECUPERACION.etiqueta,
                    "usuarioId" to uid,
                    "fecha" to FieldValue.serverTimestamp()
                )
            )

            null

        }.addOnSuccessListener {
            onExito()
        }.addOnFailureListener { e ->
            onError(e.localizedMessage ?: "No se pudo completar la acción")
        }
    }

    // ------------------------------------------------------
    // F08 - REGISTRAR UN CONTACTO (solo una consulta)
    // ------------------------------------------------------

    fun registrarContacto(
        reporte: Reporte,
        mensaje: String,
        telefono: String = ""
    ) {

        val uid = auth.currentUser?.uid ?: return

        val ref = db.collection("reportes").document(reporte.id)

        val lote = db.batch()

        lote.set(
            ref.collection("contactos").document(),
            mapOf(
                "usuarioId" to uid,
                "tipo" to "consulta",
                "mensaje" to mensaje,
                "whatsapp" to telefono,
                "fecha" to FieldValue.serverTimestamp()
            )
        )

        lote.update(ref, "numContactos", FieldValue.increment(1))

        lote.commit().addOnFailureListener {
            Log.w(TAG, "No se pudo registrar el contacto", it)
        }
    }

    // ------------------------------------------------------
    // ESCUCHAR SUBCOLECCIONES DE UN REPORTE
    // ------------------------------------------------------

    fun escucharContactos(
        reporteId: String,
        onCambio: (List<ContactoReporte>) -> Unit
    ): ListenerRegistration =

        db.collection("reportes").document(reporteId)
            .collection("contactos")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->

                if (e != null || snap == null) {
                    onCambio(emptyList())
                    return@addSnapshotListener
                }

                onCambio(
                    snap.documents.map { d ->
                        ContactoReporte(
                            id = d.id,
                            usuarioId = d.getString("usuarioId").orEmpty(),
                            tipo = d.getString("tipo").orEmpty(),
                            mensaje = d.getString("mensaje").orEmpty(),
                            whatsapp = d.getString("whatsapp").orEmpty(),
                            fechaMillis = d.millis("fecha")
                        )
                    }
                )
            }

    fun escucharHistorial(
        reporteId: String,
        onCambio: (List<EventoHistorial>) -> Unit
    ): ListenerRegistration =

        db.collection("reportes").document(reporteId)
            .collection("historial")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->

                if (e != null || snap == null) {
                    onCambio(emptyList())
                    return@addSnapshotListener
                }

                onCambio(
                    snap.documents.map { d ->
                        EventoHistorial(
                            id = d.id,
                            estadoAnterior =
                                d.getString("estadoAnterior").orEmpty(),
                            estadoNuevo = d.getString("estadoNuevo").orEmpty(),
                            usuarioId = d.getString("usuarioId").orEmpty(),
                            fechaMillis = d.millis("fecha")
                        )
                    }
                )
            }

    // ------------------------------------------------------
    // ELIMINAR (solo el dueño)
    // ------------------------------------------------------

    fun eliminarReporte(
        reporteId: String,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("reportes").document(reporteId)
            .delete()
            .addOnSuccessListener { onExito() }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "No se pudo eliminar")
            }
    }

    // ------------------------------------------------------
    // UTILIDADES
    // ------------------------------------------------------

    private fun DocumentSnapshot.millis(campo: String): Long =
        runCatching {
            getTimestamp(
                campo,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )?.toDate()?.time
        }.getOrNull() ?: System.currentTimeMillis()

    private fun idSeguro(texto: String): String =
        java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
}
