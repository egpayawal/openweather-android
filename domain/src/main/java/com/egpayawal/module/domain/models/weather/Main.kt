package com.egpayawal.module.domain.models.weather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
@Parcelize
data class Main(
    val temp: Double? = null,
    val feelsLike: Double? = null,
    val tempMin: Double? = null,
    val tempMax: Double? = null,
    val pressure: Int,
    val humidity: Int,
    val seaLevel: Int,
    val groundLevel: Int
) : Parcelable
