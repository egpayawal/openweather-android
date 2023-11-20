package com.egpayawal.module.network.features.weather.models

import com.egpayawal.module.domain.models.weather.Clouds

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
class CloudsDTO(
    private val all: Int
) {

    fun toDomain(): Clouds {
        return with(this) {
            Clouds(
                all = all
            )
        }
    }
}
