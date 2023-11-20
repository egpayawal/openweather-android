package com.egpayawal.mediapicker.models

import android.net.Uri
import java.io.File

data class CapturedData(
    val uri: Uri,
    val file: File
)
