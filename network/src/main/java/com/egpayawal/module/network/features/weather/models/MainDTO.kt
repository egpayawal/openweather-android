package com.egpayawal.module.network.features.weather.models

import com.egpayawal.module.domain.models.weather.Main
import com.google.gson.annotations.SerializedName

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
class MainDTO(
    private val temp: Double? = null,
    @SerializedName("feels_like")
    private val feelsLike: Double? = null,
    @SerializedName("temp_min")
    private val tempMin: Double? = null,
    @SerializedName("temp_max")
    private val tempMax: Double? = null,
    private val pressure: Int,
    private val humidity: Int,
    @SerializedName("sea_level")
    private val seaLevel: Int,
    @SerializedName("grnd_level")
    private val groundLevel: Int
) {

    fun toDomain(): Main {
        return with(this) {
            Main(
                temp = temp,
                feelsLike = feelsLike,
                tempMin = tempMin,
                tempMax = tempMax,
                pressure = pressure,
                humidity = humidity,
                seaLevel = seaLevel,
                groundLevel = groundLevel
            )
        }
    }
}
