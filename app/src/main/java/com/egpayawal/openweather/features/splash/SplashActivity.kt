package com.egpayawal.openweather.features.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.os.bundleOf
import com.egpayawal.common.base.BaseViewModelActivity
import com.egpayawal.openweather.R
import com.egpayawal.openweather.databinding.ActivitySplashBinding
import com.egpayawal.openweather.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseViewModelActivity<ActivitySplashBinding, SplashViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                MainActivity.openActivity(
                    this,
                    bundleOf(MainActivity.EXTRA_START_SCREEN to MainActivity.START_MAIN)
                )
                finishAffinity()
            }, 3000
        )
    }
}
