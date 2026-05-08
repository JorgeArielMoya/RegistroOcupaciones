package edu.ucne.registroocupaciones.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.registroocupaciones.data.local.entity.OcupacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OcupacionDao {
    @Query("Select * from ocupaciones order by ocupacionId Desc")
    fun observeAll () : Flow<List<OcupacionEntity>>

    @Query("Select * from ocupaciones where ocupacionId = :id")
    suspend fun getById (id : Int) : OcupacionEntity?

    @Upsert
    suspend fun upsert (ocupacion : OcupacionEntity)

    @Delete
    suspend fun delete (ocupacion : OcupacionEntity)

    @Query("Select * from ocupaciones where ocupacionId = :id")
    suspend fun deleteById (id : Int)
}