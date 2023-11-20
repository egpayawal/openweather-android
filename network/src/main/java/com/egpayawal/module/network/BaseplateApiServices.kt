package com.egpayawal.module.network

import com.egpayawal.module.network.base.response.BasePagedResponse
import com.egpayawal.module.network.features.song.models.SongDTO
import com.egpayawal.module.network.features.weather.models.ResponseWeatherDTO
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BaseplateApiServices {

    // region Songs
    @GET("tracks?filter[kind]=song&sort[]=track-id")
    suspend fun getSongs(
        @Header("Authorization") token: String,
        @Query("page") page: Int
    ): BasePagedResponse<SongDTO>

    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): ResponseWeatherDTO
}
