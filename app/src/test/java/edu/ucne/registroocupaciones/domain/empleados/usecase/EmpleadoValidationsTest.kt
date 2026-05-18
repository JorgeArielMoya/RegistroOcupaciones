package edu.ucne.registroocupaciones.domain.empleados.usecase

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.threeten.bp.LocalDate

class EmpleadoValidationsTest {

    @Test
    fun `validateFechaIngreso fails on null`() {
        val res = validateFechaIngreso(null)
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("La fecha de ingreso no puede estar vacía")
    }

    @Test
    fun `validateFechaIngreso passes on valid date`() {
        val res = validateFechaIngreso(LocalDate.of(2024, 1, 15))
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateNombres fails on blank`() {
        val res = validateNombres("")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("El nombre no puede estar vacío")
    }

    @Test
    fun `validateNombres fails on whitespace`() {
        val res = validateNombres("   ")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("El nombre no puede estar vacío")
    }

    @Test
    fun `validateNombres fails on short`() {
        val res = validateNombres("Jo")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("El nombre debe tener al menos 3 caracteres")
    }

    @Test
    fun `validateNombres fails when exceeds 100 characters`() {
        val res = validateNombres("A".repeat(101))
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("El nombre no puede exceder los 100 caracteres")
    }

    @Test
    fun `validateNombres passes on exactly 3 characters`() {
        val res = validateNombres("Ana")
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateNombres passes on exactly 100 characters`() {
        val res = validateNombres("A".repeat(100))
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateNombres passes on valid name`() {
        val res = validateNombres("Juan Pérez")
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateSexo fails on blank`() {
        val res = validateSexo("")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("Debe seleccionar una opción")
    }

    @Test
    fun `validateSexo fails on whitespace`() {
        val res = validateSexo("   ")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("Debe seleccionar una opción")
    }

    @Test
    fun `validateSexo passes on M`() {
        val res = validateSexo("M")
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateSexo passes on F`() {
        val res = validateSexo("F")
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateSueldo fails on blank`() {
        val res = validateSueldo("")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("El sueldo no puede estar vacío")
    }

    @Test
    fun `validateSueldo fails on non numeric`() {
        val res = validateSueldo("abc")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("El sueldo debe ser un número válido")
    }

    @Test
    fun `validateSueldo fails on zero`() {
        val res = validateSueldo("0")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("El sueldo debe ser mayor a 0")
    }

    @Test
    fun `validateSueldo fails on negative`() {
        val res = validateSueldo("-500")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("El sueldo debe ser mayor a 0")
    }

    @Test
    fun `validateSueldo fails when exceeds 999999 99`() {
        val res = validateSueldo("1000000.0")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("El sueldo no puede exceder 999,999.99")
    }

    @Test
    fun `validateSueldo passes on exactly 999999 99`() {
        val res = validateSueldo("999999.99")
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateSueldo passes on valid amount`() {
        val res = validateSueldo("45000.0")
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }
}