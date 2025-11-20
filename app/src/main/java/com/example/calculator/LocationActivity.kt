package com.example.calculator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class LocationActivity : AppCompatActivity() {
    private lateinit var backButton: ImageView
    private lateinit var getButton: Button
    private lateinit var permissionButton: ImageView
    private lateinit var currentLatitude: TextView
    private lateinit var currentLongitude: TextView
    private lateinit var currentAltitude: TextView
    private lateinit var currentTime: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var isCollecting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        backButton = findViewById(R.id.button_back)
        permissionButton = findViewById(R.id.button_permission)
        currentLatitude = findViewById(R.id.cur_latitude)
        currentLongitude = findViewById(R.id.cur_longitude)
        currentAltitude = findViewById(R.id.cur_atlitude)
        currentTime = findViewById(R.id.cur_time)
        getButton = findViewById(R.id.button_get)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    currentLatitude.text = "%.6f".format(location.latitude)
                    currentLongitude.text = "%.6f".format(location.longitude)
                    currentAltitude.text = "%.2f".format(location.altitude)
                    currentTime.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    saveToJson(location)
                }
            }
        }

        backButton.setOnClickListener {
            stopCollecting()
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }

        permissionButton.setOnClickListener {
            requestLocationPermission()
        }

        getButton.setOnClickListener {
            toggleLocationUpdates()
        }
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            return
        }

        isCollecting = true
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        getLocation()
    }

    private fun stopCollecting() {
        isCollecting = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Разрешения получены.", Toast.LENGTH_SHORT).show()
                if (isCollecting) {
                    startCollecting()
                }
            } else {
                Toast.makeText(applicationContext, "Отказано пользователем.", Toast.LENGTH_SHORT).show()
                stopCollecting()
            }
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLatitude.text = "%.6f".format(it.latitude)
                currentLongitude.text = "%.6f".format(it.longitude)
                currentAltitude.text = "%.2f".format(it.altitude)
                currentTime.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                saveToJson(it)
            }
        }
    }

    private fun saveToJson(location: Location) {
        val file = File(filesDir, "locations_data.json")
        if (!file.exists()) {
            file.createNewFile()
            file.writeText("[]")
        }
        val jsonArray = JSONArray(file.readText())
        val locationObject = JSONObject().apply {
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("altitude", location.altitude)
            put("time", SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()))
        }
        jsonArray.put(locationObject)
        FileWriter(file).use {
            it.write(jsonArray.toString(4))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCollecting()
    }
}