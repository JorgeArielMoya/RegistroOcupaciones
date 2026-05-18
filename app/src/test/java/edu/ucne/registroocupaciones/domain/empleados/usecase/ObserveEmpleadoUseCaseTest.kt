package edu.ucne.registroocupaciones.domain.empleados.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

class ObserveEmpleadosUseCaseTest {

    private lateinit var repository: EmpleadoRepository
    private lateinit var useCase: ObserveEmpleadoUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ObserveEmpleadoUseCase(repository)
    }

    @Test
    fun `emits lists from repository`() = runTest {
        val shared = MutableSharedFlow<List<Empleado>>()
        every { repository.observeAll() } returns shared

        val job = launch {
            useCase().test {
                shared.emit(listOf(
                    Empleado(1, LocalDate.of(2024, 1, 15), "Juan Pérez", "M", 45000.0)
                ))
                assertThat(awaitItem()).containsExactly(
                    Empleado(1, LocalDate.of(2024, 1, 15), "Juan Pérez", "M", 45000.0)
                )

                shared.emit(listOf(
                    Empleado(2, LocalDate.of(2023, 6, 10), "María López", "F", 60000.0),
                    Empleado(3, LocalDate.of(2022, 3, 5), "Carlos Ruiz", "M", 35000.0)
                ))
                assertThat(awaitItem()).containsExactly(
                    Empleado(2, LocalDate.of(2023, 6, 10), "María López", "F", 60000.0),
                    Empleado(3, LocalDate.of(2022, 3, 5), "Carlos Ruiz", "M", 35000.0)
                )

                cancelAndIgnoreRemainingEvents()
            }
        }
        job.join()
    }

    @Test
    fun `emits empty list when repository has no empleados`() = runTest {
        val shared = MutableSharedFlow<List<Empleado>>()
        every { repository.observeAll() } returns shared

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