package com.egpayawal.module.network.base.response

import com.google.gson.annotations.SerializedName

data class BasePagedResponse<T>(
    @SerializedName("data") val data: List<T>,
    @SerializedName("meta") val meta: PagingMeta,
    @SerializedName("links") val links: PagingLinks
) : BaseResponse() {
    fun <R> map(transform: (T) -> R): BasePagedResponse<R> = BasePagedResponse(
        data = data.map(transform),
        meta = meta,
        links = links
    )
}
