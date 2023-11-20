package com.egpayawal.common.utils

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView

class CustomLinkMovementMethod(
    private val onUrlClicked: (String) -> Unit,
    private val onShowKeyboard: () -> Unit
) : LinkMovementMethod() {

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())
            val link = buffer.getSpans(off, off, URLSpan::class.java)

            if (link.isNotEmpty()) {
                val url = link[0].url
                onUrlClicked(url)
                return true
            } else if (widget is EditText && widget.isFocusable && widget.isEnabled) {
                widget.isFocusableInTouchMode = true
                widget.post {
                    widget.requestFocus()
                    if (widget.text.isEmpty()) {
                        widget.setSelection(0)
                    }
                    onShowKeyboard()
                }
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }
}
