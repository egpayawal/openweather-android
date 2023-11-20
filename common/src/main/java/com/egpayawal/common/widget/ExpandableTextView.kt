package com.egpayawal.common.widget

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.egpayawal.baseplate.common.R
import com.egpayawal.common.extensions.afterMeasured
import com.google.android.material.color.MaterialColors

class ExpandableTextView : AppCompatTextView {

    private var readMoreCollapsedText = " more"
    private var readMoreExpandText = " less"
    private var readMoreColor = ContextCompat.getColor(context, R.color.black_30)
    private var _maxLines = 2
    private var originalText: CharSequence? = null

    private var isTextCollapsed = false

    private var clickListener: OnClickMoreListener? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
            try {
                array.getString(R.styleable.ExpandableTextView_etv_read_more_collapsed_text).let {
                    readMoreCollapsedText = " ${it.orEmpty()}"
                }

                array.getString(R.styleable.ExpandableTextView_etv_read_more_expand_text).let {
                    readMoreExpandText = " ${it.orEmpty()}"
                }
                array.getResourceId(R.styleable.ExpandableTextView_etv_read_more_text_color, R.color.cerulean_blue)
                    .let {
                        if (it == R.color.cerulean_blue) {
                            readMoreColor = MaterialColors
                                .getColor(context, R.attr.colorPrimaryFull, Color.BLUE)
                        } else {
                            readMoreColor = ContextCompat.getColor(context, it)
                        }
                    }

                array.getInt(R.styleable.ExpandableTextView_etv_read_more_max_lines, _maxLines).let {
                    _maxLines = it
                }
            } finally {
                array.recycle()
            }
        }

        triggerTruncateText()
    }

    fun setOnMoreClickListener(listener: OnClickMoreListener) {
        this.clickListener = listener
    }

    override fun getMaxLines(): Int {
        return _maxLines
    }

    override fun setMaxLines(maxLines: Int) {
        _maxLines = maxLines
    }

    fun triggerTruncateText() {
        afterMeasured {
            truncateText()
        }
    }

    private val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            clickListener?.onClickMoreListener()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }

    private fun truncateText() {
        val maxLines = _maxLines
        if (lineCount >= maxLines) {
            isTextCollapsed = true

            val lineEndIndex = layout.getLineEnd(maxLines - 1)
            val truncatedText =
                text.subSequence(0, lineEndIndex - readMoreCollapsedText.length).toString() + readMoreCollapsedText
            val spannable: Spannable = SpannableString(truncatedText)
            spannable.setSpan(
                ForegroundColorSpan(readMoreColor),
                truncatedText.length - readMoreCollapsedText.length,
                truncatedText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                clickableSpan,
                truncatedText.length - readMoreCollapsedText.length,
                truncatedText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text = spannable
            super.setMaxLines(_maxLines)
        }
    }

    private fun expandText() {
        text = originalText.toString()

        val truncatedText = originalText.toString() + readMoreExpandText
        val spannable: Spannable = SpannableString(truncatedText)
        spannable.setSpan(
            ForegroundColorSpan(readMoreColor),
            truncatedText.length - readMoreExpandText.length,
            truncatedText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            clickableSpan,
            truncatedText.length - readMoreExpandText.length,
            truncatedText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        text = spannable

        super.setMaxLines(1000)
    }

    fun setFullText(originalText: String) {
        this.originalText = originalText
    }

    fun toggle() {
        if (isTextCollapsed) {
            isTextCollapsed = false
            expandText()
        } else {
            isTextCollapsed = true
            truncateText()
        }
        invalidate()
    }

    fun reset() {
        originalText = null
    }

    interface OnClickMoreListener {

        fun onClickMoreListener()
    }
}
