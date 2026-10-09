package com.unifind.app.data

import com.google.firebase.firestore.DocumentSnapshot

// ==========================================================
// ESTADOS DE UN REPORTE (F07 - Seguimiento del estado)
//
// Flujo normal:
//   Perdido / Encontrado  ->  En proceso de recuperación  ->  Recuperado
// ==========================================================

enum class EstadoReporte(
    val etiqueta: String,
    val corto: String,
    val orden: Int
) {
    PERDIDO("Perdido", "Perdido", 0),
    ENCONTRADO("Encontrado", "Encontrado", 0),
    EN_RECUPERACION("En proceso de recuperación", "En proceso", 1),
    RECUPERADO("Recuperado", "Recuperado", 2);

    val abierto: Boolean
        get() = this == PERDIDO || this == ENCONTRADO

    companion object {
        fun desde(texto: String?): EstadoReporte {
            val limpio = texto?.trim().orEmpty()
            return entries.firstOrNull {
                it.etiqueta.equals(limpio, ignoreCase = true) ||
                    it.corto.equals(limpio, ignoreCase = true) ||
                    (limpio.equals("En recuperación", ignoreCase = true) &&
                        it == EN_RECUPERACION)
            } ?: PERDIDO
        }
    }
}

// ==========================================================
// REPORTE (colección "reportes")
// ==========================================================

data class Reporte(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val ubicacion: String,
    val estado: EstadoReporte,
    /** true = lo publicó alguien que PERDIÓ el objeto; false = alguien lo ENCONTRÓ. */
    val esPerdido: Boolean,
    val usuarioId: String,
    val whatsapp: String,
    val fotos: List<String>,
    val fechaMillis: Long,
    val numContactos: Int,
    val reclamadoPor: String?,
    val encontradoPor: String?
) {
    /** Persona que atendió el reporte (quien lo encontró o quien lo reclamó). */
    val contraparteId: String?
        get() = reclamadoPor ?: encontradoPor

    val tipoTexto: String
        get() = if (esPerdido) "Lo perdió" else "Lo encontró"
}

// ==========================================================
// CONTACTO (subcolección reportes/{id}/contactos)
// ==========================================================

data class ContactoReporte(
    val id: String,
    val usuarioId: String,
    val tipo: String,
    val mensaje: String,
    val whatsapp: String,
    val fechaMillis: Long
)

// ==========================================================
// HISTORIAL (subcolección reportes/{id}/historial)
// ==========================================================

data class EventoHistorial(
    val id: String,
    val estadoAnterior: String,
    val estadoNuevo: String,
    val usuarioId: String,
    val fechaMillis: Long
)

// ==========================================================
// CATÁLOGOS (se guardan también en Firestore al iniciar)
// ==========================================================

data class CategoriaInfo(val nombre: String, val emoji: String)

object Catalogo {

    val categorias = listOf(
        CategoriaInfo("Mochilas", "🎒"),
        CategoriaInfo("Electrónicos", "📱"),
        CategoriaInfo("Llaves", "🔑"),
        CategoriaInfo("Ropa", "👕"),
        CategoriaInfo("Accesorios", "👓"),
        CategoriaInfo("Documentos", "📄"),
        CategoriaInfo("Libros", "📚"),
        CategoriaInfo("Otros", "📦")
    )

    val ubicaciones = listOf(
        "Biblioteca",
        "Cafetería",
        "Edificio A",
        "Edificio B",
        "Aulas",
        "Gimnasio",
        "Cancha",
        "Estacionamiento",
        "Otro lugar"
    )

    fun emoji(categoria: String): String =
        categorias.firstOrNull {
            it.nombre.equals(categoria, ignoreCase = true)
        }?.emoji ?: "📦"
}

// ==========================================================
// DOCUMENTO DE FIRESTORE -> Reporte
// Compatible con lo que guarda PublicarScreen.
// ==========================================================

fun DocumentSnapshot.aReporte(): Reporte? {

    return try {

        val estado = EstadoReporte.desde(getString("estado"))
        val reclamado = getString("reclamadoPor")?.takeIf { it.isNotBlank() }
        val encontrado = getString("encontradoPor")?.takeIf { it.isNotBlank() }
        val tipoGuardado = getString("tipo")?.lowercase()

        // Si el reporte ya cambió de estado, se deduce su tipo original:
        // - lo reclamó alguien  -> originalmente era "Encontrado"
        // - lo halló alguien    -> originalmente era "Perdido"
        val esPerdido = when {
            tipoGuardado == "perdido" -> true
            tipoGuardado == "encontrado" -> false
            estado == EstadoReporte.PERDIDO -> true
            estado == EstadoReporte.ENCONTRADO -> false
            reclamado != null -> false
            encontrado != null -> true
            else -> true
        }

        val fotos = (get("fotosBase64") as? List<*>)
            ?.filterIsInstance<String>()
            ?.filter { it.isNotBlank() }
            .orEmpty()

        val fecha = runCatching {
            getTimestamp(
                "fecha",
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )?.toDate()?.time
        }.getOrNull() ?: System.currentTimeMillis()

        Reporte(
            id = id,
            titulo = getString("titulo")?.ifBlank { null } ?: "Sin título",
            descripcion = getString("descripcion").orEmpty(),
            categoria = getString("categoria").orEmpty(),
            ubicacion = getString("ubicacionAprox")?.ifBlank { null }
                ?: "Ubicación no especificada",
            estado = estado,
            esPerdido = esPerdido,
            usuarioId = getString("usuarioId").orEmpty(),
            whatsapp = getString("whatsapp").orEmpty(),
            fotos = fotos,
            fechaMillis = fecha,
            numContactos = (getLong("numContactos")
                ?: getLong("contactos")
                ?: 0L).toInt(),
            reclamadoPor = reclamado,
            encontradoPor = encontrado
        )

    } catch (e: Exception) {
        null
    }
}
