package com.egpayawal.common.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.egpayawal.baseplate.common.R
import com.egpayawal.common.extensions.getEnum
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class CommonSubToolbar @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : Toolbar(context, attributeSet, defStyle) {

    private lateinit var toolbarView: MaterialToolbar
    private lateinit var toolbarButton: MaterialTextView
    private lateinit var toolbarTitle: MaterialTextView

    private var title = ""
    private var centerTitle = false
    private var navigationIcon: Drawable? = null
    private var textAppearance: Int = R.attr.textHeadlineStyle

    private var buttonText: String = ""
    private var buttonVisibility = VISIBILITY.VISIBLE
    private var buttonTextColor: ColorStateList? = null
    private var buttonBackground: Drawable? = null
    private var toolbarMenuId: Int? = null
    private var menu: Menu? = null
    private var gravity = GravityType.NONE

    init {
        val attrs = context
            .obtainStyledAttributes(attributeSet, R.styleable.CommonSubToolbar)
        val mainAttrs = context
            .obtainStyledAttributes(attributeSet, R.styleable.CommonMainToolbar)
        try {
            title = mainAttrs.getString(R.styleable.CommonMainToolbar_toolbarTitle) ?: ""
            centerTitle = attrs.getBoolean(R.styleable.CommonSubToolbar_toolbarTitleCentered, false)
            navigationIcon = attrs.getDrawable(R.styleable.CommonSubToolbar_toolbarNavigationIcon)

            buttonText = attrs.getString(R.styleable.CommonSubToolbar_buttonText) ?: ""
            buttonVisibility =
                attrs.getEnum(R.styleable.CommonSubToolbar_buttonVisibility, VISIBILITY.VISIBLE)
            buttonTextColor = attrs.getColorStateList(R.styleable.CommonSubToolbar_buttonTextColor)
            buttonBackground = attrs.getDrawable(R.styleable.CommonSubToolbar_buttonBackground)

            toolbarMenuId = mainAttrs.getResourceId(R.styleable.CommonMainToolbar_toolbarMenu, 0)
        } finally {
            attrs.recycle()
            mainAttrs.recycle()
            initViews()
        }
    }

    private fun initViews() {
        View.inflate(context, R.layout.common_sub_toolbar, this)

        toolbarView = findViewById(R.id.toolbarView)

        toolbarButton = toolbarView.findViewById(R.id.toolbarButton)
        toolbarTitle = toolbarView.findViewById(R.id.txtToolbarCustomTitle)
        toolbarTitle.text = title
        toolbarView.navigationIcon = navigationIcon
        setUpToolbarButton()
    }

    override fun inflateMenu(resId: Int) {
        toolbarMenuId?.let {
            if (it == resId) {
                menu = getMenu()
            }
        }
        super.inflateMenu(toolbarMenuId!!)
    }

    fun setUpToolbarButton() {
        setButtonText(buttonText)
        buttonTextColor?.let {
            setButtonTextColor(it)
        }
        buttonBackground?.let {
            setButtonBackground(it)
        }
        setButtonVisibility(buttonVisibility)
    }

    fun setButtonBackground(drawable: Drawable) {
        toolbarButton.background = drawable
    }

    fun setButtonTextColor(buttonTextColor: ColorStateList?) {
        toolbarButton.setTextColor(buttonTextColor)
    }

    fun setButtonVisibility(rightButtonVisibility: VISIBILITY) {
        toolbarButton.visibility = when (rightButtonVisibility) {
            VISIBILITY.VISIBLE -> {
                View.VISIBLE
            }
            VISIBILITY.GONE -> {
                View.GONE
            }
            VISIBILITY.INVISIBLE -> {
                View.INVISIBLE
            }
            else -> {
                throw IllegalArgumentException("Unsupported visibility")
            }
        }
    }
    fun setButtonText(rightButtonText: String) {
        toolbarButton.text = rightButtonText
    }

    fun getToolbar(): MaterialToolbar {
        return toolbarView
    }

    enum class VISIBILITY {
        VISIBLE,
        GONE,
        INVISIBLE
    }

    enum class GravityType {
        START,
        CENTER,
        END,
        NONE
    }
}
