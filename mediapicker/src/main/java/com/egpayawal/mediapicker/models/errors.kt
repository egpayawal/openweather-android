package com.egpayawal.mediapicker.models

object MediaPickerCancelled : RuntimeException()

data class MediaPickerError(override val message: String) : RuntimeException(message)
