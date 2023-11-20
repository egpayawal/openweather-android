package com.egpayawal.module.network.features.weather.models

import com.egpayawal.module.domain.models.weather.Wind

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
class WindDTO(
    private val speed: Double? = null,
    private val deg: String? = null
) {

    fun toDomain(): Wind {
        return with(this) {
            Wind(
                speed = speed,
                deg = deg.orEmpty()
            )
        }
    }
}
