package com.egpayawal.common.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    /**
     * True when UI hasn't called ViewModel.onCreate yet.
     *
     * When activity/fragment is being reloaded view model will check this flag
     * so it doesn't need to reload data.
     */
    private var isFirstTimeUiCreate: Boolean = true

    /**
     * Called after fragment/activity view is created.
     */
    @CallSuper
    open fun onCreate(bundle: Bundle?) {
        if (isFirstTimeUiCreate) {
            isFirstTimeUiCreate(bundle)
            isFirstTimeUiCreate = false
        }
    }

    /**
     * Called when fragment is FIRST created. Changing screen orientation
     * does not trigger this method again because of ViewModel's lifecycle.
     *
     * Use this in loading data for your fragment.
     */
    abstract fun isFirstTimeUiCreate(bundle: Bundle?)
}
