package edu.ucne.registroocupaciones.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.registroocupaciones.data.repository.EmpleadoRepositoryImpl
import edu.ucne.registroocupaciones.data.repository.OcupacionRepositoryImpl
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindOcupacionRepository(
        impl: OcupacionRepositoryImpl
    ): OcupacionRepository

    @Binds
    @Singleton
    abstract fun bindEmpleadoRepository(
        impl: EmpleadoRepositoryImpl
    ): EmpleadoRepository
}
