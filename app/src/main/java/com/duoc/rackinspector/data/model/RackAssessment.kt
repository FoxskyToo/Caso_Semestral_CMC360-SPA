package com.duoc.rackinspector.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rack_assessments")
data class RackAssessment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rackName: String,         // Ejemplo: "Rack 01 - Sala A"
    val location: String,         // Ejemplo: "Sala A"
    val date: String,             // Ejemplo: "03/09/2026 - 10:24"
    val userEmail: String,        // Usuario que realizó el levantamiento
    val imageUri: String,         // Ruta local de la imagen del rack
    val status: String = "Validado",
    val components: List<ComponentItem> // Lista de componentes declarados
)