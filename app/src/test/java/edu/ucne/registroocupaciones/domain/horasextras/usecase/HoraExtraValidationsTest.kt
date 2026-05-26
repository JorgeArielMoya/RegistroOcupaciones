package edu.ucne.registroocupaciones.domain.horasextras.usecase

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class HoraExtraValidationsTest {

    @Test
    fun `validateEmpleadoId fails on zero`() {
        val res = validateEmpleadoId(0)
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("Debe seleccionar un empleado")
    }

    @Test
    fun `validateEmpleadoId fails on negative`() {
        val res = validateEmpleadoId(-1)
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("Debe seleccionar un empleado")
    }

    @Test
    fun `validateEmpleadoId passes on valid id`() {
        val res = validateEmpleadoId(1)
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateFechaDesde fails when date is in the future`() {
        val res = validateFechaDesde(LocalDate.now().plusDays(1))
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("La fecha de inicio no puede ser futura")
    }

    @Test
    fun `validateFechaDesde passes on today`() {
        val res = validateFechaDesde(LocalDate.now())
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateFechaDesde passes on past date`() {
        val res = validateFechaDesde(LocalDate.of(2026, 5, 19))
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateFechaHasta fails when fechaHasta is in the future`() {
        val res = validateFechaHasta(
            LocalDate.of(2026, 5, 19),
            LocalDate.now().plusDays(1)
        )
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("La fecha de fin no puede ser futura")
    }

    @Test
    fun `validateFechaHasta fails when fechaHasta is before fechaDesde`() {
        val res = validateFechaHasta(
            LocalDate.of(2026, 5, 19),
            LocalDate.of(2026, 5, 18)
        )
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("La fecha de fin no puede ser anterior a la fecha de inicio")
    }

    @Test
    fun `validateFechaHasta fails when fechaHasta equals fechaDesde`() {
        val res = validateFechaHasta(
            LocalDate.of(2026, 5, 19),
            LocalDate.of(2026, 5, 19)
        )
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("La fecha de fin no puede ser igual a la fecha de inicio")
    }

    @Test
    fun `validateFechaHasta passes on valid range`() {
        val res = validateFechaHasta(
            LocalDate.of(2026, 5, 19),
            LocalDate.of(2026, 5, 25)
        )
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateHorasTotales fails on zero`() {
        val res = validateHorasTotales(0.0)
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("Las horas totales no pueden estar vacías")
    }

    @Test
    fun `validateHorasTotales fails on exactly 44`() {
        val res = validateHorasTotales(44.0)
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("Las horas totales deben ser mayores a 44 para generar horas extras")
    }

    @Test
    fun `validateHorasTotales fails when below 44`() {
        val res = validateHorasTotales(40.0)
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("Las horas totales deben ser mayores a 44 para generar horas extras")
    }

    @Test
    fun `validateHorasTotales fails when exceeds 124`() {
        val res = validateHorasTotales(125.0)
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("Las horas totales no pueden exceder 124 horas semanales")
    }

    @Test
    fun `validateHorasTotales passes on exactly 124`() {
        val res = validateHorasTotales(124.0)
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateHorasTotales passes on solo tramo 35 percent`() {
        val res = validateHorasTotales(52.0)
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateHorasTotales passes on tope exacto del 35 percent`() {
        val res = validateHorasTotales(68.0)
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateHorasTotales passes on tramo 35 y 100 percent`() {
        val res = validateHorasTotales(75.0)
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateHorasNocturnas fails on negative`() {
        val res = validateHorasNocturnas(-1.0, 52.0)
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("Las horas nocturnas no pueden ser negativas")
    }

    @Test
    fun `validateHorasNocturnas fails when exceeds horas extras`() {
        val res = validateHorasNocturnas(9.0, 52.0)
        assertThat(res.isValid).isFalse()
        assertThat(res.error).isEqualTo("Las horas nocturnas no pueden exceder las horas extras")
    }

    @Test
    fun `validateHorasNocturnas passes on zero`() {
        val res = validateHorasNocturnas(0.0, 52.0)
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateHorasNocturnas passes on exactly equal to horas extras`() {
        val res = validateHorasNocturnas(8.0, 52.0)
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }

    @Test
    fun `validateHorasNocturnas passes with tramo 35 and 100 percent`() {
        val res = validateHorasNocturnas(10.0, 75.0)
        assertThat(res.isValid).isTrue()
        assertThat(res.error).isNull()
    }
}