package com.egpayawal.common.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import com.egpayawal.baseplate.common.R
import com.egpayawal.common.extensions.toPx
import java.util.WeakHashMap

class StepsIndicatorView(
    context: Context,
    attributeSet: AttributeSet
) : LinearLayout(context, attributeSet) {

    companion object {
        const val DEFAULT_HEIGHT = 12
    }

    /**
     * Total number of steps.
     */
    private var totalProgressCount: Int = 4

    /**
     * Current progress count.
     */
    private var progressCount: Int = 0
    private var progressHeight = DEFAULT_HEIGHT

    @ColorInt
    private var progressColor: Int = 0

    @ColorInt
    private var progressEmptyColor: Int = 0

    @ColorInt
    private var progressBackgroundColor: Int = 0

    private var animating = false

    val selectedPosition: Int
        get() = progressCount

    private val weakProgressBarHashMap: WeakHashMap<Int, ProgressBar> = WeakHashMap()

    init {

        val array =
            context.obtainStyledAttributes(attributeSet, R.styleable.StepsIndicatorView)
        try {
            array.getInt(
                R.styleable.StepsIndicatorView_siv_totalProgressCount,
                totalProgressCount
            ).let {
                totalProgressCount = it
            }
            array.getInt(R.styleable.StepsIndicatorView_siv_progressCount, 1).let {
                progressCount = it
            }
            array.getColor(
                R.styleable.StepsIndicatorView_siv_progressColor,
                Color.BLUE
            ).let {
                progressColor = it
            }
            array.getColor(
                R.styleable.StepsIndicatorView_siv_progressEmptyColor,
                Color.LTGRAY
            ).let {
                progressEmptyColor = it
            }
            array.getColor(
                R.styleable.StepsIndicatorView_siv_backgroundColor,
                Color.WHITE
            ).let {
                progressBackgroundColor = it
            }
            array.getInt(R.styleable.StepsIndicatorView_siv_height, progressHeight).let {
                progressHeight = it
            }
        } finally {
            array.recycle()
        }

        if (progressCount > totalProgressCount) {
            throw IndexOutOfBoundsException("StepNumber must be lesser than StepSize")
        } else if (totalProgressCount <= 1) {
            throw IndexOutOfBoundsException("Step count must be more than 1")
        }

        orientation = HORIZONTAL
        setBackgroundColor(progressBackgroundColor)
        weightSum = totalProgressCount.toFloat()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupProgressStepViews()
    }

    override fun invalidate() {
        super.invalidate()
        setupProgressStepViews()
    }

    private fun setupProgressStepViews() {
        removeAllViewsInLayout()
        weakProgressBarHashMap.clear()

        for (i in 1..totalProgressCount) {
            val progressBar = ProgressBar(
                context,
                null,
                android.R.attr.progressBarStyleHorizontal
            )
            progressBar.progressDrawable =
                constructProgressDrawable(progressColor, progressEmptyColor)
            progressBar.id = i
            progressBar.max = 100
            progressBar.progress = if (i <= progressCount) 100 else 0
            val progressLayoutParams = LayoutParams(0, progressHeight.toPx(context), 1f)
            if (i == 1) {
                progressLayoutParams.setMargins(0, 0, 10, 0)
            } else {
                progressLayoutParams.setMargins(-progressHeight.toPx(context), 0, 10, 0)
            }

            progressBar.layoutParams = progressLayoutParams
            addView(progressBar)
            weakProgressBarHashMap[i] = progressBar
        }

        handleProgressBarTranslationZ()
    }

    private fun handleProgressBarTranslationZ() {
        weakProgressBarHashMap
            .forEach { (progressCount, progressBar) ->
                when {
                    progressCount <= this.progressCount -> {
                        progressBar.translationZ = progressCount.toFloat()
                    }

                    progressCount > this.progressCount -> {
                        progressBar.translationZ = -progressCount.toFloat()
                    }
                }
            }
    }

    private fun constructProgressDrawable(
        @ColorInt progressColor: Int,
        @ColorInt progressEmptyColor: Int
    ): LayerDrawable {
        val backgroundDrawable = buildProgressBackgroundDrawable(progressEmptyColor)
        val progressDrawable = buildProgressActiveDrawable(progressColor)

        val layers = arrayOf(
            backgroundDrawable,
            progressDrawable
        )

        val layerDrawable = LayerDrawable(layers)
        layerDrawable.setId(0, android.R.id.background)
        layerDrawable.setId(1, android.R.id.progress)

        return layerDrawable
    }

    private fun buildProgressBackgroundDrawable(
        progressEmptyColor: Int
    ): Drawable {
        val progressBackgroundDrawable =
            GradientDrawable()
                .apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(progressBackgroundColor)
                    cornerRadius = 100.toPx(context).toFloat()
                }
        val progressContentDrawable =
            GradientDrawable()
                .apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(progressEmptyColor)
                    cornerRadius = 100.toPx(context).toFloat()
                }
        val progressLayers = arrayOf(
            progressBackgroundDrawable,
            progressContentDrawable
        )
        val progressBackgroundAndContent = LayerDrawable(progressLayers)
        progressBackgroundAndContent
            .setLayerInset(
                1,
                4.toPx(context),
                4.toPx(context),
                4.toPx(context),
                4.toPx(context)
            )

        return progressBackgroundAndContent
    }

    private fun buildProgressActiveDrawable(
        progressColor: Int
    ): Drawable {
        val progressBackgroundDrawable =
            GradientDrawable()
                .apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(progressBackgroundColor)
                    cornerRadius = 100.toPx(context).toFloat()
                }
        val progressContentDrawable =
            GradientDrawable()
                .apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(progressColor)
                    cornerRadius = 100.toPx(context).toFloat()
                }
        val progressLayers = arrayOf(
            progressBackgroundDrawable,
            progressContentDrawable
        )
        val progressBackgroundAndContent = LayerDrawable(progressLayers)
        progressBackgroundAndContent
            .setLayerInset(
                1,
                4.toPx(context),
                4.toPx(context),
                4.toPx(context),
                4.toPx(context)
            )
        return ClipDrawable(
            progressBackgroundAndContent,
            Gravity.START,
            ClipDrawable.HORIZONTAL
        )
    }

    /**
     * Sets the total count of steps.
     *
     * @param value
     * @param delayValidation Set to true when we want to call invalidate manually.
     */
    fun setTotalProgressCount(value: Int, delayValidation: Boolean = false) {
        totalProgressCount = value
        weightSum = totalProgressCount.toFloat()

        if (!delayValidation) {
            invalidate()
        }
    }

    /**
     * Sets the current step count.
     *
     * @param value
     * @param delayValidation Set to true when we want to call invalidate manually.
     */
    fun setProgressCount(value: Int, delayValidation: Boolean = false) {
        progressCount = value

        if (!delayValidation) {
            invalidate()
        }
    }

    @Synchronized
    fun animateNext(): Int {
        checkView()

        if (progressCount < totalProgressCount) {
            progressCount++
            val progressBar =
                weakProgressBarHashMap[progressCount]
                    ?: throw KotlinNullPointerException("Progressbar must not be null!")
            handleProgressBarTranslationZ()
            setProgressAnimate(progressBar, 100)
        }

        return progressCount
    }

    @Synchronized
    fun animateBack(): Int {
        checkView()

        if (progressCount >= 1) {
            val progressBar =
                weakProgressBarHashMap[progressCount]
                    ?: throw KotlinNullPointerException("Progressbar must not be null!")
            progressCount--
            handleProgressBarTranslationZ()
            setProgressAnimate(progressBar, 0)
        }

        return progressCount
    }

    private fun setProgressAnimate(pb: ProgressBar, progressTo: Int) {
        val animation = ObjectAnimator.ofInt(pb, "progress", pb.progress, progressTo)
        animation.duration = 500
        animation.interpolator = DecelerateInterpolator()
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                animating = true
            }
            override fun onAnimationEnd(animation: Animator) {
                animating = false
            }
            override fun onAnimationCancel(animation: Animator) {
                animating = false
            }
            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        animation.start()
    }

    private fun checkView() {
        if (weakProgressBarHashMap.isEmpty()) {
            throw IllegalArgumentException("Progress item is empty")
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        weakProgressBarHashMap.clear()
        removeAllViewsInLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }
}
