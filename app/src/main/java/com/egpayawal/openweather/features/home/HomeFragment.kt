package com.egpayawal.openweather.features.home

import android.location.Location
import android.os.Bundle
import android.view.View
import com.egpayawal.common.base.BaseViewModelFragment
import com.egpayawal.common.extensions.loadDrawable
import com.egpayawal.common.utils.ViewUtils
import com.egpayawal.common.utils.collectViewModelState
import com.egpayawal.module.network.ext.getThrowableError
import com.egpayawal.openweather.R
import com.egpayawal.openweather.databinding.FragmentHomeBinding
import com.egpayawal.openweather.ext.loadIcon
import com.egpayawal.openweather.utils.location.LocationPermissionManager
import com.egpayawal.openweather.utils.location.LocationType
import com.egpayawal.openweather.utils.location.LocationUpdates
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

/**
 * @author Eraño Payawal
 */
@AndroidEntryPoint
class HomeFragment : BaseViewModelFragment<FragmentHomeBinding, HomeViewModel>() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var locationPermissionManager: LocationPermissionManager

    @Inject
    lateinit var locationUpdates: LocationUpdates

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationUpdates.initialize(savedInstanceState)
        locationUpdates.connect(
            onUpdateLocationUI = ::onUpdateLocationUI
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setOnRefreshListener {

        }

        setUpViewModels()
        initLocation()
        requestLocation()
    }

    private fun setUpViewModels() {
        viewModel
            .state
            .collectViewModelState(viewLifecycleOwner) {
                handleState(it)
            }
    }

    private fun handleState(state: HomeState) {
        when (state) {
            is HomeState.Error -> {
                ViewUtils.showGenericErrorSnackBar(
                    binding.root,
                    state.throwable.getThrowableError()
                )
            }
            is HomeState.ShowProgressLoading -> {
                binding.swipeRefreshLayout.isRefreshing = true
            }
            is HomeState.HideProgressLoading -> {
                binding.swipeRefreshLayout.isRefreshing = false
            }
            is HomeState.DisplayLocation -> {
                binding.txtCurrentLocation.text = state.location
            }
            is HomeState.DisplayTemperature -> {
                binding.txtTemperature.text = state.temp
            }
            is HomeState.DisplayWeather -> {
                binding.txtWeather.text = state.weather?.main.orEmpty()
                binding.weatherIcon.loadIcon(state.weather?.icon.orEmpty())
            }
            is HomeState.DisplaySunrise -> {
                binding.txtSunrise.text = state.sunrise
                binding.txtSunsetLbl.text = state.sunset
            }
            is HomeState.DisplayDay -> {
                if (state.isEvening) {
                    binding.imageViewBg.loadDrawable(R.drawable.bg_home_pm)
                    binding.imageViewCenter.loadDrawable(R.drawable.img_moon)
                } else {
                    binding.imageViewBg.loadDrawable(R.drawable.bg_home_am)
                    binding.imageViewCenter.loadDrawable(R.drawable.img_sun)
                }
            }
        }
    }

    private fun onUpdateLocationUI(location: Location?) {
        Timber.tag("DEBUG").e("HomeFragment onUpdateLocationUI::location:: $location")
        location?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            location(LocationType.EnableLocation, latLng)
            // after getting latest location disable the location update
            stopLocation()
            // if I received first the user current location
            // the handler needs to stop right away
//            stopLocationTimer()
        }
    }

    private fun location(locationType: LocationType, latLng: LatLng?) {
        when (locationType) {
            is LocationType.EnableLocation -> {
                if (latLng == null) return

                viewModel.getCurrentWeather(latLng.latitude.toString(), latLng.longitude.toString())
            }
            else -> {}
        }
    }

    override fun onDestroy() {
        locationUpdates.destroy()
        super.onDestroy()
    }

    private fun stopLocation() {
        if (locationPermissionManager.isLocationPermissionGranted(requireContext())) {
            // Remove location updates to save battery.
            locationUpdates.stopUpdatesHandler()
        }
    }

    private fun startLocation() {
        if (locationPermissionManager.isLocationPermissionGranted(requireContext())) {
            locationUpdates.startUpdatesHandler()
        }
    }

    private fun requestLocation() {
        locationPermissionManager
            .requestLocationPermission(this)
            .subscribe({ result ->
                when (result) {
                    is LocationPermissionManager.Result.Granted -> {
                        //utils.saveEnableLocation(true)
                        initLocation()
                    }
                    is LocationPermissionManager.Result.Denied -> {
//                        utils.saveEnableLocation(false)
                        Timber.tag("DEBUG").e("Location permission Denied")
                    }
                    else -> Timber.tag("DEBUG").e("Location permission no response")
                }
            }, Timber::e)
            .addTo(compositeDisposable)
    }

    private fun initLocation() {
//        Timber.tag("DEBUG").e("default location:: ${utils.location}")
        // this is the first to call when home screen rendered
        // check if the location permission is granted
        if (locationPermissionManager.isLocationPermissionGranted(requireContext())) {
            // start location update callback
            startLocation()
            // if start location update didn't get the current location
            // start location timer will trigger the api regardless of user location
//            startLocationTimer()
        } else {
//            viewModel.getCurrentWeather(utils.getLatLngFromAddress(requireContext(),utils.location!!))
        }
    }
}
