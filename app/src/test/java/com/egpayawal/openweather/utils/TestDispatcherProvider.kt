package com.egpayawal.openweather.utils

import com.egpayawal.common.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class TestDispatcherProvider(val dispatcher: TestDispatcher = UnconfinedTestDispatcher()) :
    DispatcherProvider {
    override val default: CoroutineDispatcher = dispatcher

    override val io: CoroutineDispatcher = dispatcher

    override val ui: CoroutineDispatcher = dispatcher

    override val uiImmediate: CoroutineDispatcher = dispatcher
}
