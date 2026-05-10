package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

data class ValidationResult(
    val isValid: Boolean,
    val error: String? = null
)

fun validateDescripcion(descripcion: String): ValidationResult {
    return when {
        descripcion.isBlank() -> ValidationResult(false, "La descripción no puede estar vacía")
        descripcion.length < 3 -> ValidationResult(false, "La descripción debe tener al menos 3 caracteres")
        descripcion.length > 100 -> ValidationResult(false, "La descripción no puede exceder los 100 caracteres")
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