package edu.ucne.registroocupaciones.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.registroocupaciones.data.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.local.dao.HoraExtraDao
import edu.ucne.registroocupaciones.data.local.dao.OcupacionDao
import edu.ucne.registroocupaciones.data.local.db.AppDatabase
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOcupacionDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ocupacion_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideOcupacionDao(database: AppDatabase): OcupacionDao {
        return database.ocupacionDao()
    }

    @Provides
    @Singleton
    fun provideEmpleadoDao(database: AppDatabase): EmpleadoDao {
        return database.empleadoDao()
    }

    @Provides
    @Singleton
    fun providesHoraExtraDao(database: AppDatabase) : HoraExtraDao{
        return database.horaExtraDao()
    }
}