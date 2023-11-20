package com.egpayawal.module.network.base.response

import com.google.gson.annotations.SerializedName

data class BaseResponseNew<T>(
    @SerializedName("data") val data: T,
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String = "",
    @SerializedName("http_status") val httpStatus: Int = 500
)
