package com.egpayawal.module.network.features.weather.models

import com.egpayawal.module.domain.models.weather.Sys

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
class SysDTO(
    private val type: Int,
    private val id: Long = -1L,
    private val country: String? = null,
    private val sunrise: Long = -1L,
    private val sunset: Long = -1L
) {

    fun toDomain(): Sys {
        return with(this) {
            Sys(
                type = type,
                id = id,
                country = country.orEmpty(),
                sunrise = sunrise,
                sunset = sunset
            )
        }
    }
}
