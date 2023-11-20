package com.egpayawal.mediapicker.models

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

sealed class MediaPickerOwner {

    data class OwnerActivity(val owner: FragmentActivity) : MediaPickerOwner()

    data class OwnerFragment(val owner: Fragment) : MediaPickerOwner()

    fun <I, O> registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>
    ): ActivityResultLauncher<I> = when (this) {
        is OwnerActivity -> owner.registerForActivityResult(contract, callback)
        is OwnerFragment -> owner.registerForActivityResult(contract, callback)
    }

    val context: Context
        get() = when (this) {
            is OwnerActivity -> owner
            is OwnerFragment -> owner.requireContext()
        }

    fun hasPermissions(permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}
