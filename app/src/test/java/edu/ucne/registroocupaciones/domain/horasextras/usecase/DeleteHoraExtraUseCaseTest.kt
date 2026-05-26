package edu.ucne.registroocupaciones.domain.horasextras.usecase

import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DeleteHoraExtraUseCaseTest {
    private lateinit var repository: HoraExtraRepository
    private lateinit var useCase: DeleteHoraExtraUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = DeleteHoraExtraUseCase(repository)
    }

    @Test
    fun `calls repository deleteHoraExtra with id`() = runTest {
        coEvery { repository.deleteHoraExtra(1) } just Runs
        useCase(1)
        coVerify { repository.deleteHoraExtra(1) }
    }

    @Test
    fun `calls repository deleteHoraExtra with any valid id`() = runTest {
        coEvery { repository.deleteHoraExtra(99) } just Runs
        useCase(99)
        coVerify { repository.deleteHoraExtra(99) }
    }
}