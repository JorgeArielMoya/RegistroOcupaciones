package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OcupacionValidationsTest {

    @Test
    fun `validateDescripcion fails on blank`() {
        val res = validateDescripcion("")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isNotEmpty()
    }

    @Test
    fun `validateDescripcion fails on whitespace`() {
        val res = validateDescripcion("   ")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("La descripción no puede estar vacía")
    }

    @Test
    fun `validateDescripcion fails on short`() {
        val res = validateDescripcion("Ab")
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("La descripción debe tener al menos 3 caracteres")
    }

    @Test
    fun `validateDescripcion fails when exceeds 100 characters`() {
        val res = validateDescripcion("A".repeat(101))
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("La descripción no puede exceder los 100 caracteres")
    }

    @Test
    fun `validateDescripcion passes on exactly 3 characters`() {
        val res = validateDescripcion("CEO")
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateDescripcion passes on exactly 100 characters`() {
        val res = validateDescripcion("A".repeat(100))
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateDescripcion passes on valid description`() {
        val res = validateDescripcion("Desarrollador")
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
        val res = validateSueldo("-100")
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
        val res = validateSueldo("50000.0")
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }
}