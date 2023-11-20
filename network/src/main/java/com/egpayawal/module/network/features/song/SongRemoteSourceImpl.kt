package com.egpayawal.module.network.features.song

import com.egpayawal.module.domain.models.Song
import com.egpayawal.module.network.BaseplateApiServices
import com.egpayawal.module.network.features.song.models.SongDTO
import javax.inject.Inject

/**
 * Note: this is only example
 * @author Eraño Payawal
 */
class SongRemoteSourceImpl @Inject constructor(
    private val api: BaseplateApiServices
) : SongRemoteSource {

    override suspend fun getSongs(accessToken: String, page: Int): List<Song> {
        return api.getSongs(accessToken, page).data.map { SongDTO.toDomain(it) }
    }
}
