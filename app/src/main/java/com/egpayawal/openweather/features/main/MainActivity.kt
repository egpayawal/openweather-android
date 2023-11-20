package com.egpayawal.openweather.features.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.egpayawal.common.base.BaseViewModelActivity
import com.egpayawal.common.extensions.getThemeColor
import com.egpayawal.common.extensions.makeSoftInputModeAdjustPan
import com.egpayawal.common.extensions.makeSoftInputModeAdjustResize
import com.egpayawal.common.extensions.makeStatusBarNonTransparent
import com.egpayawal.common.extensions.makeStatusBarTransparent
import com.egpayawal.common.extensions.setVisible
import com.egpayawal.common.utils.ViewUtils
import com.egpayawal.common.utils.collectViewModelState
import com.egpayawal.module.network.ext.getThrowableError
import com.egpayawal.openweather.MainGraphDirections
import com.egpayawal.openweather.R
import com.egpayawal.openweather.databinding.ActivityMainBinding
import com.chuckerteam.chucker.api.Chucker
import com.squareup.seismic.ShakeDetector
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    BaseViewModelActivity<ActivityMainBinding, MainViewModel>(),
    ShakeDetector.Listener {

    companion object {

        const val EXTRA_START_SCREEN = "EXTRA_START_SCREEN"
        const val EXTRA_EMAIL = "EXTRA_EMAIL"
        const val EXTRA_PHONE = "EXTRA_PHONE"

        const val START_MAIN = "START_MAIN"

        fun openActivity(context: Context, extras: Bundle) {
            context.startActivity(
                Intent(
                    context,
                    MainActivity::class.java
                ).apply {
                    putExtras(extras)
                }
            )
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleExtras()

        setupVmObservers()
        setupViews()
        setupBroadcastReceivers()
        setupShakeListener()
    }

    private fun setupShakeListener() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val shakeDetector = ShakeDetector(this)
        val sensorDelay = SensorManager.SENSOR_DELAY_GAME
        shakeDetector.start(sensorManager, sensorDelay)
    }

    override fun onDestroy() {
        unregisterReceiver(ioExceptionReceiver)
        super.onDestroy()
    }

    private val ioExceptionReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            viewModel.onIoExceptionReceived()
        }
    }

    private fun setupBroadcastReceivers() {
        ContextCompat
            .registerReceiver(
                this,
                ioExceptionReceiver,
                IntentFilter(
                    getString(com.egpayawal.module.network.R.string.intent_action_io_exception)
                ),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
    }

    override fun onResume() {
        super.onResume()
//        viewModel.versionCheck()
    }

    private fun setupViews() {
        navController().addOnDestinationChangedListener { _, destination, _ ->
            viewModel.onDestinationChanged(destination.id)
        }

        binding.bottomNavigation.apply {
            setupWithNavController(navController())
            setOnNavigationItemReselectedListener { /* do nothing */ }

            // https://github.com/material-components/material-components-android/issues/499
            setOnApplyWindowInsetsListener(null)
        }
    }

    private fun setupVmObservers() {
        viewModel
            .state
            .collectViewModelState(this) {
                handleState(it)
            }
    }

    private fun handleState(state: MainState) {
        when (state) {
            MainState.ShowBottomNavigation -> {
                binding.bottomNavigation setVisible true
            }

            MainState.HideBottomNavigation -> {
                binding.bottomNavigation setVisible false
            }

            MainState.ChangeStatusBarToDarkTransparent -> {
                makeStatusBarTransparent(false)
            }

            MainState.ChangeStatusBarToLightTransparent -> {
                makeStatusBarTransparent(true)
            }

            MainState.ChangeStatusBarToDefault -> {
                makeStatusBarNonTransparent()
                changeStatusBarColor(getThemeColor(android.R.attr.colorBackground))
            }

            MainState.ChangeSoftInputModeAdjustPan -> {
                makeSoftInputModeAdjustPan()
            }

            MainState.ChangeSoftInputModeDefault -> {
                makeSoftInputModeAdjustResize()
            }

            MainState.ShowNoInternetConnectionError -> {
                ViewUtils.showGenericErrorSnackBar(
                    binding.root,
                    getString(R.string.no_internet_connection_error)
                )
            }

            is MainState.Error -> {
                ViewUtils.showGenericErrorSnackBar(
                    binding.root,
                    state.message.getThrowableError()
                )
            }
        }
    }

    private fun changeStatusBarColor(@ColorInt color: Int) {
        if (window.statusBarColor != color) {
            window.statusBarColor = color
        }
    }

    private fun handleExtras() {
        intent?.extras?.let { bundle ->
            val startScreen = bundle.getString(EXTRA_START_SCREEN) ?: return

            require(startScreen.isNotEmpty()) {
                "EXTRA_START_SCREEN not found!"
            }

            when (startScreen) {
                START_MAIN -> {
                    navigate(
                        MainGraphDirections
                            .actionGlobalToHomeGraph(),
                        R.id.homeFragment
                    )
                }

//                START_ONBOARDING -> {
//                    navigate(
//                        MainGraphDirections
//                            .actionGlobalToOnboardingGraph(),
//                        com.appetiser.auth.R.id.inputNameFragment
//                    )
//                }
//
//                START_WALKTHROUGH -> {
//                    navigate(
//                        MainGraphDirections
//                            .actionGlobalToWalkthroughGraph(),
//                        com.appetiser.baseplate.onboarding.R.id.walkthrough_graph
//                    )
//                }

//                START_VERIFICATION -> {
//                    navController().navigateToRegisterVerificationCodeScreen(
//                        context = this,
//                        email = bundle.getString(EXTRA_EMAIL, ""),
//                        phone = bundle.getString(EXTRA_PHONE, "")
//                    )
//                }
//
//                START_INPUT_NAME -> {
//                    navController().navigateToInputNameScreen(
//                        context = this,
//                        hideBackButton = true,
//                        isExitOnBack = true
//                    )
//                }
//
//                START_UPLOAD_PROFILE_PHOTO -> {
//                    navController().navigateToUploadProfilePhotoScreen(
//                        context = this,
//                        hideBackButton = true,
//                        isExitOnBack = true
//                    )
//                }
            }
        }
    }

    private fun navigate(navDirections: NavDirections, destinationScreenId: Int) {
        if (navController().currentDestination!!.id != destinationScreenId) {
            navController()
                .navigate(navDirections)
        }
    }

    fun navController() = findNavController(R.id.nav_host_fragment)
    override fun hearShake() {
        startActivity(Chucker.getLaunchIntent(this))
    }
}
