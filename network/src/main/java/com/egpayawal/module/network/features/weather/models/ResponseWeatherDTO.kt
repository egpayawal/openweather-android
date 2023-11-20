package com.egpayawal.module.network.features.weather.models

import com.egpayawal.module.domain.models.weather.WeatherData

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
class ResponseWeatherDTO(
    private val coord: CoordinatesDTO? = null,
    private val weather: List<WeatherDTO>? = null,
    private val base: String? = null,
    private val main: MainDTO? = null,
    private val visibility: Int,
    private val wind: WindDTO? = null,
    private val clouds: CloudsDTO? = null,
    private val dt: Long = -1L,
    private val sys: SysDTO? = null,
    private val timezone: Long = -1L,
    private val id: Long = -1L,
    private val name: String? = null,
    private val cod: Int
) {

    fun toDomain(): WeatherData {
        return with(this) {
            WeatherData(
                coord = coord?.toDomain(),
                weather = weather?.map { it.toDomain() },
                base = base.orEmpty(),
                main = main?.toDomain(),
                visibility = visibility,
                wind = wind?.toDomain(),
                clouds = clouds?.toDomain(),
                dt = dt,
                sys = sys?.toDomain(),
                timezone = timezone,
                id = id,
                name = name.orEmpty(),
                cod = cod
            )
        }
    }
}
