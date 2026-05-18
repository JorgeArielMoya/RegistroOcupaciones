package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DeleteOcupacionUseCaseTest {
    private lateinit var repository: OcupacionRepository
    private lateinit var useCase: DeleteOcupacionUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = DeleteOcupacionUseCase(repository)
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