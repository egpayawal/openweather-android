package com.egpayawal.common.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FlowImageView : AppCompatImageView {

    private val _flow = MutableSharedFlow<Boolean>()

    fun imageChanges(): Flow<Boolean> = _flow.asSharedFlow()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        _flow.tryEmit(true)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        _flow.tryEmit(resId != Resources.ID_NULL)
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        _flow.tryEmit(true)
    }

    override fun setImageIcon(icon: Icon?) {
        super.setImageIcon(icon)
        _flow.tryEmit(true)
    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(background)
        _flow.tryEmit(true)
    }

    override fun setBackgroundResource(resId: Int) {
        super.setBackgroundResource(resId)
        _flow.tryEmit(resId != Resources.ID_NULL)
    }

    // special case just to get the publish subject changes
    fun triggerChanges() {
        _flow.tryEmit(true)
    }

    fun refreshTriggerChanges() {
        _flow.tryEmit(false)
    }
}
