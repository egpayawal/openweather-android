package com.egpayawal.common.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


object DateUtils {

    private const val DATE_FORMAT = "MM/dd/yyyy"

    /**
     * Formats date to short date format based on device's locale.
     */
    fun formatDateLocalized(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        return formatter.format(date)
    }

    /**
     * Formats date to short date format based on device's locale.
     */
    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
        return formatter.format(date)
    }

    /**
     * Parses date of birth of this format "yyyy-mm-d" to [LocalDate].
     */
    fun parseBirthDate(birthDate: String): LocalDate {
        require(birthDate.isNotEmpty()) {
            "Required parameter birthDate was empty."
        }

        val split = birthDate.split("-")

        return LocalDate
            .of(
                split[0].toInt(),
                split[1].toInt(),
                split[2].toInt()
            )
    }

    fun getCurrentTime(): String {
        val todayInMilli = Instant.now().toEpochMilli()
        val todayInSec = todayInMilli / 1000L
        val date = convertSecondsToDate(todayInSec)
        return getParseDateTime(date, "h:mm a")
    }

    fun convertSecondsToDate(timeInSeconds: Long): String {
        val dateTime = Instant.ofEpochSecond(timeInSeconds)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        return dateTime.toString()
    }

    /**
     * @param dateTime = e.i, format 2023-07-17T09:59:49+08:00
     * @param outputFormat = desire date format
     * @return formatted date = E, d MMMM yyyy, h:mm a (as default)
     */
    fun getParseDateTime(
        dateTime: String,
        outputFormat: String = "E, d MMMM yyyy, h:mm a"
    ): String {
        val parse = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
            .atZone(ZoneId.systemDefault())
        return parse.to(format = outputFormat)
    }

    private fun ZonedDateTime.to(format: String): String {
        return format(DateTimeFormatter.ofPattern(format))
    }
}
