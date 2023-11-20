package com.egpayawal.module.data.features.song

import androidx.paging.PagingSource
import androidx.paging.PagingState
//import com.appetiser.auth.core.local.features.session.SessionLocalSource
import com.egpayawal.module.domain.models.Song
import com.egpayawal.module.network.features.song.SongRemoteSource
import javax.inject.Inject

/**
 * Note: this is only example
 * @author Eraño Payawal
 */
class SongPagingSource @Inject constructor(
//    private val sessionLocalSource: SessionLocalSource,
    private val songRemoteSource: SongRemoteSource
) : PagingSource<Int, Song>() {

    override fun getRefreshKey(
        state: PagingState<Int, Song>
    ): Int? = state.anchorPosition?.let { anchorPosition ->
        val anchorPage = state.closestPageToPosition(anchorPosition)
        anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        try {
            // Start refresh at page 1 if undefined.
//            val accessToken = sessionLocalSource.getSession()
//                .accessToken
//                .bearerToken
            val accessToken = ""

            val nextPageNumber = params.key ?: 1
            val songs = songRemoteSource.getSongs(
                accessToken = accessToken,
                page = nextPageNumber
            )
            return LoadResult.Page(
                data = songs,
                prevKey = null, // Only paging forward.
                nextKey = if (songs.isNotEmpty()) nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            return LoadResult.Error(e)
        }
    }
}
