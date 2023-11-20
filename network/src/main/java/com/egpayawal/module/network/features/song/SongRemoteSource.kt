package com.egpayawal.module.network.features.song

import com.egpayawal.module.domain.models.Song

/**
 * Note: this is only example
 * @author Eraño Payawal
 */
interface SongRemoteSource {

    suspend fun getSongs(accessToken: String, page: Int): List<Song>
}
