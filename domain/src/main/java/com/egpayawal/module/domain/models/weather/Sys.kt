package com.egpayawal.module.domain.models.weather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
@Parcelize
data class Sys(
    val type: Int,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long
) : Parcelable
