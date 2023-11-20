package com.egpayawal.openweather.features.home

import com.egpayawal.module.domain.models.weather.Weather

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
sealed class HomeState {

    data class DisplayLocation(val location: String) : HomeState()

    data class DisplayTemperature(val temp: String) : HomeState()

    data class DisplayWeather(val weather: Weather?) : HomeState()

    data class DisplaySunrise(val sunrise: String, val sunset: String) : HomeState()

    data class DisplayDay(val isEvening: Boolean) : HomeState()

    object ShowProgressLoading : HomeState()

    object HideProgressLoading : HomeState()

    data class Error(val throwable: Throwable) : HomeState()
}
