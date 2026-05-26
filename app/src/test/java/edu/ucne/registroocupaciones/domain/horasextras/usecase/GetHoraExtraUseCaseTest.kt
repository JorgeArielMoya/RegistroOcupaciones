package edu.ucne.registroocupaciones.domain.horasextras.usecase

import com.google.common.truth.Truth.assertThat
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetHoraExtraUseCaseTest {
    private lateinit var repository: HoraExtraRepository
    private lateinit var useCase: GetHoraExtraUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetHoraExtraUseCase(repository)
    }

    @Test
    fun `returns horaExtra when repository finds it`() = runTest {
        val horaExtra = HoraExtra(
            horaExtraId = 1,
            empleadoId = 2,
            fechaDesde = LocalDate.of(2026, 5, 19),
            fechaHasta = LocalDate.of(2026, 5, 25),
            horasTotales = 52.0,
            horasNocturnas = 0.0
        )
        coEvery { repository.getHoraExtra(1) } returns horaExtra

        val result = useCase(1)

        assertThat(result).isEqualTo(horaExtra)
    }

    @Test
    fun `returns null when repository returns null`() = runTest {
        coEvery { repository.getHoraExtra(999) } returns null

        val result = useCase(999)

        assertThat(result).isNull()
    }
}