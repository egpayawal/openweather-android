package com.egpayawal.openweather.features.main

import android.view.View

sealed class MainState {
    object ShowBottomNavigation : MainState()

    object HideBottomNavigation : MainState()

    object ChangeSoftInputModeAdjustPan : MainState()

    object ChangeSoftInputModeDefault : MainState()

    /**
     * Default status bar color.
     */
    object ChangeStatusBarToDefault : MainState()

    /**
     * Changes status bar to transparent with [View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR] flag set to false.
     */
    object ChangeStatusBarToDarkTransparent : MainState()

    /**
     * Changes status bar to transparent with [View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR] flag set to true.
     */
    object ChangeStatusBarToLightTransparent : MainState()

    object ShowNoInternetConnectionError : MainState()
    data class Error(val message: Throwable) : MainState()
}
