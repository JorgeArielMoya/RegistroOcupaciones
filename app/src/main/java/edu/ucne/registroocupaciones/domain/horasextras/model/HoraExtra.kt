package edu.ucne.registroocupaciones.domain.horasextras.model

import java.time.LocalDate

data class HoraExtra (
    val horaExtraId: Int = 0,
    val empleadoId: Int = 0,
    val fechaDesde: LocalDate = LocalDate.now(),
    val fechaHasta: LocalDate = LocalDate.now(),
    val horasTotales: Double = 0.0,
    val horasNocturnas: Double = 0.0
)