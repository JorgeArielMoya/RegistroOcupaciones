package edu.ucne.registroocupaciones.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.registroocupaciones.data.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.local.dao.OcupacionDao
import edu.ucne.registroocupaciones.data.local.entities.EmpleadoEntity
import edu.ucne.registroocupaciones.data.local.entities.OcupacionEntity

@TypeConverters(Converters::class)
@Database(
    entities = [OcupacionEntity::class, EmpleadoEntity::class],
    version = 2,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun ocupacionDao () : OcupacionDao
    abstract fun empleadoDao () : EmpleadoDao
}