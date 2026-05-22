package edu.ucne.registroocupaciones.data.ocupaciones.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "empleados")
data class EmpleadoEntity (
    @PrimaryKey(autoGenerate = true)
    val empleadoId : Int = 0,
    val fechaIngreso: LocalDate = LocalDate.now(),
    val nombres: String,
    val sexo: String,
    val sueldo: Double
)