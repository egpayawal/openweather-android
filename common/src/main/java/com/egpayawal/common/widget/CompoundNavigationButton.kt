package com.egpayawal.common.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import com.egpayawal.baseplate.common.R
import com.egpayawal.common.extensions.gone
import com.egpayawal.common.extensions.visible
import com.google.android.material.textview.MaterialTextView

/**
 * Component used for navigation purposes. Contains [leftIcon], [rightIcon], [title], and [description].
 */
class CompoundNavigationButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle) {

    private lateinit var container: ConstraintLayout
    private lateinit var imgLeftIcon: AppCompatImageView
    private lateinit var imgRightIcon: AppCompatImageView
    private lateinit var txtTitle: MaterialTextView
    private var titleColor: ColorStateList? = null
    private lateinit var txtDescription: MaterialTextView
    private lateinit var txtRightLabel: MaterialTextView
    private lateinit var bottomDivider: View

    private var title: String = ""
    private var description: String = ""
    private var rightLabel: String = ""
    private var leftIcon: Drawable? = null
    private var leftIconTint: ColorStateList? = null
    private var rightIcon: Drawable? = null
    private var rightIconTint: ColorStateList? = null
    private var hasBottomDivider: Boolean = false

    init {
        val attrs =
            context.obtainStyledAttributes(attributeSet, R.styleable.CompoundNavigationButton)

        try {
            title =
                attrs.getString(
                    R.styleable.CompoundNavigationButton_navigationButtonTitle
                ) ?: ""

            titleColor = attrs.getColorStateList(R.styleable.CompoundNavigationButton_navigationButtonTitleColor)
            description =
                attrs.getString(
                    R.styleable.CompoundNavigationButton_navigationButtonDescription
                ) ?: ""
            rightLabel =
                attrs.getString(
                    R.styleable.CompoundNavigationButton_navigationButtonRightLabel
                ) ?: ""
            leftIcon =
                attrs.getDrawable(
                    R.styleable.CompoundNavigationButton_navigationButtonLeftIcon
                )
            leftIconTint =
                attrs.getColorStateList(
                    R.styleable.CompoundNavigationButton_navigationButtonLeftIconTint
                )
            rightIcon =
                attrs.getDrawable(
                    R.styleable.CompoundNavigationButton_navigationButtonRightIcon
                )
            rightIconTint =
                attrs.getColorStateList(
                    R.styleable.CompoundNavigationButton_navigationButtonRightIconTint
                )
            hasBottomDivider =
                attrs.getBoolean(
                    R.styleable.CompoundNavigationButton_navigationButtonHasBottomDivider,
                    false
                )
        } finally {
            attrs.recycle()
            initViews()
        }
    }

    private fun initViews() {
        View.inflate(context, R.layout.component_compound_navigation_button, this)

        container = findViewById(R.id.container)
        imgLeftIcon = findViewById(R.id.imgLeftIcon)
        imgRightIcon = findViewById(R.id.imgRightIcon)
        txtTitle = findViewById(R.id.txtTitle)
        txtDescription = findViewById(R.id.txtDescription)
        txtRightLabel = findViewById(R.id.txtRightLabel)
        bottomDivider = findViewById(R.id.viewSeparator)

        setLeftIcon(leftIcon)
        setLeftIconTint(leftIconTint)
        setRightIcon(rightIcon)
        setRightIconTint(rightIconTint)
        txtTitle.text = title

        titleColor?.let {
            setTextTitleColor(it)
        }

        setDescription(description)
        setRightLabel(rightLabel)
        setHasBottomDivider(hasBottomDivider)
    }

    private fun setLeftIconTint(leftIconTint: ColorStateList?) {
        ImageViewCompat.setImageTintList(imgLeftIcon, leftIconTint)
    }

    private fun setRightIconTint(leftIconTint: ColorStateList?) {
        ImageViewCompat.setImageTintList(imgRightIcon, leftIconTint)
    }

    fun setRightLabel(label: String?) {
        if (label.isNullOrEmpty()) {
            txtRightLabel.gone()
        } else {
            txtRightLabel.text = label
            txtRightLabel.visible()
        }
    }

    fun setDescription(description: String?) {
        if (description.isNullOrEmpty()) {
            txtDescription.gone()
        } else {
            txtDescription.text = description
            txtDescription.visible()
        }
    }

    fun getDescription(): String {
        return description
    }

    fun setTextTitleColor(titleColor: ColorStateList) {
        txtTitle.setTextColor(titleColor)
    }

    fun setLeftIcon(drawable: Drawable?) {
        if (drawable != null) {
            imgLeftIcon.setImageDrawable(leftIcon)
            imgLeftIcon.visible()
        } else {
            imgLeftIcon.gone()
        }
    }

    fun setRightIcon(drawable: Drawable?) {
        if (drawable != null) {
            imgRightIcon.setImageDrawable(rightIcon)
            imgRightIcon.visible()
        } else {
            imgRightIcon.gone()
        }
    }

    fun setHasBottomDivider(value: Boolean) {
        bottomDivider.isVisible = value
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        container.setOnClickListener(l)
    }
}
