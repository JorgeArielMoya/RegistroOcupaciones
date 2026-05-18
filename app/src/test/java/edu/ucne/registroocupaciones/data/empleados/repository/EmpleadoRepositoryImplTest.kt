package edu.ucne.registroocupaciones.data.empleados.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.data.ocupaciones.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.ocupaciones.local.entities.EmpleadoEntity
import edu.ucne.registroocupaciones.data.ocupaciones.repository.EmpleadoRepositoryImpl
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate

@ExperimentalCoroutinesApi
class EmpleadoRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: EmpleadoRepositoryImpl
    private lateinit var dao: EmpleadoDao

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = EmpleadoRepositoryImpl(dao)
    }

    @Test
    fun `upsert guarda nuevo empleado correctamente`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Juan Pérez",
            sexo = "Masculino",
            sueldo = 45000.0
        )
        val entitySlot = slot<EmpleadoEntity>()
        coEvery { dao.upsert(capture(entitySlot)) } just Runs

        // When
        val result = repository.upsert(empleado)

        // Then
        assertEquals(0, result)
        coVerify { dao.upsert(any()) }
        assertEquals(empleado.nombres, entitySlot.captured.nombres)
        assertEquals(empleado.sexo, entitySlot.captured.sexo)
        assertEquals(empleado.sueldo, entitySlot.captured.sueldo, 0.0)
    }

    @Test
    fun `upsert actualiza empleado existente y retorna su id`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 7,
            fechaIngreso = LocalDate.of(2023, 6, 1),
            nombres = "María López",
            sexo = "Femenino",
            sueldo = 52000.0
        )
        coEvery { dao.upsert(any()) } just Runs

        // When
        val result = repository.upsert(empleado)

        // Then
        assertEquals(7, result)
        coVerify { dao.upsert(any()) }
    }

    @Test
    fun `upsert mapea la fecha a String al guardar en la entidad`() = runTest {
        // Given
        val fecha = LocalDate.of(2024, 3, 20)
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = fecha,
            nombres = "Carlos Ruiz",
            sexo = "Masculino",
            sueldo = 30000.0
        )
        val entitySlot = slot<EmpleadoEntity>()
        coEvery { dao.upsert(capture(entitySlot)) } just Runs

        // When
        repository.upsert(empleado)

        // Then
        assertEquals(fecha.toString(), entitySlot.captured.fechaIngreso)
    }

    @Test
    fun `delete elimina empleado por id correctamente`() = runTest {
        // Given
        val empleadoId = 4
        coEvery { dao.deleteById(empleadoId) } just Runs

        // When
        repository.delete(empleadoId)

        // Then
        coVerify { dao.deleteById(empleadoId) }
    }

    @Test
    fun `getEmpleado retorna empleado cuando existe el id`() = runTest {
        // Given
        val entity = EmpleadoEntity(
            empleadoId = 1,
            fechaIngreso = "2024-01-15",
            nombres = "Ana García",
            sexo = "Femenino",
            sueldo = 48000.0
        )
        coEvery { dao.getById(1) } returns entity

        // When
        val result = repository.getEmpleado(1)

        // Then
        assertNotNull(result)
        assertEquals("Ana García", result?.nombres)
        assertEquals("Femenino", result?.sexo)
        assertEquals(48000.0, result?.sueldo ?: 0.0, 0.0)
        assertEquals(1, result?.empleadoId)
    }

    @Test
    fun `getEmpleado retorna null cuando no existe el id`() = runTest {
        // Given
        coEvery { dao.getById(99) } returns null

        // When
        val result = repository.getEmpleado(99)

        // Then
        assertNull(result)
    }

    @Test
    fun `observeAll retorna flow con lista de empleados`() = runTest {
        // Given
        val entities = listOf(
            EmpleadoEntity(1, "2023-05-10", "Pedro Soto", "M", 40000.0),
            EmpleadoEntity(2, "2024-02-20", "Laura Méndez", "F", 55000.0)
        )
        every { dao.observeAll() } returns flowOf(entities)

        // When
        val result = repository.observeAll().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Pedro Soto", result[0].nombres)
        assertEquals(40000.0, result[0].sueldo, 0.0)
        assertEquals("Laura Méndez", result[1].nombres)
        assertEquals(55000.0, result[1].sueldo, 0.0)
    }

    @Test
    fun `observeAll retorna flow vacio cuando no hay empleados`() = runTest {
        // Given
        every { dao.observeAll() } returns flowOf(emptyList())

        // When
        val result = repository.observeAll().first()

        // Then
        assertTrue(result.isEmpty())
    }
}