package com.egpayawal.module.domain.models.weather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
@Parcelize
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) : Parcelable
