package edu.ucne.registroocupaciones.domain.empleados.usecase

import java.time.LocalDate

data class ValidationResult(
    val isValid: Boolean,
    val error: String? = null
)

fun validateFechaIngreso(fecha: LocalDate?): ValidationResult {
    return when {
        fecha == null -> ValidationResult(false, "La fecha de ingreso no puede estar vacía")
        fecha.isAfter(LocalDate.now()) -> ValidationResult(false, "La fecha de ingreso no puede estar en el futuro")
        else -> ValidationResult(true)
    }
}

fun validateNombres(nombres: String): ValidationResult {
    return when {
        nombres.isBlank() -> ValidationResult(false, "El nombre no puede estar vacío")
        nombres.length < 3 -> ValidationResult(false, "El nombre debe tener al menos 3 caracteres")
        nombres.length > 100 -> ValidationResult(false, "El nombre no puede exceder los 100 caracteres")
        else -> ValidationResult(true)
    }
}

fun validateSexo(sexo: String): ValidationResult {
    return when {
        sexo.isBlank() -> ValidationResult(false, "Debe seleccionar una opción")
        else -> ValidationResult(true)
    }
}

fun validateSueldo(sueldo: String): ValidationResult {
    return when {
        sueldo.isBlank() -> ValidationResult(false, "El sueldo no puede estar vacío")
        sueldo.toDoubleOrNull() == null -> ValidationResult(false, "El sueldo debe ser un número válido")
        sueldo.toDouble() <= 0 -> ValidationResult(false, "El sueldo debe ser mayor a 0")
        sueldo.toDouble() > 999_999.99 -> ValidationResult(false, "El sueldo no puede exceder 999,999.99")
        else -> ValidationResult(true)
    }
}