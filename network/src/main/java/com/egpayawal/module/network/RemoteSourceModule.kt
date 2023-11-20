package com.egpayawal.module.network

import com.egpayawal.module.network.features.song.SongRemoteSource
import com.egpayawal.module.network.features.song.SongRemoteSourceImpl
import com.egpayawal.module.network.features.weather.WeatherRemoteSource
import com.egpayawal.module.network.features.weather.WeatherRemoteSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteSourceModule {

//    @Binds
//    @Singleton
//    fun providesSongRemoteSource(impl: SongRemoteSourceImpl): SongRemoteSource

    @Provides
    @Singleton
    fun providesSongRemoteSource(apiServices: BaseplateApiServices): SongRemoteSource =
        SongRemoteSourceImpl(apiServices)

    @Provides
    @Singleton
    fun providesWeatherRemoteSource(apiServices: BaseplateApiServices): WeatherRemoteSource =
        WeatherRemoteSourceImpl(apiServices)
}
