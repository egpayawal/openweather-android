package com.egpayawal.openweather.features.home

import com.egpayawal.common.utils.ResourceManager
import com.egpayawal.module.data.features.weather.WeatherRepository
import com.egpayawal.openweather.core.BaseViewModelTest
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

/**
 * Created by Eraño Payawal on 11/21/23.
 * hunterxer31@gmail.com
 */
class HomeViewModelTest : BaseViewModelTest<HomeViewModel>() {

    @MockK
    lateinit var weatherRepository: WeatherRepository

    @MockK
    lateinit var resourceManager: ResourceManager

    private val mockIsEvening = true

    override fun createViewModel(): HomeViewModel = HomeViewModel(
        weatherRepository,
        resourceManager,
        dispatchers = testDispatcherProvider
    )

    @Test
    fun currentWeather_ShouldEmitError_WhenResponseThrowsException() = runTest {
        val errorMessage = "Test error."
        val throwable = HttpException(
            Response.error<Unit>(422, errorMessage.toResponseBody())
        )

//        val expectedStates = listOf(
//            HomeState.Error(throwable),
//            HomeState.ShowProgressLoading,
//            HomeState.HideProgressLoading,
//            HomeState.DisplayDay(mockIsEvening),
//            HomeState.DisplaySunrise(mockIsEvening),
//        )
    }
}
