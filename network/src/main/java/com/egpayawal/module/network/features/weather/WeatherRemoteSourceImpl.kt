package com.egpayawal.module.network.features.weather

import com.egpayawal.module.domain.models.weather.WeatherData
import com.egpayawal.module.network.BaseplateApiServices
import com.egpayawal.module.network.utils.UNIT_METRIC
import javax.inject.Inject

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
class WeatherRemoteSourceImpl @Inject constructor(
    private val api: BaseplateApiServices
) : WeatherRemoteSource {

    override suspend fun getWeatherByLocation(lat: String, lon: String, appId: String): WeatherData {
        return api.getWeatherByLocation(lat, lon, appId, UNIT_METRIC).toDomain()
    }
}
