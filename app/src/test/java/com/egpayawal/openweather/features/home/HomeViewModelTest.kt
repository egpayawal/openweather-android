package com.egpayawal.openweather.features.home

import com.egpayawal.common.utils.ResourceManager
import com.egpayawal.module.data.features.weather.WeatherRepository
import com.egpayawal.module.domain.models.weather.WeatherData
import com.egpayawal.openweather.core.BaseViewModelTest
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

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
    private val mockSunrise = "5:59 am"
    private val mockSunset = "Sunset: 5:30 pm"
    private val mockLat = "14.6494"
    private val mockLng = "121.1309"

    override fun createViewModel(): HomeViewModel = HomeViewModel(
        weatherRepository,
        resourceManager,
        dispatchers = testDispatcherProvider
    )

    @Test
    fun currentWeather_ShouldEmitError_WhenResponseThrowsException() = runTest {
        val errorMessage = "Test error."
        val throwable = Exception(errorMessage)

        val expectedStates = listOf(
            HomeState.ShowProgressLoading,
            HomeState.Error(throwable),
            HomeState.HideProgressLoading
        )

        viewModel.getCurrentWeather(mockLat, mockLat)

        coEvery { weatherRepository.getWeatherByLocation(any(), any(), any()) } throws throwable
        coEvery { resourceManager.getString(any()) } returns errorMessage

        val states = viewModel.state.take(expectedStates.size).toList()
        advanceUntilIdle()

        assertThat(states).containsExactlyElementsIn(expectedStates).inOrder()
    }

    @Test
    fun currentWeather_ShouldEmitDisplayDay_WhenResponseIsSuccessful() = runTest {
        val expectedStates = listOf(
            HomeState.ShowProgressLoading,
            HomeState.DisplayDay(mockIsEvening),
            HomeState.HideProgressLoading
        )

        viewModel.getCurrentWeather(mockLat, mockLat)

        coEvery { weatherRepository.getWeatherByLocation(any(), any(), any()) }
        coEvery { resourceManager.getString(any()) }

        val states = viewModel.state.take(expectedStates.size).toList()
        advanceUntilIdle()

        assertThat(states).containsExactlyElementsIn(expectedStates).inOrder()
    }
}
