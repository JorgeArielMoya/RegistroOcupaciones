package edu.ucne.registroocupaciones.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.registroocupaciones.data.ocupaciones.local.OcupacionDao
import edu.ucne.registroocupaciones.data.ocupaciones.local.OcupacionDatabase
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOcupacionDatabase(
        @ApplicationContext context: Context
    ): OcupacionDatabase {
        return Room.databaseBuilder(
            context,
            OcupacionDatabase::class.java,
            "OcupacionDatabase"
        ).build()
    }

    @Provides
    @Singleton
    fun provideOcupacionDao(database: OcupacionDatabase): OcupacionDao {
        return database.ocupacionDao()
    }
}