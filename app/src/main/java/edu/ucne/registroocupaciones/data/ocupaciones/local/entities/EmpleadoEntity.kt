package edu.ucne.registroocupaciones.data.ocupaciones.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "empleados")
data class EmpleadoEntity (
    @PrimaryKey(autoGenerate = true)
    val empleadoId : Int = 0,
    val fecha: Date = Date(),
    val nombres: String,
    val sexo: String,
    val sueldo: Double
)