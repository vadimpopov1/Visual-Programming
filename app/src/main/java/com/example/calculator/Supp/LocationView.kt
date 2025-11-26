package com.example.calculator.Ui.Location

import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationView(
    private val latitudeView: TextView,
    private val longitudeView: TextView,
    private val altitudeView: TextView,
    private val timeView: TextView
) {

    fun updateLocationData(latitude: Double, longitude: Double, altitude: Double) {
        latitudeView.text = "%.6f".format(latitude)
        longitudeView.text = "%.6f".format(longitude)
        altitudeView.text = "%.2f".format(altitude)
        updateTime()
    }

    private fun updateTime() {
        timeView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }
}