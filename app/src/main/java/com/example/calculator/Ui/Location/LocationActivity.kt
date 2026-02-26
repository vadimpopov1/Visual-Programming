package com.example.calculator.Ui.Location

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.data.LocationData
import com.example.calculator.R
import com.example.calculator.Services.LocationService
import com.example.calculator.Ui.Hub.MainMenu
import com.example.calculator.data.local.JsonSave
import com.example.calculator.supp.PermissionManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.util.Date

class LocationActivity : AppCompatActivity() {
    private lateinit var backButton: ImageView
    private lateinit var getButton: Button
    private lateinit var permissionButton: ImageView
    private lateinit var locationView: LocationView
    private lateinit var locationService: LocationService
    private lateinit var locationCallback: LocationCallback
    private var isCollecting = false
    private var currentLocationData: LocationData? = null
    private var deviceIdentifier: String? = null

    val EXTRA_DEVICE_IDENTIFIER = "device_identifier"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        deviceIdentifier = intent.getStringExtra(EXTRA_DEVICE_IDENTIFIER)

        backButton = findViewById(R.id.button_back)
        permissionButton = findViewById(R.id.button_permission)
        getButton = findViewById(R.id.button_get)
        locationView = LocationView(findViewById(R.id.cur_latitude), findViewById(R.id.cur_longitude), findViewById(R.id.cur_atlitude), findViewById(R.id.cur_time))
        locationService = LocationService(this)

        setupLocationCallback()

        backButton.setOnClickListener {
            stopCollecting()
            navigateToMainMenu()
        }

        permissionButton.setOnClickListener {
            PermissionManager.requestLocationPermission(this)
        }

        getButton.setOnClickListener {
            toggleLocationUpdates()
        }
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val locationData = LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitude = location.altitude,
                        accuracy = location.accuracy,
                        timestamp = Date(),
                        imei = deviceIdentifier
                    )
                    locationView.updateLocationData(
                        locationData.latitude,
                        locationData.longitude,
                        locationData.altitude
                    )
                    JsonSave.saveToJson(locationData)
                }
            }
        }
        locationService.setLocationCallback(locationCallback)
    }

    private fun toggleLocationUpdates() {
        if (isCollecting) {
            stopCollecting()
            getButton.text = "GET"
            Toast.makeText(this, "Сбор данных остановлен", Toast.LENGTH_SHORT).show()
        } else {
            startCollecting()
            getButton.text = "STOP"
            Toast.makeText(this, "Сбор данных запущен", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCollecting() {
        if (!PermissionManager.checkLocationPermission(this)) {
            PermissionManager.requestLocationPermission(this)
            return
        }

        isCollecting = true
        getCurrentLocation()
        locationService.startLocationUpdates()
    }

    private fun stopCollecting() {
        isCollecting = false
        locationService.stopLocationUpdates()
    }

    private fun getCurrentLocation() {
        if (!PermissionManager.checkLocationPermission(this)) {
            return
        }

        locationService.getCurrentLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionManager.LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionManager.isPermissionGranted(grantResults)) {
                Toast.makeText(this, "Разрешения получены", Toast.LENGTH_SHORT).show()
                if (isCollecting) {
                    startCollecting()
                }
            } else {
                Toast.makeText(this, "Отказано пользователем", Toast.LENGTH_SHORT).show()
                stopCollecting()
            }
        }
    }

    private fun navigateToMainMenu() {
        val intent = Intent(this, MainMenu::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCollecting()
    }
}