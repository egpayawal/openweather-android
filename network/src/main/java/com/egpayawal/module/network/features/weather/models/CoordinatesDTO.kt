package com.egpayawal.module.network.features.weather.models

import com.egpayawal.module.domain.models.weather.Coordinates

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
class CoordinatesDTO(
    private val lat: String? = null,
    private val lon: String? = null
) {

    fun toDomain(): Coordinates {
        return with(this) {
            Coordinates(
                lat = lat.orEmpty(),
                lon = lon.orEmpty()
            )
        }
    }
}
