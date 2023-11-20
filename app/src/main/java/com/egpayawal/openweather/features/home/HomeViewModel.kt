package com.egpayawal.openweather.features.home

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.egpayawal.common.base.BaseViewModel
import com.egpayawal.common.utils.DateUtils.convertSecondsToDate
import com.egpayawal.common.utils.DateUtils.getCurrentTime
import com.egpayawal.common.utils.DateUtils.getParseDateTime
import com.egpayawal.common.utils.ResourceManager
import com.egpayawal.common.utils.dispatchers.DispatcherProvider
import com.egpayawal.common.utils.launchWithTimber
import com.egpayawal.module.data.features.weather.WeatherRepository
import com.egpayawal.module.domain.models.weather.WeatherData
import com.egpayawal.openweather.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber
import java.time.LocalTime
import javax.inject.Inject

/**
 * @author Eraño Payawal
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val resourceManager: ResourceManager,
    private val dispatchers: DispatcherProvider
) : BaseViewModel() {

    private val _state: MutableSharedFlow<HomeState> = MutableSharedFlow()

    val state: Flow<HomeState> = _state

    override fun isFirstTimeUiCreate(bundle: Bundle?) = Unit

    fun getCurrentWeather(lat: String, lon: String) {
        viewModelScope.launchWithTimber(dispatchers.io) {
            Timber.tag("DEBUG").e("getCurrentWeather :: lat::$lat , lng:$lon")
            _state.emit(HomeState.ShowProgressLoading)
            try {
                val data =
                    weatherRepository.getWeatherByLocation(
                        lat = lat,
                        lon = lon,
                        appId = resourceManager.getString(R.string.open_weather_app_id)
                    )
                Timber.tag("DEBUG").e("get current weather success")
                handleResult(data)
            } catch (e: Exception) {
                Timber.e(e)
                _state.emit(HomeState.Error(e))
            } finally {
                _state.emit(HomeState.HideProgressLoading)
            }
        }
    }

    private suspend fun handleResult(data: WeatherData) {
        val location = String.format("%s, %s", "${data.name}", "${data.sys?.country}")
        val celsius = data.main?.temp?.toString().orEmpty()
        val temp = resourceManager.getString(R.string.temp, celsius)
        Timber.tag("DEBUG").e("Place: $location")

        Timber.tag("DEBUG").e("sunrise long:: ${data.sys?.sunrise}")
        data.sys?.sunrise?.let {

        }

        var sunrise = ""
        var sunset = ""
        if (data.sys?.sunrise != null) {
            val convertedDateSunrise = convertSecondsToDate(data.sys?.sunrise!!)
            val time = getParseDateTime(dateTime = convertedDateSunrise, outputFormat = "h:mm a")
            Timber.tag("DEBUG").e("time sunrise:: $time")
            sunrise = time
        }
        if (data.sys?.sunset != null) {
            val convertedDateSunset = convertSecondsToDate(data.sys?.sunset!!)
            val time = getParseDateTime(dateTime = convertedDateSunset, outputFormat = "h:mm a")
            sunset = resourceManager.getString(R.string.sunset, time)
        }

        val todayTime = getCurrentTime()
        Timber.tag("DEBUG").e("todayTime:: $todayTime")

        val now = LocalTime.now()
        Timber.tag("DEBUG").e("now:: $now")

        val isEvening = ((now.hour >= 18 && now.minute >= 0) || (now.hour in 0..6 && now.minute >= 0))

        if (isEvening) {
            Timber.tag("DEBUG").e("evening!")
        } else {
            Timber.tag("DEBUG").e("morning!")
        }


        _state.emit(HomeState.DisplayDay(isEvening))
        _state.emit(HomeState.DisplaySunrise(sunrise, sunset))
        _state.emit(HomeState.DisplayLocation(location))
        _state.emit(HomeState.DisplayTemperature(temp))
        _state.emit(HomeState.DisplayWeather(data.weather?.firstOrNull()))
    }
}
