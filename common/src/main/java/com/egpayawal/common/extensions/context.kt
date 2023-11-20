package com.egpayawal.common.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.egpayawal.baseplate.common.R
import com.google.android.material.color.MaterialColors

fun Context.openBrowser(url: String) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
    )
}

@ColorInt
fun Context.getThemeColor(@AttrRes attrResId: Int): Int {
    return MaterialColors
        .getColor(
            this,
            attrResId,
            android.graphics.Color.TRANSPARENT
        )
}

fun Context.openChromeTab(url: String?) {
    try {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.white))
        builder.setShowTitle(true)
        builder.build().launchUrl(this, Uri.parse(url))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun Context.dialPhoneNumber(number: String) {
    return try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.sendEmail(email: String) {
    val isValidEmail = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    if (isValidEmail) {
        try {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
