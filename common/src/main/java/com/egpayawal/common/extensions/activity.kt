package com.egpayawal.common.extensions

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.view.Window.ID_ANDROID_CONTENT
import android.view.WindowManager
import android.widget.Toast
import java.io.File
import java.io.IOException

fun getStatusBarHeight(): Int {
    val res = Resources.getSystem()
    val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        return res.getDimensionPixelSize(resourceId)
    }

    return 0
}

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.getDeviceHeight(): Int {
    return DisplayMetrics().apply {
        this@getDeviceHeight.windowManager.defaultDisplay.getMetrics(this)
    }.heightPixels
}

fun Activity.getDeviceWidth(): Int {
    return DisplayMetrics().apply {
        this@getDeviceWidth.windowManager.defaultDisplay.getMetrics(this)
    }.heightPixels
}

fun Activity.convertPxToDp(px: Float): Float {
    return px / this.resources.displayMetrics.density
}

fun Activity.convertDpToPx(dp: Float): Float {
    return dp * this.resources.displayMetrics.density
}

@Throws(IOException::class)
fun Activity.createImageFile(): File {
    val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

    return File.createTempFile(
        "avatar", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}

fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content)
}

fun Activity.isKeyboardOpen(): Boolean {
    val rootView = this.getRootView()
    val heightDiff = rootView.height - rootView.height
    val contentViewTop = window.findViewById<View>(ID_ANDROID_CONTENT).top

    return heightDiff > contentViewTop
}

fun Activity.isKeyboardClosed(): Boolean {
    return !this.isKeyboardOpen()
}

fun Activity.makeStatusBarTransparent(isLightStatusBar: Boolean = true) {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (!isLightStatusBar) {
            clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = if (isLightStatusBar) {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        statusBarColor = Color.TRANSPARENT
    }
}

fun Activity.makeStatusBarNonTransparent(isLightStatusBar: Boolean = true) {
    window.apply {
        decorView.systemUiVisibility = 0
        if (isLightStatusBar) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}

fun Activity.makeSoftInputModeAdjustPan() {
    window.apply {
        // If input mode is already adjustPan. Return.
        if (attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) return

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}

fun Activity.makeSoftInputModeAdjustResize() {
    window.apply {
        // If input mode is already adjustResize. Return.
        if (attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) return

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}
