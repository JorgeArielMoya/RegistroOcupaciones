package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

import com.google.common.truth.Truth.assertThat
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetOcupacionUseCaseTest {
    private lateinit var repository: OcupacionRepository
    private lateinit var useCase: GetOcupacionUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetOcupacionUseCase(repository)
    }

    @Test
    fun `returns ocupacion when repository finds it`() = runTest {
        val ocupacion = Ocupacion(1, "Desarrollador", 50000.0)
        coEvery { repository.getOcupacion(1) } returns ocupacion

        val result = useCase(1)

        assertThat(result).isEqualTo(ocupacion)
    }

    @Test
    fun `returns null when repository returns null`() = runTest {
        coEvery { repository.getOcupacion(999) } returns null

        val result = useCase(999)

        assertThat(result).isNull()
    }
}