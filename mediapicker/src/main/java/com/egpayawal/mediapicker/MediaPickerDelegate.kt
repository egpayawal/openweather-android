package com.egpayawal.mediapicker

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.egpayawal.mediapicker.models.MediaPickerOptions
import com.egpayawal.mediapicker.models.MediaPickerOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class MediaPickerDelegate(
    private val fragment: Fragment,
    private val factory: () -> MediaPickerOptions
) : ReadOnlyProperty<Fragment, MediaPicker> {

    private var mediaPicker: MediaPicker? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.apply {
                    mediaPicker = MediaPicker(
                        owner = MediaPickerOwner.OwnerFragment(fragment),
                        options = factory()
                    )
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                mediaPicker = null
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): MediaPicker {
        mediaPicker?.let { return it }

        error("Failed to Initialize MediaPicker")
    }
}
