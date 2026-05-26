package edu.ucne.registroocupaciones.domain.horasextras.usecase

import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class UpsertHoraExtraUseCaseTest {
    private lateinit var useCase: UpsertHoraExtraUseCase
    private lateinit var repository: HoraExtraRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = UpsertHoraExtraUseCase(repository)
    }

    @Test
    fun `invoke guarda hora extra con datos validos solo tramo 35`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 52.0,
            horasNocturnas = 0.0
        )
        coEvery { repository.upsert(horaExtra) } returns 0

        val result = useCase(horaExtra)

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull())
        coVerify { repository.upsert(horaExtra) }
    }

    @Test
    fun `invoke guarda hora extra con tramo 35 y 100`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 75.0,
            horasNocturnas = 0.0
        )
        coEvery { repository.upsert(horaExtra) } returns 0

        val result = useCase(horaExtra)

        assertTrue(result.isSuccess)
        coVerify { repository.upsert(horaExtra) }
    }

    @Test
    fun `invoke guarda hora extra con horas nocturnas`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 75.0,
            horasNocturnas = 10.0
        )
        coEvery { repository.upsert(horaExtra) } returns 0

        val result = useCase(horaExtra)

        assertTrue(result.isSuccess)
        coVerify { repository.upsert(horaExtra) }
    }

    @Test
    fun `invoke actualiza hora extra existente`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 5,
            empleadoId = 2,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 60.0,
            horasNocturnas = 8.0
        )
        coEvery { repository.upsert(horaExtra) } returns 5

        val result = useCase(horaExtra)

        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrNull())
        coVerify { repository.upsert(horaExtra) }
    }

    @Test
    fun `invoke falla cuando empleadoId es cero`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 0,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 52.0,
            horasNocturnas = 0.0
        )

        val result = useCase(horaExtra)

        assertTrue(result.isFailure)
        assertEquals("Debe seleccionar un empleado", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando fechaDesde es futura`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.now().plusDays(1),
            fechaHasta = LocalDate.now().plusDays(7),
            horasTotales = 52.0,
            horasNocturnas = 0.0
        )

        val result = useCase(horaExtra)

        assertTrue(result.isFailure)
        assertEquals("La fecha de inicio no puede ser futura", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando fechaHasta es anterior a fechaDesde`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 18),
            horasTotales = 52.0,
            horasNocturnas = 0.0
        )

        val result = useCase(horaExtra)

        assertTrue(result.isFailure)
        assertEquals("La fecha de fin no puede ser anterior a la fecha de inicio", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando fechaHasta es igual a fechaDesde`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 19),
            horasTotales = 52.0,
            horasNocturnas = 0.0
        )

        val result = useCase(horaExtra)

        assertTrue(result.isFailure)
        assertEquals("La fecha de fin no puede ser igual a la fecha de inicio", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando horasTotales es cero`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 0.0,
            horasNocturnas = 0.0
        )

        val result = useCase(horaExtra)

        assertTrue(result.isFailure)
        assertEquals("Las horas totales no pueden estar vacías", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando horasTotales no supera las 44 horas`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 44.0,
            horasNocturnas = 0.0
        )

        val result = useCase(horaExtra)

        assertTrue(result.isFailure)
        assertEquals("Las horas totales deben ser mayores a 44 para generar horas extras", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando horasTotales excede 124 horas`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 125.0,
            horasNocturnas = 0.0
        )

        val result = useCase(horaExtra)

        assertTrue(result.isFailure)
        assertEquals("Las horas totales no pueden exceder 124 horas semanales", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando horasNocturnas son negativas`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 52.0,
            horasNocturnas = -1.0
        )

        val result = useCase(horaExtra)

        assertTrue(result.isFailure)
        assertEquals("Las horas nocturnas no pueden ser negativas", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando horasNocturnas exceden las horas extras`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 52.0,
            horasNocturnas = 9.0
        )

        val result = useCase(horaExtra)

        assertTrue(result.isFailure)
        assertEquals("Las horas nocturnas no pueden exceder las horas extras", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke valida empleadoId antes que fechaDesde`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 0,
            fechaDesde = LocalDate.now().plusDays(1),
            fechaHasta = LocalDate.now().plusDays(7),
            horasTotales = 52.0,
            horasNocturnas = 0.0
        )

        val result = useCase(horaExtra)

        assertEquals("Debe seleccionar un empleado", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke valida fechaDesde antes que horasTotales`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.now().plusDays(1),
            fechaHasta = LocalDate.now().plusDays(7),
            horasTotales = 0.0,
            horasNocturnas = 0.0
        )

        val result = useCase(horaExtra)

        assertEquals("La fecha de inicio no puede ser futura", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke valida horasTotales antes que horasNocturnas`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 44.0,
            horasNocturnas = -1.0
        )

        val result = useCase(horaExtra)

        assertEquals("Las horas totales deben ser mayores a 44 para generar horas extras", result.exceptionOrNull()?.message)
    }
}