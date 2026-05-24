package edu.ucne.registroocupaciones.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.registroocupaciones.data.local.entities.EmpleadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpleadoDao {
    @Upsert
    suspend fun upsert (entity : EmpleadoEntity)

    @Delete
    suspend fun delete (entity: EmpleadoEntity)

    @Query("Select * from empleados order by empleadoId desc")
    fun observeAll () : Flow<List<EmpleadoEntity>>

    @Query("Select * from empleados where empleadoId = :id")
    suspend fun getById (id: Int) : EmpleadoEntity?

    @Query("Delete from empleados where empleadoId = :id")
    suspend fun deleteById (id : Int)
}