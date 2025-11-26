package com.example.calculator.Services

import android.content.Context
import com.google.android.gms.location.*

class LocationService(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.create().apply {
        setInterval(5000)
        setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private var locationCallback: LocationCallback? = null
    private var isCollecting = false

    fun startLocationUpdates() {
        if (isCollecting) return

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, null)
        isCollecting = true
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback!!)
        locationCallback = null
        isCollecting = false
    }

    fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { }
    }

    fun isCollecting(): Boolean = isCollecting

    fun setLocationCallback(callback: LocationCallback) {
        locationCallback = callback
    }
}