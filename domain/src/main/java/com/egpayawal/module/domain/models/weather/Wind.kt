package com.egpayawal.module.domain.models.weather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
@Parcelize
data class Wind(
    val speed: Double? = null,
    val deg: String
) : Parcelable
