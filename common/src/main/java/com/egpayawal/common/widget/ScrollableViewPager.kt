package com.egpayawal.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.egpayawal.baseplate.common.R

class ScrollableViewPager : ViewPager {

    private var canScroll = true

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.ScrollableViewPager)
            try {
                array.getBoolean(R.styleable.ScrollableViewPager_canScroll, false).let {
                    canScroll = it
                }
            } finally {
                array.recycle()
            }
        }
        setCanScroll(canScroll)
    }

    fun setCanScroll(canScroll: Boolean) {
        this.canScroll = canScroll
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return canScroll && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return canScroll && super.onInterceptTouchEvent(ev)
    }

    class PageAdapter constructor(fm: FragmentManager, private val fragments: List<Fragment>) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return this.fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return this.fragments[position]
        }
    }
}
