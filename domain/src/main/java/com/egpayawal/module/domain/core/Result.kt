package com.egpayawal.module.domain.core

@Suppress("UNCHECKED_CAST")
class Result<out T>(private val value: Any) {

    val isSuccessful: Boolean get() = value !is Error

    val isError: Boolean get() = value is Error

    fun value(): T = value as T

    fun error(): Error = value as Error

    companion object {
        fun <T : Any> success(value: T): Result<T> = Result(value)

        fun <T> error(error: Error): Result<T> = Result(error)

        fun <R, T> error(result: Result<T>): Result<R> = Result(result.error())
    }
}
