package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class UpsertEmpleadoUseCaseTest {
    private lateinit var useCase: UpsertEmpleadoUseCase
    private lateinit var repository: EmpleadoRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = UpsertEmpleadoUseCase(repository)
    }

    @Test
    fun `invoke guarda empleado con datos validos`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Juan Pérez",
            sexo = "Masculino",
            sueldo = 45000.0
        )
        coEvery { repository.upsert(empleado) } returns 0

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull())
        coVerify { repository.upsert(empleado) }
    }

    @Test
    fun `invoke actualiza empleado existente con datos validos`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 5,
            fechaIngreso = LocalDate.of(2023, 6, 10),
            nombres = "María López",
            sexo = "Femenino",
            sueldo = 60000.0
        )
        coEvery { repository.upsert(empleado) } returns 5

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrNull())
        coVerify { repository.upsert(empleado) }
    }

    @Test
    fun `invoke falla cuando nombres esta vacio`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "",
            sexo = "Masculino",
            sueldo = 45000.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre no puede estar vacío", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando nombres esta en blanco`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "   ",
            sexo = "Masculino",
            sueldo = 45000.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre no puede estar vacío", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando nombres tiene menos de 3 caracteres`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Jo",
            sexo = "Masculino",
            sueldo = 45000.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre debe tener al menos 3 caracteres", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando nombres excede 100 caracteres`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "A".repeat(101),
            sexo = "Masculino",
            sueldo = 45000.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre no puede exceder los 100 caracteres", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke acepta nombres con exactamente 3 caracteres`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Ana",
            sexo = "Femenino",
            sueldo = 45000.0
        )
        coEvery { repository.upsert(empleado) } returns 0

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke acepta nombres con exactamente 100 caracteres`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "A".repeat(100),
            sexo = "Masculino",
            sueldo = 45000.0
        )
        coEvery { repository.upsert(empleado) } returns 0

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke falla cuando sexo esta vacio`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Juan Pérez",
            sexo = "",
            sueldo = 45000.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Debe seleccionar una opción", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando sexo esta en blanco`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Juan Pérez",
            sexo = "   ",
            sueldo = 45000.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Debe seleccionar una opción", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando sueldo es cero`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Juan Pérez",
            sexo = "Masculino",
            sueldo = 0.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El sueldo debe ser mayor a 0", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando sueldo es negativo`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Juan Pérez",
            sexo = "Masculino",
            sueldo = -500.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El sueldo debe ser mayor a 0", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla cuando sueldo excede 999999 99`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Juan Pérez",
            sexo = "Masculino",
            sueldo = 1_000_000.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El sueldo no puede exceder 999,999.99", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke acepta sueldo en el limite maximo permitido`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Juan Pérez",
            sexo = "Masculino",
            sueldo = 999_999.99
        )
        coEvery { repository.upsert(empleado) } returns 0

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke valida nombres antes que sexo`() = runTest {
        // Given — ambos campos inválidos, debe fallar por nombres primero
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "",
            sexo = "",
            sueldo = 45000.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertEquals("El nombre no puede estar vacío", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke valida sexo antes que sueldo`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = LocalDate.of(2024, 1, 15),
            nombres = "Juan Pérez",
            sexo = "",
            sueldo = -100.0
        )

        // When
        val result = useCase(empleado)

        // Then
        assertEquals("Debe seleccionar una opción", result.exceptionOrNull()?.message)
    }
}