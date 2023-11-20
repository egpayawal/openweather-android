package com.egpayawal.module.data.features.song

//import com.appetiser.auth.core.local.features.session.SessionLocalSource
import com.egpayawal.module.network.features.song.SongRemoteSource
import javax.inject.Inject

/**
 * Note: this is only example
 * @author Eraño Payawal
 */
class SongRepositoryImpl @Inject constructor(
//    private val sessionLocalSource: SessionLocalSource,
    private val songRemoteSource: SongRemoteSource
) : SongRepository {

    companion object {

        const val SONG_PAGE_SIZE = 15
    }

//    override fun getSongs(): Flow<PagingData<Song>> = Pager(config = PagingConfig(SONG_PAGE_SIZE)) {
//        SongPagingSource(sessionLocalSource, songRemoteSource)
//    }.flow
}
