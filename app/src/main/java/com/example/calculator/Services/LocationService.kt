package com.example.calculator.Services

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
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

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
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

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { }
    }

    fun isCollecting(): Boolean = isCollecting

    fun setLocationCallback(callback: LocationCallback) {
        locationCallback = callback
    }
}