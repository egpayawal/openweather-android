package com.egpayawal.module.domain.models.weather

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
data class WeatherData(
    val coord: Coordinates? = null,
    val weather: List<Weather>? = null,
    val base: String? = null,
    val main: Main? = null,
    val visibility: Int,
    val wind: Wind? = null,
    val clouds: Clouds? = null,
    val dt: Long = -1L,
    val sys: Sys? = null,
    val timezone: Long = -1L,
    val id: Long = -1L,
    val name: String? = null,
    val cod: Int
)
