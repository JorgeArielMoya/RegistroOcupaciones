package edu.ucne.registroocupaciones.data.ocupaciones.mappers

import edu.ucne.registroocupaciones.data.ocupaciones.local.entities.EmpleadoEntity
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado

fun EmpleadoEntity.toDomain () : Empleado = Empleado (
    empleadoId = empleadoId,
    fechaIngreso = fechaIngreso,
    nombres = nombres,
    sexo = sexo,
    sueldo = sueldo
)

fun Empleado.toEntity () : EmpleadoEntity = EmpleadoEntity (
    empleadoId = empleadoId,
    fechaIngreso = fechaIngreso,
    nombres = nombres,
    sexo = sexo,
    sueldo = sueldo
)