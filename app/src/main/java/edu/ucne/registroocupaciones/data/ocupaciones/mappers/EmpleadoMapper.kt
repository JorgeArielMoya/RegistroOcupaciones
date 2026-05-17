package edu.ucne.registroocupaciones.data.ocupaciones.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import edu.ucne.registroocupaciones.data.ocupaciones.local.entities.EmpleadoEntity
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import java.time.LocalDate.parse

@RequiresApi(Build.VERSION_CODES.O)
fun EmpleadoEntity.toDomain () : Empleado = Empleado (
    empleadoId = empleadoId,
    fechaIngreso = parse(fechaIngreso),
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