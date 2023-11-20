package com.egpayawal.openweather.features.main

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.egpayawal.common.base.BaseViewModel
import com.egpayawal.common.utils.launchWithTimber
import com.egpayawal.openweather.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : BaseViewModel() {

    private val _state = MutableSharedFlow<MainState>()

    val state: Flow<MainState> = _state

    private val bottomNavDestinationIds by lazy {
        arrayOf(
            R.id.homeFragment,
            R.id.aboutFragment
        )
    }

    override fun isFirstTimeUiCreate(bundle: Bundle?) = Unit

    fun onDestinationChanged(destinationId: Int) {
        handleBottomNavigation(destinationId)
        handleStatusBar(destinationId)
        handleSoftInputMode(destinationId)
    }

    private fun handleSoftInputMode(destinationId: Int) {
        viewModelScope.launchWithTimber {
            when (destinationId) {
//                in adjustPanSoftInputModeDestinationIds -> {
//                    _state
//                        .emit(
//                            MainState.ChangeSoftInputModeAdjustPan
//                        )
//                }
                else -> {
                    _state
                        .emit(
                            MainState.ChangeSoftInputModeDefault
                        )
                }
            }
        }
    }

    private fun handleStatusBar(destinationId: Int) {
        viewModelScope.launchWithTimber {
            when (destinationId) {
//                in transparentStatusBarDestinationIds -> {
//                    _state
//                        .emit(
//                            MainState.ChangeStatusBarToDarkTransparent
//                        )
//                }
                else -> {
                    _state
                        .emit(
                            MainState.ChangeStatusBarToDefault
                        )
                }
            }
        }
    }

    private fun handleBottomNavigation(destinationId: Int) {
        viewModelScope.launchWithTimber {
            when (destinationId) {
                in bottomNavDestinationIds -> {
                    _state
                        .emit(
                            MainState.ShowBottomNavigation
                        )
                }
                else -> {
                    _state
                        .emit(
                            MainState.HideBottomNavigation
                        )
                }
            }
        }
    }

    fun onIoExceptionReceived() {
        viewModelScope.launchWithTimber {
            _state.emit(MainState.ShowNoInternetConnectionError)
        }
    }
}
