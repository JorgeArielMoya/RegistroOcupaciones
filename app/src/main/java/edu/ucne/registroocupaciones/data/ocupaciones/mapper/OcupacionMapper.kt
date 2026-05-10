package edu.ucne.registroocupaciones.data.ocupaciones.mapper

import edu.ucne.registroocupaciones.data.ocupaciones.local.OcupacionEntity
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