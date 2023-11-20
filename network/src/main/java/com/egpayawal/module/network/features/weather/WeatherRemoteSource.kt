package com.egpayawal.module.network.features.weather

import com.egpayawal.module.domain.models.weather.WeatherData

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
interface WeatherRemoteSource {

    suspend fun getWeatherByLocation(lat: String, lon: String, appId: String): WeatherData
}
