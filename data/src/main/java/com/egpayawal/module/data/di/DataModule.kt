package com.egpayawal.module.data.di

import com.egpayawal.module.data.features.song.SongRepository
import com.egpayawal.module.data.features.song.SongRepositoryImpl
import com.egpayawal.module.data.features.weather.WeatherRepository
import com.egpayawal.module.data.features.weather.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun providesSongRepository(impl: SongRepositoryImpl): SongRepository

    @Binds
    @Singleton
    fun providesWeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository
}
