package com.egpayawal.module.domain.models

data class Paging<T>(
    val list: List<T>,
    val nextPage: Int? = null
)
