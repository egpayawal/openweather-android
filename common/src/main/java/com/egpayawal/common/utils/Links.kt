package com.egpayawal.common.utils

import android.content.Context
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.URLSpan
import androidx.core.text.HtmlCompat
import java.util.regex.Pattern

object Links {

    private const val PLAIN_URL_REGEX: String =
        "([\\s\\S\\w\\W]?)((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])([\\s\\S\\w\\W]?)"
    private const val FORMATTED_LINK_REGEX: String =
        "\\[url\\:(\\n*)?([^\\|]+)\\|(\\n*)?([^\\]]+)\\]"
    private val PLAIN_URL_PATTERN: Pattern = Pattern.compile(
        PLAIN_URL_REGEX,
        Pattern.MULTILINE or Pattern.CASE_INSENSITIVE
    )
    private val FORMATTED_LINK_PATTERN: Pattern = Pattern.compile(
        FORMATTED_LINK_REGEX,
        Pattern.MULTILINE or Pattern.CASE_INSENSITIVE
    )

    fun convertToSpannedLinks(source: String?, context: Context, textColor: Int): SpannableString? {
        if (source.isNullOrEmpty()) {
            return null
        }
        val formatted = convertFromFormattedLinks(source)
        val formattedWithUrls = replacePlainUrlsWithHtmlLinks(formatted)

        val spanned = SpannableString(
            HtmlCompat.fromHtml(
                formattedWithUrls,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        )

        val spans = spanned.getSpans(0, spanned.length, URLSpan::class.java)

        if (spans.isNotEmpty()) {
            applyNoUnderlineSpan(spanned, context, textColor = textColor)
        }

        return spanned
    }

    private fun convertFromFormattedLinks(source: String): String {
        return if (source.isBlank()) {
            source
        } else {
            FORMATTED_LINK_PATTERN.matcher(source).replaceAll("<a href=\"$4\">$2</a>")
        }
    }

    private fun replacePlainUrlsWithHtmlLinks(source: String): String {
        val matcher = PLAIN_URL_PATTERN.matcher(source)
        val stringBuilder = StringBuilder()

        if (matcher.find()) {
            val group1 = matcher.group(1)
            val group2 = matcher.group(2)
            val group4 = matcher.group(4)

            group1?.let {
                stringBuilder.append(it)
            }

            stringBuilder.append("<a href=\"$group2\">$group2</a>")

            if (shouldAppendGroup4(group4 = group4)) {
                stringBuilder.append(group4)
            }

            if (shouldReplaceWithHtmlLinks(group1 = group1, group4 = group4)
            ) {
                return matcher.replaceAll(stringBuilder.toString())
            }
        }

        return source
    }

    private fun applyNoUnderlineSpan(spanned: SpannableString, context: Context, textColor: Int) {
        val spans = spanned.getSpans(0, spanned.length, URLSpan::class.java)

        for (span in spans) {
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)
            spanned.removeSpan(span)
            spanned.setSpan(UrlSpanNoUnderline(span, context, textColor), start, end, 0)
        }
    }

    class UrlSpanNoUnderline(
        src: URLSpan,
        private val context: Context,
        private val textColor: Int
    ) : URLSpan(src.url) {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = textColor
        }
    }

    private fun shouldAppendGroup4(group4: String?): Boolean {
        return group4 != null && group4 != "\"" && group4 != "<"
    }

    private fun shouldReplaceWithHtmlLinks(group1: String?, group4: String?): Boolean {
        return (group1 != null && group1 != "\"" && group1 != ">") ||
            (group4 != null && group4 != "\"" && group4 != "<")
    }
}
