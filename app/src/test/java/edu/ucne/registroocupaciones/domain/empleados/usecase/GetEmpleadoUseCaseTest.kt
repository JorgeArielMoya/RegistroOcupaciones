package edu.ucne.registroocupaciones.domain.empleados.usecase

import com.google.common.truth.Truth.assertThat
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetEmpleadoUseCaseTest {
    private lateinit var repository: EmpleadoRepository
    private lateinit var useCase: GetEmpleadoUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetEmpleadoUseCase(repository)
    }

    @Test
    fun `returns empleado when repository finds it`() = runTest {
        val empleado = Empleado(1, LocalDate.of(2024, 1, 15), "Juan Pérez", "M", 45000.0)
        coEvery { repository.getEmpleado(1) } returns empleado

        val result = useCase(1)

        assertThat(result).isEqualTo(empleado)
    }

    @Test
    fun `returns null when repository returns null`() = runTest {
        coEvery { repository.getEmpleado(999) } returns null

        val result = useCase(999)

        assertThat(result).isNull()
    }
}