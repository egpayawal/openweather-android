package com.egpayawal.module.domain.core

data class Error(
    val message: String = "",
    val cause: Throwable? = null
)
