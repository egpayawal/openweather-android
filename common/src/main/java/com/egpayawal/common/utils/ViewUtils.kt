package com.egpayawal.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.core.text.HtmlCompat
import com.egpayawal.baseplate.common.R
import com.egpayawal.common.SNACKBAR_DURATION
import com.egpayawal.common.extensions.getThemeColor
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object ViewUtils {
    @SuppressLint("ResourceType")
    fun buildDatePickerDialog(
        year: Int,
        month: Int,
        day: Int,
        onDobSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
    ): MaterialDatePicker<Long> {
        val initialDate = LocalDateTime
            .of(
                LocalDate.of(year, month, day),
                LocalTime.now()
            )
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return MaterialDatePicker.Builder
            .datePicker()
            .setCalendarConstraints(
                CalendarConstraints.Builder().apply {
                    setValidator(
                        DateValidatorPointBackward.now()
                    )
                }.build()
            )
            .setSelection(initialDate)
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    val date = Instant
                        .ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    onDobSelected(
                        date.year,
                        date.monthValue,
                        date.dayOfMonth
                    )
                }
            }
    }

    fun showAlertDialog(
        context: Context,
        title: String?,
        body: String,
        dialogInterface: (DialogInterface, Int) -> Unit
    ) {
        if (body.isEmpty()) {
            return
        }

        val alertBuilder = MaterialAlertDialogBuilder(context)
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(HtmlCompat.fromHtml(body, HtmlCompat.FROM_HTML_MODE_LEGACY))
        alertBuilder.setCancelable(false)
        alertBuilder.setPositiveButton(context.getString(android.R.string.ok), dialogInterface)
        alertBuilder.create().show()
    }

    fun showConfirmDialog(
        context: Context,
        title: String = "",
        body: String,
        positiveButtonText: String,
        negativeButtonText: String = "",
        positiveClickListener: () -> Unit = {},
        negativeClickListener: () -> Unit = {}
    ) {
        val alertBuilder = MaterialAlertDialogBuilder(context)
        title.isNotEmpty().let {
            alertBuilder.setTitle(title)
        }
        alertBuilder.setMessage(HtmlCompat.fromHtml(body, HtmlCompat.FROM_HTML_MODE_LEGACY))

        alertBuilder
            .setPositiveButton(
                positiveButtonText
            ) { _, _ ->
                positiveClickListener()
            }

        if (negativeButtonText.isNotEmpty()) {
            alertBuilder
                .setNegativeButton(
                    negativeButtonText
                ) { _, _ ->
                    negativeClickListener()
                }
        }

        alertBuilder.setCancelable(false)
        alertBuilder.create().show()
    }

    fun showGenericSuccessSnackBar(
        parentView: View,
        text: String
    ) {
        showSuccessSnackBar(
            parentView,
            text,
            parentView
                .context
                .getString(
                    R.string.hide
                )
        ) {
            it.dismiss()
        }
    }

    fun showGenericErrorSnackBar(
        parentView: View,
        text: String
    ) {
        showErrorSnackBar(
            parentView,
            text,
            parentView
                .context
                .getString(
                    R.string.hide
                )
        ) {
            it.dismiss()
        }
    }

    @SuppressLint("WrongConstant")
    fun showSuccessSnackBar(
        parentView: View,
        text: String,
        actionText: String,
        actionClickListener: (Snackbar) -> Unit
    ): Snackbar {
        return Snackbar.make(parentView, text, SNACKBAR_DURATION)
            .apply {
                setAction(actionText) {
                    actionClickListener(this)
                }
                setTextColor(
                    parentView.context.getThemeColor(R.attr.colorOnAlertSuccess)
                )
                setActionTextColor(
                    parentView.context.getThemeColor(R.attr.colorOnAlertSuccess)
                )
                setBackgroundTint(
                    parentView.context.getThemeColor(R.attr.colorAlertSuccess)
                )
                show()
            }
    }

    @SuppressLint("WrongConstant")
    fun showErrorSnackBar(
        parentView: View,
        text: String,
        actionText: String,
        actionClickListener: (Snackbar) -> Unit
    ): Snackbar {
        return Snackbar.make(parentView, text, SNACKBAR_DURATION)
            .apply {
                setAction(actionText) {
                    actionClickListener(this)
                }
                setTextColor(
                    parentView.context.getThemeColor(R.attr.colorOnAlertError)
                )
                setActionTextColor(
                    parentView.context.getThemeColor(R.attr.colorOnAlertError)
                )
                setBackgroundTint(
                    parentView.context.getThemeColor(R.attr.colorAlertError)
                )
                show()
            }
    }
}
