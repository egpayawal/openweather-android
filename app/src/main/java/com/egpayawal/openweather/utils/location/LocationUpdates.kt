package com.egpayawal.openweather.utils.location

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import timber.log.Timber
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by Eraño Payawal on 3/2/21.
 * hunterxer31@gmail.com
 */
class LocationUpdates @Inject constructor(private val application: Application) {

    companion object {
        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        const val UPDATE_INTERVAL_IN_MILLISECONDS = 300000L

        /**
         * The fastest rate for active location updates. Exact. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = UPDATE_INTERVAL_IN_MILLISECONDS / 2

        // Keys for storing activity state in the Bundle.
        private const val KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates"
        private const val KEY_LOCATION = "location"
        private const val KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string"
    }

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Provides access to the Location Settings API.
     */
    private var mSettingsClient: SettingsClient? = null

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    /**
     * Callback for Location events.
     */
    private var mLocationCallback: LocationCallback? = null

    /**
     * Represents a geographical location.
     */
    private var mCurrentLocation: Location? = null

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private var mRequestingLocationUpdates: Boolean = false

    /**
     * Time when the location was updated represented as a String.
     */
    private var mLastUpdateTime: String? = null

    private lateinit var updateLocationUI: (location: Location) -> Unit

    fun connect(onUpdateLocationUI: (location: Location) -> Unit) {
        this.updateLocationUI = onUpdateLocationUI
    }

    /**
     * Updates all UI fields.
     */
    private fun updateUI() {
//        setButtonsEnabledState()
        mCurrentLocation?.let { updateLocationUI.invoke(it) }
    }

    /**
     * This should be called in onCreate
     */
    fun initialize(savedInstanceState: Bundle?) {
        mRequestingLocationUpdates = false
        mLastUpdateTime = ""

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
        mSettingsClient = LocationServices.getSettingsClient(application)

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
    }

    fun destroy() {
        mRequestingLocationUpdates = false
        mLastUpdateTime = null
        mFusedLocationClient = null
        mSettingsClient = null
        mLocationCallback = null
        mCurrentLocation = null
        mLocationRequest = null
        mLocationSettingsRequest = null
        Timber.tag("DEBUG").e("Location Destroy")
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet()
                    .contains(KEY_REQUESTING_LOCATION_UPDATES)
            ) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES)
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING)
            }
            updateUI()
        }
    }

    /**
     * Creates a callback for receiving location events.
     */
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                mCurrentLocation = locationResult.lastLocation
                mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
                Timber.tag("Location").e("createLocationCallback")
                updateLocationUI.invoke(mCurrentLocation!!)
//                updateLocationUI()
            }
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     *
     *
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     *
     *
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Uses a [com.google.android.gms.location.LocationSettingsRequest.Builder] to build
     * a [com.google.android.gms.location.LocationSettingsRequest] that is used for checking
     * if a device has the needed location settings.
     */
    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    fun startUpdatesHandler() {
        Timber.tag("DEBUG").e("startUpdatesHandler::mRequestingLocationUpdates:: $mRequestingLocationUpdates")
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true
//            setButtonsEnabledState()
            Timber.tag("DEBUG").e("startUpdatesHandler:: starting location...")
            startLocationUpdates()
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */
    fun stopUpdatesHandler() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates()
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mLocationSettingsRequest?.let {
            mSettingsClient?.checkLocationSettings(it)
                ?.addOnSuccessListener {
                    Timber.tag("Location").e("All location settings are satisfied.")
                    mLocationRequest?.let { it1 ->
                        mLocationCallback?.let { it2 ->
                            mFusedLocationClient?.requestLocationUpdates(
                                it1,
                                it2,
                                Looper.myLooper()
                            )
                        }
                    }
                    updateUI()
                }
                ?.addOnFailureListener { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Timber.tag("Location")
                                .e("Location settings are not satisfied. Attempting to upgrade location settings")
                            // Emit location permission
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                            Timber.tag("Location").e(errorMessage)
                            Toast.makeText(application, errorMessage, Toast.LENGTH_LONG).show()
                            mRequestingLocationUpdates = false
                        }
                    }
                    updateUI()
                }
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private fun stopLocationUpdates() {
        Timber.tag("DEBUG").e("stopLocationUpdates::mRequestingLocationUpdates:: $mRequestingLocationUpdates")
        if (!mRequestingLocationUpdates) {
            Timber.tag("Location").e("stopLocationUpdates: updates never requested, no-op.")
            return
        }

        Timber.tag("DEBUG").e("removing location updates...")

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mLocationCallback?.let {
            mFusedLocationClient?.removeLocationUpdates(it)
                ?.addOnCompleteListener {
                    mRequestingLocationUpdates = false
    //                setButtonsEnabledState()
                }
        }
    }

    /**
     * Stores activity data in the Bundle.
     */
    fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates)
        savedInstanceState?.putParcelable(KEY_LOCATION, mCurrentLocation)
        savedInstanceState?.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime)
    }
}
