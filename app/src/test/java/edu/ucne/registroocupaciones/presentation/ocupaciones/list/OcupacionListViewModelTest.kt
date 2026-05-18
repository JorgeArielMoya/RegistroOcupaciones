package edu.ucne.registroocupaciones.presentation.ocupaciones.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.DeleteOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.ObserveOcupacionUseCase
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

@ExperimentalCoroutinesApi
class OcupacionListViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: OcupacionListViewModel
    private lateinit var observeOcupacionesUseCase: ObserveOcupacionUseCase
    private lateinit var deleteOcupacionUseCase: DeleteOcupacionUseCase

    @Before
    fun setup() {
        observeOcupacionesUseCase = mockk()
        deleteOcupacionUseCase = mockk()

        every { observeOcupacionesUseCase() } returns flowOf(emptyList())

        viewModel = OcupacionListViewModel(
            observeOcupacionesUseCase,
            deleteOcupacionUseCase
        )
    }

    @Test
    fun `loadOcupaciones carga lista correctamente`() = runTest {
        // Given
        val ocupaciones = listOf(
            Ocupacion(1, "Desarrollador", 50000.0),
            Ocupacion(2, "Diseñador", 35000.0)
        )
        every { observeOcupacionesUseCase() } returns flowOf(ocupaciones)

        // When
        viewModel = OcupacionListViewModel(observeOcupacionesUseCase, deleteOcupacionUseCase)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(2, viewModel.state.value.ocupaciones.size)
        assertEquals("Desarrollador", viewModel.state.value.ocupaciones[0].descripcion)
        assertEquals("Diseñador", viewModel.state.value.ocupaciones[1].descripcion)
    }

    @Test
    fun `loadOcupaciones muestra lista vacia correctamente`() = runTest {
        // Given
        every { observeOcupacionesUseCase() } returns flowOf(emptyList())

        // When
        viewModel = OcupacionListViewModel(observeOcupacionesUseCase, deleteOcupacionUseCase)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.ocupaciones.isEmpty())
    }

    @Test
    fun `onEvent Delete elimina ocupacion y muestra mensaje`() = runTest {
        // Given
        val ocupacionId = 1
        coEvery { deleteOcupacionUseCase(ocupacionId) } just Runs

        // When
        viewModel.onEvent(OcupacionListUiEvent.Delete(ocupacionId))
        advanceUntilIdle()

        // Then
        coVerify { deleteOcupacionUseCase(ocupacionId) }
        assertEquals("Eliminado", viewModel.state.value.message)
    }

    @Test
    fun `onEvent CreateNew activa navegacion a crear`() {
        // When
        viewModel.onEvent(OcupacionListUiEvent.CreateNew)

        // Then
        assertTrue(viewModel.state.value.navigateToCreate)
    }

    @Test
    fun `onEvent Edit activa navegacion a editar con id`() {
        // Given
        val ocupacionId = 3

        // When
        viewModel.onEvent(OcupacionListUiEvent.Edit(ocupacionId))

        // Then
        assertEquals(ocupacionId, viewModel.state.value.navigateToEditId)
    }

    @Test
    fun `onEvent ShowMessage actualiza mensaje en estado`() {
        // When
        viewModel.onEvent(OcupacionListUiEvent.ShowMessage("Operación exitosa"))

        // Then
        assertEquals("Operación exitosa", viewModel.state.value.message)
    }

    @Test
    fun `onEvent ClearMessage limpia mensaje`() {
        // Given
        viewModel.onEvent(OcupacionListUiEvent.ShowMessage("Mensaje"))

        // When
        viewModel.onEvent(OcupacionListUiEvent.ClearMessage)

        // Then
        assertNull(viewModel.state.value.message)
    }

    @Test
    fun `onEvent Load recarga lista`() = runTest {
        // Given
        val ocupaciones = listOf(Ocupacion(1, "Contador", 40000.0))
        every { observeOcupacionesUseCase() } returns flowOf(ocupaciones)

        // When
        viewModel.onEvent(OcupacionListUiEvent.Load)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.state.value.ocupaciones.size)
        assertEquals("Contador", viewModel.state.value.ocupaciones[0].descripcion)
    }

    @Test
    fun `onEvent Refresh recarga lista`() = runTest {
        // Given
        val ocupaciones = listOf(Ocupacion(2, "Médico", 80000.0))
        every { observeOcupacionesUseCase() } returns flowOf(ocupaciones)

        // When
        viewModel.onEvent(OcupacionListUiEvent.Refresh)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.state.value.ocupaciones.size)
        assertEquals("Médico", viewModel.state.value.ocupaciones[0].descripcion)
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