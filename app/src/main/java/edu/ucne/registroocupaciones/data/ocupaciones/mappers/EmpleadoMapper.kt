package edu.ucne.registroocupaciones.data.ocupaciones.mappers

import edu.ucne.registroocupaciones.data.ocupaciones.local.entities.EmpleadoEntity
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import org.threeten.bp.LocalDate

fun EmpleadoEntity.toDomain () : Empleado = Empleado (
    empleadoId = empleadoId,
    fechaIngreso = LocalDate.parse(fechaIngreso),
    nombres = nombres,
    sexo = sexo,
    sueldo = sueldo
)

fun Empleado.toEntity () : EmpleadoEntity = EmpleadoEntity (
    empleadoId = empleadoId,
    fechaIngreso = fechaIngreso.toString(),
    nombres = nombres,
    sexo = sexo,
    sueldo = sueldo
)