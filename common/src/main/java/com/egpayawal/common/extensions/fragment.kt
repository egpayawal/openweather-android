package com.egpayawal.common.extensions

import android.os.Environment
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.IOException

fun Fragment.navigate(dest: NavDirections, navigatorExtras: Navigator.Extras? = null) {
    if (navigatorExtras != null) {
        findNavController()
            .navigate(
                dest,
                navigatorExtras
            )
    } else {
        findNavController().navigate(dest)
    }
}

fun Fragment.navigate(dest: NavDirections, navOptions: NavOptions) {
    findNavController()
        .navigate(
            dest,
            navOptions
        )
}

fun Fragment.navigateUp() = findNavController().navigateUp()

fun Fragment.isDestinationInBackstack(@IdRes destinationId: Int): Boolean {
    return try {
        findNavController().getBackStackEntry(destinationId)
        true
    } catch (e: Exception) {
        false
    }
}

@Throws(IOException::class)
fun Fragment.createImageFile(): File {
    val storageDir: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

    return File.createTempFile(
        "avatar", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}
