package com.egpayawal.mediapicker.models

data class VideoCompression(
    val isSharedStorage: Boolean = false,
    val quality: VideoQuality = VideoQuality.MEDIUM,
    val isMinBitrateCheckEnabled: Boolean = true,
    val videoBitrateInMbps: Int? = null,
    val disableAudio: Boolean = false,
    val keepOriginalResolution: Boolean = true,
    val videoHeight: Double? = null,
    val videoWidth: Double? = null
)

enum class VideoQuality {
    VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW
}
