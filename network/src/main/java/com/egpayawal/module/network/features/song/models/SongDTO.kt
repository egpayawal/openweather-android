package com.egpayawal.module.network.features.song.models

import com.egpayawal.module.domain.models.Song
import com.google.gson.annotations.SerializedName

/**
 * Note: this is only example
 * @author Eraño Payawal
 */
data class SongDTO(
    @field:SerializedName("track_id") val id: Long,
    @field:SerializedName("track_name") val name: String?,
    @field:SerializedName("artist_id") val artistId: Long?,
    @field:SerializedName("artist_name") val artist: String?,
    @field:SerializedName("collection_name") val collection: String?,
    @field:SerializedName("artwork_url100") val artworkUrl: String,
    @field:SerializedName("track_price") val price: Double,
    @field:SerializedName("currency") val currency: String,
    @field:SerializedName("short_description") val description: String,
    @field:SerializedName("primary_genre_name") val genre: String,
    @field:SerializedName("is_favorite") val isFavorite: Boolean
) {

    companion object {

        fun toDomain(song: SongDTO): Song {
            return with(song) {
                Song(
                    id = id,
                    name = name ?: "",
                    artistId = artistId ?: 0L,
                    artist = artist ?: "",
                    collection = collection ?: "",
                    artworkUrl = artworkUrl,
                    price = price,
                    currency = currency,
                    description = description,
                    genre = genre,
                    isFavorite = isFavorite,
                    previewVideo = null
                )
            }
        }
    }
}
