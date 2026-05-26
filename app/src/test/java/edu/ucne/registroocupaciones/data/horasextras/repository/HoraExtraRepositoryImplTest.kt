package edu.ucne.registroocupaciones.data.horasextras.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.data.local.dao.HoraExtraDao
import edu.ucne.registroocupaciones.data.local.entities.HoraExtraEntity
import edu.ucne.registroocupaciones.data.repository.HoraExtraRepositoryImpl
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class HoraExtraRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: HoraExtraRepositoryImpl
    private lateinit var dao: HoraExtraDao

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = HoraExtraRepositoryImpl(dao)
    }

    @Test
    fun `upsert guarda nueva hora extra correctamente`() = runTest {
        // Given
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 52.0,
            horasNocturnas = 0.0
        )
        val entitySlot = slot<HoraExtraEntity>()
        coEvery { dao.upsert(capture(entitySlot)) } just Runs

        // When
        val result = repository.upsert(horaExtra)

        // Then
        assertEquals(0, result)
        coVerify { dao.upsert(any()) }
        assertEquals(horaExtra.empleadoId, entitySlot.captured.empleadoId)
        assertEquals(horaExtra.horasTotales, entitySlot.captured.horasTotales, 0.0)
        assertEquals(horaExtra.horasNocturnas, entitySlot.captured.horasNocturnas, 0.0)
    }

    @Test
    fun `upsert actualiza hora extra existente y retorna su id`() = runTest {
        // Given
        val horaExtra = HoraExtra(
            horaExtraId = 5,
            empleadoId = 2,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 75.0,   // tramo 35% y 100%
            horasNocturnas = 10.0
        )
        coEvery { dao.upsert(any()) } just Runs

        // When
        val result = repository.upsert(horaExtra)

        // Then
        assertEquals(5, result)
        coVerify { dao.upsert(any()) }
    }

    @Test
    fun `deleteHoraExtra elimina hora extra por id correctamente`() = runTest {
        // Given
        val horaExtraId = 3
        coEvery { dao.deleteById(horaExtraId) } just Runs

        // When
        repository.deleteHoraExtra(horaExtraId)

        // Then
        coVerify { dao.deleteById(horaExtraId) }
    }

    @Test
    fun `getHoraExtra retorna hora extra cuando existe el id`() = runTest {
        // Given
        val entity = HoraExtraEntity(
            horaExtraId = 1,
            empleadoId = 2,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 60.0,
            horasNocturnas = 8.0
        )
        coEvery { dao.getById(1) } returns entity

        // When
        val result = repository.getHoraExtra(1)

        // Then
        assertNotNull(result)
        assertEquals(1, result?.horaExtraId)
        assertEquals(2, result?.empleadoId)
        assertEquals(60.0, result?.horasTotales ?: 0.0, 0.0)
        assertEquals(8.0, result?.horasNocturnas ?: 0.0, 0.0)
    }

    @Test
    fun `getHoraExtra retorna null cuando no existe el id`() = runTest {
        // Given
        coEvery { dao.getById(99) } returns null

        // When
        val result = repository.getHoraExtra(99)

        // Then
        assertNull(result)
    }

    @Test
    fun `observeAll retorna flow con lista de horas extras`() = runTest {
        // Given
        val entities = listOf(
            HoraExtraEntity(
                horaExtraId = 1,
                empleadoId = 1,
                fechaDesde = LocalDate.of(2026, 5, 12),
                fechaHasta = LocalDate.of(2026, 5, 18),
                horasTotales = 52.0,   // 8h al 35%
                horasNocturnas = 0.0
            ),
            HoraExtraEntity(
                horaExtraId = 2,
                empleadoId = 2,
                fechaDesde = LocalDate.of(2026, 5, 19),
                fechaHasta = LocalDate.of(2026, 5, 25),
                horasTotales = 75.0,   // 24h al 35% + 7h al 100%
                horasNocturnas = 10.0
            )
        )
        every { dao.observeAll() } returns flowOf(entities)

        // When
        val result = repository.observeAll().first()

        // Then
        assertEquals(2, result.size)
        assertEquals(52.0, result[0].horasTotales, 0.0)
        assertEquals(0.0, result[0].horasNocturnas, 0.0)
        assertEquals(75.0, result[1].horasTotales, 0.0)
        assertEquals(10.0, result[1].horasNocturnas, 0.0)
    }

    @Test
    fun `observeAll retorna flow vacio cuando no hay horas extras`() = runTest {
        // Given
        every { dao.observeAll() } returns flowOf(emptyList())

        // When
        val result = repository.observeAll().first()

        // Then
        assertTrue(result.isEmpty())
    }
}