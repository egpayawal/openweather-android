package com.egpayawal.common.extensions

import java.time.*

fun Long.yearsAgoInMilli(): Long {
    return LocalDateTime
        .now()
        .atZone(SYSTEM_DEFAULT_ZONE_ID)
        .minusYears(this)
        .toEpochSecond() * 1000
}

fun Long.toLocalDate(): LocalDate {
    return toZonedDateTime().toLocalDate()
}

fun Long.toZonedDateTime(): ZonedDateTime {
    return Instant
        .ofEpochSecond(this)
        .atZone(SYSTEM_DEFAULT_ZONE_ID)
}

val SYSTEM_DEFAULT_ZONE_ID: ZoneId = ZoneId.systemDefault()
