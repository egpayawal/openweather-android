package com.egpayawal.module.network.base

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egpayawal.module.network.base.response.BasePagedResponse
import com.egpayawal.module.network.base.response.PagingOption

fun <T : Any> createPagingSource(
    requestData: suspend (pagingOption: PagingOption) -> BasePagedResponse<T>,
    getRefreshKeyCallback: (state: PagingState<Int, T>) -> Int? = { state ->
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
): PagingSource<Int, T> = BasePagingSourceImpl(requestData, getRefreshKeyCallback)

private open class BasePagingSourceImpl<T : Any>(
    private val requestData: suspend (pagingOption: PagingOption) -> BasePagedResponse<T>,
    private val getRefreshKeyCallback: (state: PagingState<Int, T>) -> Int?
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val request = requestData(
                PagingOption(
                    count = params.loadSize,
                    page = params.key ?: 1 // First page
                )
            )

            LoadResult.Page(
                data = request.data,
                prevKey = request.links.prevKey,
                nextKey = request.links.nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return getRefreshKeyCallback.invoke(state)
    }
}
