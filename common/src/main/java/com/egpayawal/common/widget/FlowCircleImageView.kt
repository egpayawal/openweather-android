package com.egpayawal.common.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FlowCircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CircleImageView(context, attrs, defStyle) {

    private val _sharedFlow = MutableSharedFlow<Boolean>(
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        extraBufferCapacity = 1
    )

    fun imageChanges(): Flow<Boolean> = _sharedFlow
        .asSharedFlow()

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        _sharedFlow.tryEmit(true)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        _sharedFlow.tryEmit(resId != 0)
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        _sharedFlow.tryEmit(true)
    }

    override fun setImageIcon(icon: Icon?) {
        super.setImageIcon(icon)
        _sharedFlow.tryEmit(true)
    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(background)
        _sharedFlow.tryEmit(true)
    }

    override fun setBackgroundResource(resId: Int) {
        super.setBackgroundResource(resId)
        _sharedFlow.tryEmit(resId != 0)
    }

    // special case just to get the publish subject changes
    fun triggerChanges() {
        _sharedFlow.tryEmit(true)
    }

    fun refreshTriggerChanges() {
        _sharedFlow.tryEmit(false)
    }
}
