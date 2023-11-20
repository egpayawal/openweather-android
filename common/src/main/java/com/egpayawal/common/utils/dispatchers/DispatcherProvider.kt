package com.egpayawal.common.utils.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Allow providing different types of [CoroutineDispatcher]s.
 */
interface DispatcherProvider {

    val default: CoroutineDispatcher

    val io: CoroutineDispatcher

    val ui: CoroutineDispatcher

    val uiImmediate: CoroutineDispatcher
}
