package com.egpayawal.mediapicker.models

import android.graphics.Bitmap

/**
 * @property quality from 0 to 100
 * @property maxFileSize in bytes
 */
data class ImageCompression(
    val width: Int = 612,
    val height: Int = 816,
    val quality: Int = 80,
    val format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    val maxFileSize: Long? = null
)
