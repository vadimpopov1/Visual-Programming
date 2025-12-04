package com.example.calculator.supp

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionManager {
    const val LOCATION_PERMISSION_REQUEST_CODE = 100

    fun checkLocationPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_PHONE_STATE), LOCATION_PERMISSION_REQUEST_CODE)
    }

    fun isPermissionGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}