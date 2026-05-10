package edu.ucne.registroocupaciones.data.ocupaciones.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [OcupacionEntity::class],
    version = 1,
    exportSchema = false
)

abstract class OcupacionDatabase : RoomDatabase() {
    abstract fun ocupacionDao () : OcupacionDao
}