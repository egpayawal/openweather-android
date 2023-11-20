package com.egpayawal.common.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import com.egpayawal.baseplate.common.R
import com.egpayawal.common.extensions.getEnum
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

open class CommonMainToolbar @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : AppBarLayout(context, attributeSet, defStyle) {

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbarView: MaterialToolbar

    private var style: MainTopBarStyle = MainTopBarStyle.MAIN
    private var title: String = ""
    private var titleColor: ColorStateList? = null
    private var backgroundColor: ColorStateList? = null

    init {
        val attrs =
            context.obtainStyledAttributes(attributeSet, R.styleable.CommonMainToolbar)
        try {
            style = attrs.getEnum(R.styleable.CommonMainToolbar_toolbarCustomStyles, MainTopBarStyle.MAIN)
            title = attrs.getString(R.styleable.CommonMainToolbar_toolbarTitle) ?: ""

            titleColor = attrs.getColorStateList(R.styleable.CommonMainToolbar_toolbarTitleColor)

            backgroundColor =
                attrs.getColorStateList(R.styleable.CommonMainToolbar_toolbarBackgroundColor)
        } finally {
            attrs.recycle()
            initViews()
        }
    }

    open fun initViews() {
        when (style) {
            MainTopBarStyle.MAIN -> {
                View.inflate(context, R.layout.common_main_toolbar, this)
            }
            MainTopBarStyle.LARGE -> {
                View.inflate(context, R.layout.common_main_large_toolbar, this)
            }
            MainTopBarStyle.SUB -> {
                View.inflate(context, R.layout.common_main_sub_toolbar, this)
            }
        }

        appBarLayout = findViewById(R.id.appBarLayout)
        toolbarView = findViewById(R.id.toolbarView)

        setToolbarTitle(title)

        backgroundColor?.let {
            setToolbarBackgroundColor(it)
        }

        titleColor?.let {
            setTitleColor(it)
        }
    }

    fun setToolbarTitle(title: String) {
        toolbarView.title = title
    }

    fun setTitleColor(titleColor: ColorStateList) {
        toolbarView.setTitleTextColor(titleColor)
    }

    fun setToolbarBackgroundColor(backgroundColor: ColorStateList) {
        toolbarView.backgroundTintList = backgroundColor
    }

    fun getToolbar(): MaterialToolbar {
        return toolbarView
    }

    enum class MainTopBarStyle {
        LARGE,
        MAIN,
        SUB
    }
}
