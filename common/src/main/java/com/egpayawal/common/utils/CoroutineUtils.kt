package com.egpayawal.common.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object TimberCoroutineExceptionHandler :
    AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        Timber.e(exception)
    }
}

fun CoroutineScope.launchWithTimber(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(TimberCoroutineExceptionHandler + context, start, block)
}

fun Flow<*>.launchInWithTimber(scope: CoroutineScope): Job = scope.launchWithTimber {
    collect() // tail-call
}

inline val Fragment.viewLifecycleScope: LifecycleCoroutineScope get() = viewLifecycleOwner.lifecycleScope
inline fun <T> Flow<T>.collectViewModelState(
    owner: LifecycleOwner,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launchWithTimber {
    collect {
        action(it)
    }
}
