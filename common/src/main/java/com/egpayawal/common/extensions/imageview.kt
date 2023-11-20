package com.egpayawal.common.extensions

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.egpayawal.baseplate.common.R
import com.egpayawal.common.GlideApp
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import timber.log.Timber

@SuppressLint("CheckResult")
@BindingAdapter("avatarUrl", "imageSignature", requireAll = false)
fun ImageView.loadAvatarUrl(url: String?, imageSignature: Any? = null) {
    val image = GlideApp.with(this.context)
        .load(url)
        .placeholder(R.drawable.ic_default_avatar)
        .error(R.drawable.ic_default_avatar)
        .fallback(R.drawable.ic_default_avatar)
        .skipMemoryCache(false)
        .dontAnimate()
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                Timber.e("onLoadFailed Error $e")
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })

    if (imageSignature != null) {
        image.signature(ObjectKey(imageSignature))
    }
    image.into(this)
}

@BindingAdapter("imageSrc")
fun ImageView.loadImageUrl(url: String?) {
    if (url != null && url.isNotEmpty()) {
        GlideApp.with(this.context)
            .load("$url")
            .placeholder(R.drawable.ic_create_post_placeholder)
            .skipMemoryCache(false)
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Timber.e("onLoadFailed Error $e ")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(this)
    }
}

@BindingAdapter("imageSrc")
fun ImageView.loadImageUrlFitCenter(url: String?) {
    if (url != null && url.isNotEmpty()) {
        GlideApp.with(this.context)
            .load("$url")
            .placeholder(R.drawable.ic_create_post_placeholder)
            .skipMemoryCache(false)
            .fitCenter()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Timber.e("onLoadFailed Error $e ")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(this)
    }
}

fun ImageView.loadDrawable(@DrawableRes resId: Int?) {
    resId?.let {
        GlideApp.with(context)
            .asBitmap()
            .load(resId)
            .dontAnimate()
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    this@loadDrawable.setImageBitmap(resource)
                }

                override fun onLoadCleared(errorDrawable: Drawable?) {
                    Timber.e("onLoadFailed Error $errorDrawable ")
                }
            })
    }
}
