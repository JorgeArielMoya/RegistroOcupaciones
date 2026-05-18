package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UpsertOcupacionUseCaseTest {
    private lateinit var useCase: UpsertOcupacionUseCase
    private lateinit var repository: OcupacionRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = UpsertOcupacionUseCase(repository)
    }

    @Test
    fun `invoke guarda ocupacion con datos validos`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "Desarrollador", sueldo = 50000.0)
        coEvery { repository.existsByDescripcion("Desarrollador", 0) } returns false
        coEvery { repository.upsert(ocupacion) } returns 0

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull())
        coVerify { repository.upsert(ocupacion) }
    }

    @Test
    fun `invoke actualiza ocupacion existente con datos validos`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 3, descripcion = "Diseñador", sueldo = 35000.0)
        coEvery { repository.existsByDescripcion("Diseñador", 3) } returns false
        coEvery { repository.upsert(ocupacion) } returns 3

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull())
        coVerify { repository.upsert(ocupacion) }
    }

    @Test
    fun `invoke falla cuando descripcion esta vacia`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "", sueldo = 50000.0)

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("La descripción no puede estar vacía", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando descripcion esta en blanco`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "   ", sueldo = 50000.0)

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La descripción no puede estar vacía", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando descripcion tiene menos de 3 caracteres`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "Ab", sueldo = 50000.0)

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La descripción debe tener al menos 3 caracteres", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando descripcion excede 100 caracteres`() = runTest {
        // Given
        val descripcionLarga = "A".repeat(101)
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = descripcionLarga, sueldo = 50000.0)

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La descripción no puede exceder los 100 caracteres", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke acepta descripcion con exactamente 3 caracteres`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "CEO", sueldo = 50000.0)
        coEvery { repository.existsByDescripcion("CEO", 0) } returns false
        coEvery { repository.upsert(ocupacion) } returns 0

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke acepta descripcion con exactamente 100 caracteres`() = runTest {
        // Given
        val descripcion100 = "A".repeat(100)
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = descripcion100, sueldo = 50000.0)
        coEvery { repository.existsByDescripcion(descripcion100, 0) } returns false
        coEvery { repository.upsert(ocupacion) } returns 0

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke falla cuando sueldo es cero`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "Contador", sueldo = 0.0)

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El sueldo debe ser mayor a 0", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando sueldo es negativo`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "Contador", sueldo = -100.0)

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El sueldo debe ser mayor a 0", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando sueldo excede 999999 99`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "Contador", sueldo = 1_000_000.0)

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El sueldo no puede exceder 999,999.99", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke acepta sueldo en el limite maximo permitido`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "Contador", sueldo = 999_999.99)
        coEvery { repository.existsByDescripcion("Contador", 0) } returns false
        coEvery { repository.upsert(ocupacion) } returns 0

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke falla cuando descripcion ya existe en otra ocupacion`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "Médico", sueldo = 80000.0)
        coEvery { repository.existsByDescripcion("Médico", 0) } returns true

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Ya existe una ocupación con esta descripción", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke no detecta duplicado al editar la misma ocupacion`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 2, descripcion = "Médico", sueldo = 80000.0)
        coEvery { repository.existsByDescripcion("Médico", 2) } returns false
        coEvery { repository.upsert(ocupacion) } returns 2

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.existsByDescripcion("Médico", 2) }
    }
}