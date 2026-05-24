package edu.ucne.registroocupaciones.data.ocupaciones.repository


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.data.local.dao.OcupacionDao
import edu.ucne.registroocupaciones.data.local.entities.OcupacionEntity
import edu.ucne.registroocupaciones.data.repository.OcupacionRepositoryImpl
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class OcupacionRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: OcupacionRepositoryImpl
    private lateinit var dao: OcupacionDao

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = OcupacionRepositoryImpl(dao)
    }

    @Test
    fun `upsert guarda nueva ocupacion correctamente`() = runTest {
        // Given
        val ocupacion = Ocupacion(
            ocupacionId = 0,
            descripcion = "Desarrollador",
            sueldo = 50000.0
        )
        val entitySlot = slot<OcupacionEntity>()
        coEvery { dao.upsert(capture(entitySlot)) } just Runs

        // When
        val result = repository.upsert(ocupacion)

        // Then
        assertEquals(0, result)
        coVerify { dao.upsert(any()) }
        assertEquals(ocupacion.descripcion, entitySlot.captured.descripcion)
        assertEquals(ocupacion.sueldo, entitySlot.captured.sueldo, 0.0)
    }

    @Test
    fun `upsert actualiza ocupacion existente correctamente`() = runTest {
        // Given
        val ocupacion = Ocupacion(
            ocupacionId = 5,
            descripcion = "Diseñador",
            sueldo = 35000.0
        )
        coEvery { dao.upsert(any()) } just Runs

        // When
        val result = repository.upsert(ocupacion)

        // Then
        assertEquals(5, result)
        coVerify { dao.upsert(any()) }
    }

    @Test
    fun `delete elimina ocupacion por id correctamente`() = runTest {
        // Given
        val ocupacionId = 3
        coEvery { dao.deleteById(ocupacionId) } just Runs

        // When
        repository.delete(ocupacionId)

        // Then
        coVerify { dao.deleteById(ocupacionId) }
    }

    @Test
    fun `observeOcupaciones retorna flow con lista de ocupaciones`() = runTest {
        // Given
        val entities = listOf(
            OcupacionEntity(1, "Médico", 80000.0),
            OcupacionEntity(2, "Abogado", 60000.0)
        )
        every { dao.observeAll() } returns flowOf(entities)

        // When
        val result = repository.observeOcupaciones().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Médico", result[0].descripcion)
        assertEquals(80000.0, result[0].sueldo, 0.0)
        assertEquals("Abogado", result[1].descripcion)
        assertEquals(60000.0, result[1].sueldo, 0.0)
    }

    @Test
    fun `observeOcupaciones retorna flow vacio cuando no hay ocupaciones`() = runTest {
        // Given
        every { dao.observeAll() } returns flowOf(emptyList())

        // When
        val result = repository.observeOcupaciones().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getOcupacion retorna ocupacion cuando existe el id`() = runTest {
        // Given
        val entity = OcupacionEntity(1, "Ingeniero", 70000.0)
        coEvery { dao.getById(1) } returns entity

        // When
        val result = repository.getOcupacion(1)

        // Then
        assertNotNull(result)
        assertEquals("Ingeniero", result?.descripcion)
        assertEquals(70000.0, result?.sueldo ?: 0.0, 0.0)
        assertEquals(1, result?.ocupacionId)
    }

    @Test
    fun `getOcupacion retorna null cuando no existe el id`() = runTest {
        // Given
        coEvery { dao.getById(99) } returns null

        // When
        val result = repository.getOcupacion(99)

        // Then
        assertNull(result)
    }

    @Test
    fun `existsByDescripcion retorna true cuando descripcion ya existe`() = runTest {
        // Given
        coEvery { dao.existsByDescripcion("Contador", 0) } returns true

        // When
        val result = repository.existsByDescripcion("Contador", excludeId = 0)

        // Then
        assertTrue(result)
        coVerify { dao.existsByDescripcion("Contador", 0) }
    }

    @Test
    fun `existsByDescripcion retorna false cuando descripcion no existe`() = runTest {
        // Given
        coEvery { dao.existsByDescripcion("Piloto", 0) } returns false

        // When
        val result = repository.existsByDescripcion("Piloto", excludeId = 0)

        // Then
        assertFalse(result)
    }

    @Test
    fun `existsByDescripcion excluye el id indicado al verificar duplicados`() = runTest {
        // Given
        coEvery { dao.existsByDescripcion("Arquitecto", 2) } returns false

        // When
        val result = repository.existsByDescripcion("Arquitecto", excludeId = 2)

        // Then
        assertFalse(result)
        coVerify { dao.existsByDescripcion("Arquitecto", 2) }
    }
}