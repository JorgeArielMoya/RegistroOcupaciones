package edu.ucne.registroocupaciones.domain.empleados.model

import org.threeten.bp.LocalDate

data class Empleado (
    val empleadoId : Int = 0,
    val fechaIngreso: LocalDate,
    val nombres: String,
    val sexo: String,
    val sueldo: Double
)