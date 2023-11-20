@file:OptIn(FlowPreview::class)

package com.egpayawal.common.extensions

import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.DrawableRes
import com.egpayawal.common.widget.CustomPasswordTransformation
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import reactivecircus.flowbinding.android.view.keys
import reactivecircus.flowbinding.android.widget.afterTextChanges
import reactivecircus.flowbinding.android.widget.editorActionEvents
import reactivecircus.flowbinding.android.widget.textChangeEvents
import reactivecircus.flowbinding.android.widget.textChanges
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

suspend fun EditText.capitalizeFirstLetter() {
    return textChangeEvents()
        .skipInitialValue()
        .map {
            val typeString = it.text.toString()
            if (typeString.length == 1) {
                if (typeString.single().isLowerCase()) {
                    setText(typeString.uppercase(Locale.getDefault()))
                }
            }
            return@map typeString
        }.collect {
            setSelection(it.length)
        }
}

fun EditText.setDrawableEnd(@DrawableRes drawableId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(
        0,
        0,
        drawableId,
        0
    )
}

fun EditText.indicatorIcons(@DrawableRes enabledIcon: Int, @DrawableRes disabled: Int) {
    addTextChangedListener(object : TextWatcher {

        override fun onTextChanged(input: CharSequence, start: Int, before: Int, count: Int) {
            if (input.isNotEmpty()) {
                setDrawableEnd(enabledIcon)
            } else {
                setDrawableEnd(disabled)
            }
        }

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    })
}

fun TextInputLayout.setCustomPasswordTransformation() {
    setEndIconOnClickListener {
        editText?.let { et ->
            // Store the current cursor position
            val selection = et.selectionEnd

            if (et.transformationMethod != null &&
                et.transformationMethod is PasswordTransformationMethod
            ) {
                et.transformationMethod = null
            } else {
                et.transformationMethod = CustomPasswordTransformation.getInstance()
            }

            // And restore the cursor position
            if (selection >= 0) {
                et.setSelection(selection)
            }
        }
    }

    editText?.transformationMethod = CustomPasswordTransformation.getInstance()
}

fun EditText.debounceTextChanges(): Flow<String> {
    return textChanges()
        .skipInitialValue()
        .debounce(TEXT_WATCHER_DEBOUNCE_TIME.milliseconds)
        .map { it.toString() }
}

fun EditText.isTextChangeEventNotEmpty(): Flow<Boolean> {
    return textChangeEvents()
        .skipInitialValue()
        .map { it.text }
        .map { it.isNotEmpty() }
}

fun EditText.unifiedInputs(
    editorAction: Int = EditorInfo.IME_ACTION_SEARCH,
    debounceDuration: Duration = TEXT_WATCHER_DEBOUNCE_TIME.milliseconds
): Flow<CharSequence> {
    return merge(
        editorActionEvents { it.actionId == editorAction }.map { text!! },
        keys { event ->
            event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
        }.map { text!! },
        textChanges().skipInitialValue()
    ).debounce(debounceDuration)
}

fun EditText.debounceAfterTextChanges(
    debounceDuration: Duration = TEXT_WATCHER_DEBOUNCE_TIME.milliseconds
): Flow<CharSequence> {
    return afterTextChanges()
        .skipInitialValue()
        .map { it.editable }
        .map { it.toString() }
        .debounce(debounceDuration)
        .distinctUntilChanged()
}

fun EditText.debounceTextChanged(
    debounceDuration: Duration = TEXT_WATCHER_DEBOUNCE_TIME.milliseconds
): Flow<CharSequence> {
    return textChanges()
        .skipInitialValue()
        .map { it.toString() }
        .debounce(debounceDuration)
        .distinctUntilChanged()
}
