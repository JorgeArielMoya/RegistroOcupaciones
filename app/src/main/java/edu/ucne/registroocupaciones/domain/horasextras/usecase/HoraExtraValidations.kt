package edu.ucne.registroocupaciones.domain.horasextras.usecase

import java.time.LocalDate

data class ValidationResult(
    val isValid : Boolean,
    val error : String? = null
)


fun validateEmpleadoId(empleadoId: Int): ValidationResult {
    return when {
        empleadoId <= 0 -> ValidationResult(false, "Debe seleccionar un empleado")
        else -> ValidationResult(true)
    }
}

fun validateFechaDesde(fechaDesde: LocalDate): ValidationResult {
    return when {
        fechaDesde.isAfter(LocalDate.now()) -> ValidationResult(false, "La fecha de inicio no puede ser futura")
        else -> ValidationResult(true)
    }
}

fun validateFechaHasta(fechaDesde: LocalDate, fechaHasta: LocalDate): ValidationResult {
    return when {
        fechaHasta.isAfter(LocalDate.now()) -> ValidationResult(false, "La fecha de fin no puede ser futura")
        fechaHasta.isBefore(fechaDesde) -> ValidationResult(false, "La fecha de fin no puede ser anterior a la fecha de inicio")
        fechaHasta == fechaDesde -> ValidationResult(false, "La fecha de fin no puede ser igual a la fecha de inicio")
        else -> ValidationResult(true)
    }
}
fun validateHorasTotales(horasTotales: Double): ValidationResult {
    return when {
        horasTotales == 0.0 -> ValidationResult(false, "Las horas totales no pueden estar vacías")
        horasTotales <= 44.0 -> ValidationResult(false, "Las horas totales deben ser mayores a 44 para generar horas extras")
        horasTotales > 124.0 -> ValidationResult(false, "Las horas totales no pueden exceder 124 horas semanales")
        else -> ValidationResult(true)
    }
}

fun validateHorasNocturnas(horasNocturnas: Double, horasTotales: Double): ValidationResult {
    return when {
        horasNocturnas < 0 -> ValidationResult(false, "Las horas nocturnas no pueden ser negativas")
        horasNocturnas > (horasTotales - 44.0) -> ValidationResult(false, "Las horas nocturnas no pueden exceder las horas extras")
        else -> ValidationResult(true)
    }
}