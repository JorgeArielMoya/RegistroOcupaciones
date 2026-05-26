package edu.ucne.registroocupaciones.domain.horasextras.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ObserveHoraExtraUseCaseTest {
    private lateinit var repository: HoraExtraRepository
    private lateinit var useCase: ObserveHoraExtraUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ObserveHoraExtraUseCase(repository)
    }

    @Test
    fun `emits lists from repository`() = runTest {
        val shared = MutableSharedFlow<List<HoraExtra>>()
        every { repository.observeAll() } returns shared

        val job = launch {
            useCase().test {
                shared.emit(listOf(
                    HoraExtra(
                        horaExtraId = 1,
                        empleadoId = 1,
                        fechaDesde = LocalDate.of(2026, 5, 19),
                        fechaHasta = LocalDate.of(2026, 5, 25),
                        horasTotales = 52.0,
                        horasNocturnas = 0.0
                    )
                ))
                assertThat(awaitItem()).containsExactly(
                    HoraExtra(
                        horaExtraId = 1,
                        empleadoId = 1,
                        fechaDesde = LocalDate.of(2026, 5, 19),
                        fechaHasta = LocalDate.of(2026, 5, 25),
                        horasTotales = 52.0,
                        horasNocturnas = 0.0
                    )
                )

                shared.emit(listOf(
                    HoraExtra(
                        horaExtraId = 2,
                        empleadoId = 2,
                        fechaDesde = LocalDate.of(2026, 5, 19),
                        fechaHasta = LocalDate.of(2026, 5, 25),
                        horasTotales = 75.0,
                        horasNocturnas = 10.0
                    ),
                    HoraExtra(
                        horaExtraId = 3,
                        empleadoId = 1,
                        fechaDesde = LocalDate.of(2026, 5, 12),
                        fechaHasta = LocalDate.of(2026, 5, 18),
                        horasTotales = 80.0,
                        horasNocturnas = 0.0
                    )
                ))
                assertThat(awaitItem()).containsExactly(
                    HoraExtra(
                        horaExtraId = 2,
                        empleadoId = 2,
                        fechaDesde = LocalDate.of(2026, 5, 19),
                        fechaHasta = LocalDate.of(2026, 5, 25),
                        horasTotales = 75.0,
                        horasNocturnas = 10.0
                    ),
                    HoraExtra(
                        horaExtraId = 3,
                        empleadoId = 1,
                        fechaDesde = LocalDate.of(2026, 5, 12),
                        fechaHasta = LocalDate.of(2026, 5, 18),
                        horasTotales = 80.0,
                        horasNocturnas = 0.0
                    )
                )

                cancelAndIgnoreRemainingEvents()
            }
        }
        job.join()
    }

    @Test
    fun `emits empty list when repository has no horas extras`() = runTest {
        val shared = MutableSharedFlow<List<HoraExtra>>()
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