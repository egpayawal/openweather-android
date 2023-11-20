package com.egpayawal.module.network.base.response

data class PagingOption(
    val count: Int,
    val page: Int
) : Map<String, Int> by mapOf("per_page" to count, "page" to page)
