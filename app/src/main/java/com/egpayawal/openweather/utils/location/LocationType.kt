package com.egpayawal.openweather.utils.location

/**
 * Created by Eraño Payawal on 5/24/21.
 * hunterxer31@gmail.com
 */
sealed class LocationType {
    object MyLocation : LocationType()
    object EnableLocation : LocationType()

}
