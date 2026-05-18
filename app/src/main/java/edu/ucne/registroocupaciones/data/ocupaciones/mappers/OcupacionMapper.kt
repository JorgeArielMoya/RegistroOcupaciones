package edu.ucne.registroocupaciones.data.ocupaciones.mappers

import edu.ucne.registroocupaciones.data.ocupaciones.local.entities.OcupacionEntity
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion

fun OcupacionEntity.toDomain(): Ocupacion = Ocupacion(
    ocupacionId = ocupacionId,
    descripcion = descripcion,
    sueldo = sueldo
)

fun Ocupacion.toEntity(): OcupacionEntity = OcupacionEntity(
    ocupacionId = ocupacionId,
    descripcion = descripcion,
    sueldo = sueldo
)