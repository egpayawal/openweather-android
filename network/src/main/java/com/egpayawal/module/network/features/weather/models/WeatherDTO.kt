package com.egpayawal.module.network.features.weather.models

import com.egpayawal.module.domain.models.weather.Weather

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
class WeatherDTO(
    private val id: Int,
    private val main: String? = null,
    private val description: String? = null,
    private val icon: String? = null
) {

    fun toDomain(): Weather {
        return with(this) {
            Weather(
                id = id,
                main = main.orEmpty(),
                description = description.orEmpty(),
                icon = icon.orEmpty()
            )
        }
    }
}
