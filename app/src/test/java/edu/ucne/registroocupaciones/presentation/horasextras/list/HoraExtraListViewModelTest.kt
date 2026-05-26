package edu.ucne.registroocupaciones.presentation.horasextras.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.usecase.ObserveEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.usecase.DeleteHoraExtraUseCase
import edu.ucne.registroocupaciones.domain.horasextras.usecase.ObserveHoraExtraUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.time.LocalDate

@ExperimentalCoroutinesApi
class HoraExtraListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HoraExtraListViewModel
    private lateinit var observeHorasExtrasUseCase: ObserveHoraExtraUseCase
    private lateinit var deleteHoraExtraUseCase: DeleteHoraExtraUseCase
    private lateinit var observeEmpleadosUseCase: ObserveEmpleadoUseCase

    @Before
    fun setup() {
        observeHorasExtrasUseCase = mockk()
        deleteHoraExtraUseCase = mockk()
        observeEmpleadosUseCase = mockk()

        every { observeHorasExtrasUseCase() } returns flowOf(emptyList())
        every { observeEmpleadosUseCase() } returns flowOf(emptyList())

        viewModel = HoraExtraListViewModel(
            observeHorasExtrasUseCase,
            deleteHoraExtraUseCase,
            observeEmpleadosUseCase
        )
    }

    @Test
    fun `loadHorasExtras carga lista correctamente`() = runTest {
        // Given
        val horasExtras = listOf(
            HoraExtra(
                horaExtraId = 1,
                empleadoId = 1,
                fechaDesde = LocalDate.of(2026, 5, 19),
                fechaHasta = LocalDate.of(2026, 5, 25),
                horasTotales = 52.0,
                horasNocturnas = 0.0
            ),
            HoraExtra(
                horaExtraId = 2,
                empleadoId = 2,
                fechaDesde = LocalDate.of(2026, 5, 19),
                fechaHasta = LocalDate.of(2026, 5, 25),
                horasTotales = 75.0,
                horasNocturnas = 10.0
            )
        )
        every { observeHorasExtrasUseCase() } returns flowOf(horasExtras)

        // When
        viewModel = HoraExtraListViewModel(
            observeHorasExtrasUseCase,
            deleteHoraExtraUseCase,
            observeEmpleadosUseCase
        )
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(2, viewModel.state.value.horasExtras.size)
        assertEquals(52.0, viewModel.state.value.horasExtras[0].horasTotales, 0.0)
        assertEquals(75.0, viewModel.state.value.horasExtras[1].horasTotales, 0.0)
        assertEquals(10.0, viewModel.state.value.horasExtras[1].horasNocturnas, 0.0)
    }

    @Test
    fun `loadHorasExtras muestra lista vacia correctamente`() = runTest {
        // Given
        every { observeHorasExtrasUseCase() } returns flowOf(emptyList())

        // When
        viewModel = HoraExtraListViewModel(
            observeHorasExtrasUseCase,
            deleteHoraExtraUseCase,
            observeEmpleadosUseCase
        )
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.horasExtras.isEmpty())
    }

    @Test
    fun `loadEmpleados carga lista de empleados correctamente`() = runTest {
        // Given
        val empleados = listOf(
            Empleado(1, LocalDate.of(2024, 1, 15), "Juan Pérez", "M", 30000.0),
            Empleado(2, LocalDate.of(2023, 6, 10), "María López", "F", 45000.0)
        )
        every { observeEmpleadosUseCase() } returns flowOf(empleados)

        // When
        viewModel = HoraExtraListViewModel(
            observeHorasExtrasUseCase,
            deleteHoraExtraUseCase,
            observeEmpleadosUseCase
        )
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.state.value.empleados.size)
        assertEquals("Juan Pérez", viewModel.state.value.empleados[0].nombres)
        assertEquals(30000.0, viewModel.state.value.empleados[0].sueldo, 0.0)
    }

    @Test
    fun `onEvent Delete elimina hora extra y muestra mensaje`() = runTest {
        // Given
        val horaExtraId = 1
        coEvery { deleteHoraExtraUseCase(horaExtraId) } just Runs

        // When
        viewModel.onEvent(HoraExtraListUiEvent.Delete(horaExtraId))
        advanceUntilIdle()

        // Then
        coVerify { deleteHoraExtraUseCase(horaExtraId) }
        assertEquals("Eliminado", viewModel.state.value.message)
    }

    @Test
    fun `onEvent CreateNew activa navegacion a crear`() {
        // When
        viewModel.onEvent(HoraExtraListUiEvent.CreateNew)

        // Then
        assertTrue(viewModel.state.value.navigateToCreate)
    }

    @Test
    fun `onEvent Edit activa navegacion a editar con id`() {
        // Given
        val horaExtraId = 3

        // When
        viewModel.onEvent(HoraExtraListUiEvent.Edit(horaExtraId))

        // Then
        assertEquals(horaExtraId, viewModel.state.value.navigateToEditId)
    }

    @Test
    fun `onEvent ShowMessage actualiza mensaje en estado`() {
        // When
        viewModel.onEvent(HoraExtraListUiEvent.ShowMessage("Operación exitosa"))

        // Then
        assertEquals("Operación exitosa", viewModel.state.value.message)
    }

    @Test
    fun `onEvent ClearMessage limpia mensaje`() {
        // Given
        viewModel.onEvent(HoraExtraListUiEvent.ShowMessage("Mensaje"))

        // When
        viewModel.onEvent(HoraExtraListUiEvent.ClearMessage)

        // Then
        assertNull(viewModel.state.value.message)
    }

    @Test
    fun `onEvent Load recarga lista`() = runTest {
        // Given
        val horasExtras = listOf(
            HoraExtra(
                horaExtraId = 1,
                empleadoId = 1,
                fechaDesde = LocalDate.of(2026, 5, 19),
                fechaHasta = LocalDate.of(2026, 5, 25),
                horasTotales = 68.0,
                horasNocturnas = 0.0
            )
        )
        every { observeHorasExtrasUseCase() } returns flowOf(horasExtras)

        // When
        viewModel.onEvent(HoraExtraListUiEvent.Load)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.state.value.horasExtras.size)
        assertEquals(68.0, viewModel.state.value.horasExtras[0].horasTotales, 0.0)
    }

    @Test
    fun `onEvent Refresh recarga lista`() = runTest {
        // Given
        val horasExtras = listOf(
            HoraExtra(
                horaExtraId = 2,
                empleadoId = 1,
                fechaDesde = LocalDate.of(2026, 5, 12),
                fechaHasta = LocalDate.of(2026, 5, 18),
                horasTotales = 80.0,   // 24h al 35% + 12h al 100%
                horasNocturnas = 5.0
            )
        )
        every { observeHorasExtrasUseCase() } returns flowOf(horasExtras)

        // When
        viewModel.onEvent(HoraExtraListUiEvent.Refresh)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.state.value.horasExtras.size)
        assertEquals(80.0, viewModel.state.value.horasExtras[0].horasTotales, 0.0)
        assertEquals(5.0, viewModel.state.value.horasExtras[0].horasNocturnas, 0.0)
    }
}

@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}