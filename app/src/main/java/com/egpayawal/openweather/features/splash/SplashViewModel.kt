package com.egpayawal.openweather.features.splash

import android.os.Bundle
import com.egpayawal.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
) : BaseViewModel() {

    override fun isFirstTimeUiCreate(bundle: Bundle?) {
    }
}
