package edu.ucne.registroocupaciones.data.ocupaciones.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.registroocupaciones.data.ocupaciones.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.ocupaciones.local.dao.OcupacionDao
import edu.ucne.registroocupaciones.data.ocupaciones.local.entities.EmpleadoEntity
import edu.ucne.registroocupaciones.data.ocupaciones.local.entities.OcupacionEntity

@Database(
    entities = [OcupacionEntity::class, EmpleadoEntity::class],
    version = 2,
    exportSchema = false
)

abstract class OcupacionDatabase : RoomDatabase() {
    abstract fun ocupacionDao () : OcupacionDao
    abstract fun empleadoDao () : EmpleadoDao
}