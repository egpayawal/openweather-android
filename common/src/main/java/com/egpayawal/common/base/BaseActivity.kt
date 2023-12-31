package com.egpayawal.common.base

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.egpayawal.common.extensions.setVisible
import com.google.android.material.appbar.MaterialToolbar

/**
 * Automatically initializes ViewDataBinding class for your activity.
 */
abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity() {

    lateinit var binding: B

    protected var toolbar: Toolbar? = null

    @LayoutRes
    abstract fun getLayoutId(): Int

    open fun getCustomToolbar(): MaterialToolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            getLayoutId()
        )
        binding.lifecycleOwner = this

        setupToolbarAndStatusBar()
    }

    /**
     * @return true if should use back button on toolbar
     */
    protected open fun canBack(): Boolean {
        return false
    }

    fun hideToolbar() {
        if (toolbar != null) {
            toolbar?.setVisible(false)
        }
    }

    fun showToolbar() {
        if (toolbar != null) {
            toolbar?.setVisible(true)
        }
    }

    fun setToolbarBackgroundColor(@ColorRes color: Int) {
        if (toolbar != null) {
            toolbar?.setBackgroundColor(ContextCompat.getColor(this, color))
        }
    }

    private fun setupToolbarAndStatusBar() {
        getCustomToolbar()?.let {
            toolbar = it
            setSupportActionBar(toolbar)
            if (canBack()) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                // TODO add navigation click rxbinding
            }
        }
    }

    fun setToolbarHomeIndicatorIcon(@DrawableRes iconRes: Int) {
        supportActionBar?.setHomeAsUpIndicator(iconRes)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    fun enableToolbarHomeIndicator() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    fun disableToolbarBackButton() {
        toolbar?.navigationIcon = null
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
}
