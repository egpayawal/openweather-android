package com.egpayawal.common.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton

inline fun Button.enableWhen(editText: EditText, crossinline predicate: (CharSequence) -> Boolean) {
    editText.addTextChangedListener(object : TextWatcher {

        override fun onTextChanged(input: CharSequence, start: Int, before: Int, count: Int) {
            isEnabled = if (input.isNotEmpty()) {
                predicate(input)
            } else {
                false
            }
        }

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    })
    editText.text = editText.text // Force trigger
}

inline fun AppCompatImageButton.enableWhen(editText: EditText, crossinline predicate: (CharSequence) -> Boolean) {
    editText.addTextChangedListener(object : TextWatcher {

        override fun onTextChanged(input: CharSequence, start: Int, before: Int, count: Int) {
            isEnabled = if (input.isNotEmpty()) {
                predicate(input)
            } else {
                false
            }
        }

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    })
    editText.text = editText.text // Force trigger
}

inline fun Button.enableWithAlphaWhen(editText: EditText, crossinline predicate: (CharSequence) -> Boolean) {
    editText.addTextChangedListener(object : TextWatcher {

        override fun onTextChanged(input: CharSequence, start: Int, before: Int, count: Int) {
            isEnabled = if (input.isNotEmpty()) {
                predicate(input)
            } else {
                false
            }

            if (isEnabled) {
                this@enableWithAlphaWhen.alpha = 1f
            } else {
                this@enableWithAlphaWhen.alpha = .3f
            }
        }

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    })
    editText.text = editText.text // Force trigger
}

fun Button.enabledWithAlpha() {
    isEnabled = true
    alpha = 1f
}

fun Button.disabledWithAlpha() {
    isEnabled = false
    alpha = .3f
}
