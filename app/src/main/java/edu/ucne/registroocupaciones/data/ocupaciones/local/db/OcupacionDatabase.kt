package edu.ucne.registroocupaciones.data.ocupaciones.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.registroocupaciones.data.ocupaciones.local.dao.OcupacionDao
import edu.ucne.registroocupaciones.data.ocupaciones.local.entities.OcupacionEntity

@Database(
    entities = [OcupacionEntity::class],
    version = 1,
    exportSchema = false
)

abstract class OcupacionDatabase : RoomDatabase() {
    abstract fun ocupacionDao () : OcupacionDao
}