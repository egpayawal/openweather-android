package com.egpayawal.module.data.features.weather

import com.egpayawal.module.domain.models.weather.WeatherData
import com.egpayawal.module.network.features.weather.WeatherRemoteSource
import javax.inject.Inject

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
class WeatherRepositoryImpl @Inject constructor(
    private val weatherRemoteSource: WeatherRemoteSource
) : WeatherRepository {

    override suspend fun getWeatherByLocation(lat: String, lon: String, appId: String): WeatherData {
        return weatherRemoteSource.getWeatherByLocation(
            lat = lat,
            lon = lon,
            appId = appId
        )
    }
}
