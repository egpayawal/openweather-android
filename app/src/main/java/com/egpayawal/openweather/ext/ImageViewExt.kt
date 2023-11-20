package com.egpayawal.openweather.ext

import android.widget.ImageView
import com.egpayawal.common.extensions.loadImageUrl
import com.egpayawal.openweather.utils.URL_ICON
import timber.log.Timber

/**
 * Created by Eraño Payawal on 11/20/23.
 * hunterxer31@gmail.com
 */
fun ImageView.loadIcon(icon: String) {
    val url = String.format(URL_ICON, icon)
    Timber.tag("DEBUG").e("icon url::$url")
    this.loadImageUrl(url)
}
