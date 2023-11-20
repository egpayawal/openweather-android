package com.egpayawal.module.network.base.response

import com.google.gson.annotations.SerializedName

open class BasePaginatedResponse<T>(
    @SerializedName("data") val data: T,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("http_status") val httpStatus: Int,
    @SerializedName("meta") val meta: PagingMeta
)

data class PagingMeta(
    @SerializedName("current_page")
    val currentPage: Int = 1,
    @SerializedName("from")
    val from: Int? = null,
    @SerializedName("last_page")
    val lastPage: Int? = null,
    @SerializedName("path")
    val path: String? = null,
    @SerializedName("per_page")
    val perPage: Int? = null,
    @SerializedName("to")
    val to: Int? = null,
    @SerializedName("total")
    val total: Int? = null
)
