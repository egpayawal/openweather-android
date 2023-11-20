package com.egpayawal.common.extensions

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.BindingAdapter
import com.egpayawal.common.utils.throttleFirst
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.view.touches
import kotlin.time.Duration.Companion.milliseconds

suspend fun View.onDoubleClick(action: () -> Unit) = coroutineScope {
    channelFlow {
        val gestureDetector = GestureDetector(
            this@onDoubleClick.context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean = true
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    return trySendBlocking(Unit).isSuccess
                }
            }
        )
        this@onDoubleClick.touches().collect(gestureDetector::onTouchEvent)
    }.flowOn(Dispatchers.Main.immediate).collect { action() }
}

/** Milliseconds used for UI animations */
const val ANIMATION_FAST_MILLIS = 100L
const val ANIMATION_SLOW_MILLIS = 300L
const val NINJA_TAP_THROTTLE_TIME = 400L
const val TEXT_WATCHER_DEBOUNCE_TIME = 500L

/** Combination of all flags required to put activity into immersive mode */
const val FLAGS_FULLSCREEN =
    (
        View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            or View.SYSTEM_UI_FLAG_IMMERSIVE
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

const val NORMAL_SCREEN = (
    View.SYSTEM_UI_FLAG_LOW_PROFILE or
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    )

fun View.toggleSystemUiVisibility() {
    var uiOptions = this.systemUiVisibility
    uiOptions = uiOptions and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
    uiOptions = uiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
    uiOptions = uiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
    uiOptions = uiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
    this.systemUiVisibility = uiOptions
}

@BindingAdapter("setVisible")
infix fun View.setVisible(isVisible: Boolean?) {
    if (isVisible == true && visibility == View.VISIBLE) {
        return
    }

    if (isVisible == false && visibility == View.GONE) {
        return
    }

    this.visibility = when (isVisible) {
        false -> View.GONE
        else -> View.VISIBLE
    }
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

@BindingAdapter("setHidden")
fun View.setHidden(isHidden: Boolean) {
    this.visibility = if (isHidden) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}

inline fun View.enableWhen(editText: EditText, crossinline predicate: (CharSequence) -> Boolean) {
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

/**
 * Blocks repeated clicks
 * @param scope used to scope this listener to
 */
inline fun View.ninjaTap(scope: CoroutineScope, crossinline action: (View) -> Unit) {
    scope.launch {
        ninjaTap(action)
    }
}

/**
 * Suspend version of [View.ninjaTap]. Should be called in its own coroutine builder ([launch]) as
 * this suspends indefinitely.
 */
suspend inline fun View.ninjaTap(crossinline action: (View) -> Unit) {
    this@ninjaTap.clicks()
        .throttleFirst(NINJA_TAP_THROTTLE_TIME.milliseconds)
        .collect {
            action.invoke(this@ninjaTap)
        }
}

@BindingAdapter("loadingState")
fun ShimmerFrameLayout.setLoadingVisibility(isLoading: Boolean) {
    visibility = if (isLoading) {
        startShimmer()
        View.VISIBLE
    } else {
        stopShimmer()
        View.GONE
    }
}

inline fun View.afterMeasured(crossinline f: View.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

fun View.showKeyboard(activity: Activity) {
    WindowInsetsControllerCompat(activity.window, this).let { controller ->
        controller.show(WindowInsetsCompat.Type.ime())
    }
}

fun View.hideKeyboard(activity: Activity) {
    WindowInsetsControllerCompat(activity.window, this).let { controller ->
        controller.hide(WindowInsetsCompat.Type.ime())
    }
}

fun View.hideKeyboardClearFocus(activity: Activity) {
    hideKeyboard(activity)
    clearFocus()
}
