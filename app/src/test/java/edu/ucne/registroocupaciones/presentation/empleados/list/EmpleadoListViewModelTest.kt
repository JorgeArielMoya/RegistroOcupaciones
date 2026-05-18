package edu.ucne.registroocupaciones.presentation.empleados.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.usecase.DeleteEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.ObserveEmpleadoUseCase
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
import org.threeten.bp.LocalDate

@ExperimentalCoroutinesApi
class EmpleadoListViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EmpleadoListViewModel
    private lateinit var observeEmpleadosUseCase: ObserveEmpleadoUseCase
    private lateinit var deleteEmpleadoUseCase: DeleteEmpleadoUseCase

    @Before
    fun setup() {
        observeEmpleadosUseCase = mockk()
        deleteEmpleadoUseCase = mockk()

        every { observeEmpleadosUseCase() } returns flowOf(emptyList())

        viewModel = EmpleadoListViewModel(
            observeEmpleadosUseCase,
            deleteEmpleadoUseCase
        )
    }

    @Test
    fun `loadEmpleados carga lista correctamente`() = runTest {
        // Given
        val empleados = listOf(
            Empleado(1, LocalDate.of(2024, 1, 15), "Juan Pérez", "M", 45000.0),
            Empleado(2, LocalDate.of(2023, 6, 10), "María López", "F", 60000.0)
        )
        every { observeEmpleadosUseCase() } returns flowOf(empleados)

        // When
        viewModel = EmpleadoListViewModel(observeEmpleadosUseCase, deleteEmpleadoUseCase)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(2, viewModel.state.value.empleados.size)
        assertEquals("Juan Pérez", viewModel.state.value.empleados[0].nombres)
        assertEquals("María López", viewModel.state.value.empleados[1].nombres)
    }

    @Test
    fun `loadEmpleados muestra lista vacia correctamente`() = runTest {
        // Given
        every { observeEmpleadosUseCase() } returns flowOf(emptyList())

        // When
        viewModel = EmpleadoListViewModel(observeEmpleadosUseCase, deleteEmpleadoUseCase)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.empleados.isEmpty())
    }

    @Test
    fun `onEvent Delete elimina empleado y muestra mensaje`() = runTest {
        // Given
        val empleadoId = 1
        coEvery { deleteEmpleadoUseCase(empleadoId) } just Runs

        // When
        viewModel.onEvent(EmpleadoListUiEvent.Delete(empleadoId))
        advanceUntilIdle()

        // Then
        coVerify { deleteEmpleadoUseCase(empleadoId) }
        assertEquals("Eliminado", viewModel.state.value.message)
    }

    @Test
    fun `onEvent CreateNew activa navegacion a crear`() {
        // When
        viewModel.onEvent(EmpleadoListUiEvent.CreateNew)

        // Then
        assertTrue(viewModel.state.value.navigateToCreate)
    }

    @Test
    fun `onEvent Edit activa navegacion a editar con id`() {
        // Given
        val empleadoId = 5

        // When
        viewModel.onEvent(EmpleadoListUiEvent.Edit(empleadoId))

        // Then
        assertEquals(empleadoId, viewModel.state.value.navigateToEditId)
    }

    @Test
    fun `onEvent ShowMessage actualiza mensaje en estado`() {
        // When
        viewModel.onEvent(EmpleadoListUiEvent.ShowMessage("Operación exitosa"))

        // Then
        assertEquals("Operación exitosa", viewModel.state.value.message)
    }

    @Test
    fun `onEvent ClearMessage limpia mensaje`() {
        // Given
        viewModel.onEvent(EmpleadoListUiEvent.ShowMessage("Mensaje"))

        // When
        viewModel.onEvent(EmpleadoListUiEvent.ClearMessage)

        // Then
        assertNull(viewModel.state.value.message)
    }

    @Test
    fun `onEvent Load recarga lista`() = runTest {
        // Given
        val empleados = listOf(
            Empleado(1, LocalDate.of(2024, 1, 15), "Carlos Ruiz", "M", 35000.0)
        )
        every { observeEmpleadosUseCase() } returns flowOf(empleados)

        // When
        viewModel.onEvent(EmpleadoListUiEvent.Load)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.state.value.empleados.size)
        assertEquals("Carlos Ruiz", viewModel.state.value.empleados[0].nombres)
    }

    @Test
    fun `onEvent Refresh recarga lista`() = runTest {
        // Given
        val empleados = listOf(
            Empleado(2, LocalDate.of(2023, 3, 20), "Ana García", "F", 48000.0)
        )
        every { observeEmpleadosUseCase() } returns flowOf(empleados)

        // When
        viewModel.onEvent(EmpleadoListUiEvent.Refresh)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.state.value.empleados.size)
        assertEquals("Ana García", viewModel.state.value.empleados[0].nombres)
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