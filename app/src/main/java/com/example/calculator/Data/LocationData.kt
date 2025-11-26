package com.example.calculator.data

import java.util.Date

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val timestamp: Date = Date()
)