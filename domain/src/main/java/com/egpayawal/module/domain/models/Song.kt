package com.egpayawal.module.domain.models

import java.util.*

/**
 * Note: this is only example
 * @author Eraño Payawal
 */
data class Song(
    val id: Long,
    val name: String,
    var artistId: Long,
    val artist: String,
    val collection: String,
    val artworkUrl: String,
    val price: Double,
    val currency: String,
    val description: String,
    val genre: String,
    val isFavorite: Boolean,
    val previewVideo: String?
) {

    val thumbnail: String
        get() = artworkUrl.replace("100x100", "300x300")

    val priceDisplay: String
        get() = "${Currency.getInstance(currency).symbol} ${this.price}"
}
