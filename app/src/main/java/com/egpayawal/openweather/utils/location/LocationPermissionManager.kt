package com.egpayawal.openweather.utils.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Eraño Payawal on 10/24/20.
 * hunterxer31@gmail.com
 */
class LocationPermissionManager @Inject constructor() {

    sealed class Result {
        object Granted : Result()
        object Denied : Result()
    }

    fun requestLocationPermission(activity: FragmentActivity) : Single<Result> =
        RxPermissions(activity)
            .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .map {
                if (it) {
                    Result.Granted
                } else {
                    Result.Denied
                }
            }
            .singleOrError()

    fun requestLocationPermission(fragment: Fragment) : Single<Result> =
        RxPermissions(fragment)
            .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .map {
                if (it) {
                    Result.Granted
                } else {
                    Result.Denied
                }
            }
            .singleOrError()

    fun isLocationPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
