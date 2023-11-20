package com.egpayawal.mediapicker

import androidx.fragment.app.Fragment
import com.egpayawal.mediapicker.models.MediaPickerOptions

fun Fragment.mediaPickers(
    factory: () -> MediaPickerOptions
) = MediaPickerDelegate(
    fragment = this,
    factory = factory
)
