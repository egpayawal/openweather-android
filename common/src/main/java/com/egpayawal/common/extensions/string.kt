package com.egpayawal.common.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.telephony.TelephonyManager
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.UnderlineSpan
import android.util.Base64
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import com.egpayawal.baseplate.common.BuildConfig
import com.egpayawal.common.widget.CustomTypefaceSpan
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.abs

fun String.isEmailValid(): Boolean {
    return Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$",
        Pattern
            .CASE_INSENSITIVE
    ).matcher(this).find()
}

@SuppressLint("DefaultLocale")
fun Context.getNetworkCountryIso(): String {
    // adb shell setprop gsm.sim.operator.iso-country ph
    if (BuildConfig.DEBUG) {
        return "ph".toUpperCase()
    }
    val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return tm.networkCountryIso.capitalize()
}

@Suppress("DEPRECATION")
@SuppressWarnings("deprecation")
fun String.toHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

private fun String.convertImageBitmapToBase64(): String {
    val baos = ByteArrayOutputStream()
    val bitmap = BitmapFactory.decodeFile(File(this).absolutePath) ?: return ""
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    return "data:image/png;base64,".plus(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT))
}

fun String.convertStringToBase64(): String {
    val data = this.toByteArray(Charset.forName("UTF-8"))
    return Base64.encodeToString(data, Base64.DEFAULT)
}

fun String.underlineString(
    context: Context,
    fontFamily: Int,
    vararg words: String,
    clickable: (String) -> Unit
): SpannableStringBuilder {
    val ssb = SpannableStringBuilder()
    val typeface = ResourcesCompat.getFont(context, fontFamily)

    if (this.isEmpty()) {
        throw NullPointerException("text must not be empty")
    }

    if (words.isEmpty()) {
        throw NullPointerException("words must not be empty")
    }

    ssb.append(this)
    for (item in words) {
        val findIndex = this.indexOf(item)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                clickable.invoke(item)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
            }
        }

        ssb.setSpan(
            UnderlineSpan(),
            findIndex,
            findIndex + item.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        typeface?.let {
            ssb.setSpan(
                CustomTypefaceSpan(it),
                findIndex,
                findIndex + item.length,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        ssb.setSpan(
            clickableSpan,
            findIndex,
            findIndex + item.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return ssb
}

fun String.spannableString(
    context: Context,
    textSize: Float? = 0f,
    fontFamily: Int,
    @ColorInt fontColor: Int,
    vararg words: String,
    clickable: (String) -> Unit
): SpannableStringBuilder {
    val ssb = SpannableStringBuilder()
    val typeface = ResourcesCompat.getFont(context, fontFamily)

    if (this.isEmpty()) {
        throw NullPointerException("text must not be empty")
    }

    if (words.isEmpty()) {
        throw NullPointerException("words must not be empty")
    }

    ssb.append(this)
    for (item in words) {
        val findIndex = this.indexOf(item)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                clickable.invoke(item)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }

        ssb.setSpan(
            ForegroundColorSpan(fontColor),
            findIndex,
            findIndex + item.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        typeface?.let {
            ssb.setSpan(
                CustomTypefaceSpan(it),
                findIndex,
                findIndex + item.length,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        ssb.setSpan(
            clickableSpan,
            findIndex,
            findIndex + item.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textSize?.let {
            ssb.setSpan(
                RelativeSizeSpan(it),
                findIndex,
                findIndex + item.length,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
    }
    return ssb
}

fun String.getTimeSpanString(): CharSequence {
    val odt = OffsetDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
    val difference = Duration.between(odt, ZonedDateTime.now()).toMillis() / 1000
    val hours = difference.div(3600)
    val minutes = difference.rem(3600).div(60)
    val seconds = difference.rem(60)
    val days = abs(hours / 24)
    val years = abs(days / 365)

    var dayTime = ""

    if (years > 1) {
        return DateTimeFormatter.ofPattern("dd MMMM yyyy").format(odt)
    }

    if (seconds < 60) {
        dayTime = if (seconds <= 0) {
            "now"
        } else {
            abs(seconds).toString() + "s"
        }
    }

    if (minutes in 1..60) {
        dayTime = abs(minutes).toString() + "m"
    }

    if (hours in 1..24) {
        dayTime = abs(hours).toString() + "h"
    }

    if (days in 1..7) {
        dayTime = abs(days).toString() + "d"
    }

    if (days in 8..31) {
        dayTime = DateTimeFormatter.ofPattern("d MMMM").format(odt)
    }

    return dayTime
}

fun String.getTimeAgo(): CharSequence {
    val odt = OffsetDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
    return TimeAgo.using(
        odt.toInstant().toEpochMilli(),
        TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build()
    )
}

fun Long.getTimeAgo(): CharSequence {
    return TimeAgo.using(this, TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build())
}
