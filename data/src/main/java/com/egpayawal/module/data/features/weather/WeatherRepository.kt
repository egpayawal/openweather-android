package com.egpayawal.module.data.features.weather

import com.egpayawal.module.domain.models.weather.WeatherData

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
interface WeatherRepository {

    suspend fun getWeatherByLocation(lat: String, lon: String, appId: String): WeatherData
}
