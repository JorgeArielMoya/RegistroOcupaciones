package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DeleteEmpleadoUseCaseTest {
    private lateinit var repository: EmpleadoRepository
    private lateinit var useCase: DeleteEmpleadoUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = DeleteEmpleadoUseCase(repository)
    }

    @Test
    fun `calls repository delete with id`() = runTest {
        coEvery { repository.delete(1) } just runs
        useCase(1)
        coVerify { repository.delete(1) }
    }

    @Test
    fun `calls repository delete with any valid id`() = runTest {
        coEvery { repository.delete(99) } just runs
        useCase(99)
        coVerify { repository.delete(99) }
    }
}