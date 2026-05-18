package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObserveOcupacionesUseCaseTest {
    private lateinit var repository: OcupacionRepository
    private lateinit var useCase: ObserveOcupacionUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ObserveOcupacionUseCase(repository)
    }

    @Test
    fun `emits lists from repository`() = runTest {
        val shared = MutableSharedFlow<List<Ocupacion>>()
        every { repository.observeOcupaciones() } returns shared

        val job = launch {
            useCase().test {
                shared.emit(listOf(Ocupacion(1, "Desarrollador", 50000.0)))
                assertThat(awaitItem()).containsExactly(Ocupacion(1, "Desarrollador", 50000.0))

                shared.emit(listOf(
                    Ocupacion(2, "Diseñador", 35000.0),
                    Ocupacion(3, "Contador", 40000.0)
                ))
                assertThat(awaitItem()).containsExactly(
                    Ocupacion(2, "Diseñador", 35000.0),
                    Ocupacion(3, "Contador", 40000.0)
                )

                cancelAndIgnoreRemainingEvents()
            }
        }
        job.join()
    }

    @Test
    fun `emits empty list when repository has no ocupaciones`() = runTest {
        val shared = MutableSharedFlow<List<Ocupacion>>()
        every { repository.observeOcupaciones() } returns shared

        val job = launch {
            useCase().test {
                shared.emit(emptyList())
                assertThat(awaitItem()).isEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }
        job.join()
    }
}