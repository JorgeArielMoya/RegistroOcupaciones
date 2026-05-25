package edu.ucne.registroocupaciones.data.mappers

import edu.ucne.registroocupaciones.data.local.entities.HoraExtraEntity
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra

fun HoraExtraEntity.toDomain () : HoraExtra = HoraExtra(
    horaExtraId = horaExtraId,
    empleadoId = empleadoId,
    fechaDesde = fechaDesde,
    fechaHasta = fechaHasta,
    horasTotales = horasTotales,
    horasNocturnas = horasNocturnas
)

fun HoraExtra.toEntity () : HoraExtraEntity = HoraExtraEntity(
    horaExtraId = horaExtraId,
    empleadoId = empleadoId,
    fechaDesde = fechaDesde,
    fechaHasta = fechaHasta,
    horasTotales = horasTotales,
    horasNocturnas = horasNocturnas
)