package com.egpayawal.module.network.base.response

import com.google.gson.annotations.SerializedName
import java.net.URL

data class PagingLinks(
    @SerializedName("first") val firstLink: String?,
    @SerializedName("last") val lastLink: String?,
    @SerializedName("prev") val previousLink: String?,
    @SerializedName("next") val nextLink: String?
) {

    val firstKey: Int? get() = firstLink?.getKey()
    val lastKey: Int? get() = lastLink?.getKey()
    val prevKey: Int? get() = previousLink?.getKey()
    val nextKey: Int? get() = nextLink?.getKey()

    private fun String.getKey(): Int? {
        val query = URL(this).query?.run {
            split("&")
                .associate {
                    val keyPair = it.split("=")
                    keyPair[0] to keyPair[1].toInt()
                }
        } ?: emptyMap()

        return query["page"]
    }
}
