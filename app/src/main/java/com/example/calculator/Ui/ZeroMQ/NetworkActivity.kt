package com.example.calculator.Ui.ZeroMQ

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.calculator.R
import com.example.calculator.Services.LocationService
import com.example.calculator.data.LocationData
import com.example.calculator.data.remote.LocationDataSender
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.util.Date

class NetworkActivity : AppCompatActivity() {
    private lateinit var SuccessCounter: TextView
    private lateinit var Message: TextView
    private lateinit var IpAddress: EditText
    private lateinit var startButton: Button

    private lateinit var Handler: Handler
    private var counter = 0
    private var isSending = false
    private var sendingThread: Thread? = null
    private var timerThread: Thread? = null

    private lateinit var locationService: LocationService
    private lateinit var locationDataSender: LocationDataSender

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var currentLocationData: LocationData? = null

    private var deviceIdentifier: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)

        SuccessCounter = findViewById(R.id.tv_counter)
        Message = findViewById(R.id.tv_message)
        IpAddress = findViewById(R.id.et_ip_address)
        startButton = findViewById(R.id.btn_start)
        Handler = Handler(Looper.getMainLooper())

        locationService = LocationService(this)

        getDeviceIdentifier()

        updateButtonText()

        startButton.setOnClickListener {
            if (!isSending) {
                checkPermissionsAndStart()
            } else {
                stopSending()
            }
        }
    }

    private fun getDeviceIdentifier() {
        deviceIdentifier = getAndroidId()
    }

    private fun getAndroidId(): String {
        return try {
            Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            "None"
        }
    }

    private fun checkPermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            startSending()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSending()
            } else {
                Toast.makeText(this, "Необходимо разрешение на доступ к местоположению", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateIpAddress(): Boolean {
        val ipText = IpAddress.text.toString().trim()

        if (ipText.isEmpty()) {
            IpAddress.error = "Введите IP адрес"
            return false
        }

        if (!ipText.contains(":") || ipText.split(":").size != 2) {
            IpAddress.error = "Неверный формат. Пример: 192.168.0.176:5555"
            return false
        }

        val parts = ipText.split(":")
        val ipParts = parts[0].split(".")

        if (ipParts.size != 4) {
            IpAddress.error = "Неверный IP адрес"
            return false
        }

        val port = parts[1].toIntOrNull()
        if (port == null || port < 1 || port > 65535) {
            IpAddress.error = "Порт должен быть от 1 до 65535"
            return false
        }

        IpAddress.error = null
        return true
    }

    private fun startSending() {
        val serverIp = IpAddress.text.toString().trim()

        if (!validateIpAddress()) {
            return
        }

        locationDataSender = LocationDataSender(serverIp)

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    currentLocationData = LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitude = location.altitude,
                        accuracy = location.accuracy,
                        timestamp = Date(),
                        imei = deviceIdentifier,
                    )
                }
            }
        }

        locationService.setLocationCallback(locationCallback)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationService.startLocationUpdates()
        }

        isSending = true
        updateButtonText()
        counter = 0

        timerThread = Thread {
            try {
                Thread.sleep(10000)
                if (isSending && counter == 0) {
                    Handler.post {
                        Toast.makeText(this, "Нет подключения к серверу", Toast.LENGTH_LONG).show()
                    }
                    stopSending()
                }
            } catch (e: Exception) {
            }
        }
        timerThread?.start()

        sendingThread = Thread {
            try {
                while (isSending) {
                    try {
                        Thread.sleep(1000)

                        if (ActivityCompat.checkSelfPermission(
                                this@NetworkActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            locationService.getCurrentLocation()
                        }
                        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        var cellInfoList = telephonyManager.allCellInfo
//                        if (cellInfoList != null) {
//                            for (it in cellInfoList) {
//                                when (it) {
//                                    is CellInfoLte -> {
//                                        cellInfoList = "${it.cellSignalStrength}"
//                                    }
//                                }
//                            }
//                        }
                        currentLocationData?.let { locationData ->
                            val reply = locationDataSender.sendLocationData(locationData, deviceIdentifier, cellInfoList.toString())
                            if (reply != null) {
                                counter++
                                Handler.post {
                                    SuccessCounter.text = "Успешно: $counter"
                                    Message.text = reply
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                Handler.post {
                    Toast.makeText(this@NetworkActivity, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                }
            } finally {
                locationDataSender.close()
                Handler.post {
                    Toast.makeText(this@NetworkActivity, "Отправка остановлена", Toast.LENGTH_SHORT).show()
                }
            }
        }
        sendingThread?.start()
    }

    private fun stopSending() {
        isSending = false
        updateButtonText()

        locationService.stopLocationUpdates()

        try {
            sendingThread?.join(500)
            timerThread?.join(500)
        } catch (e: Exception) {
        }

        sendingThread = null
        timerThread = null

        if (::locationDataSender.isInitialized) {
            locationDataSender.close()
        }
    }

    private fun updateButtonText() {
        startButton.text = if (isSending) "STOP" else "START"
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSending()
        Handler.removeCallbacksAndMessages(null)
    }

    override fun onPause() {
        super.onPause()
        if (isSending) {
            locationService.stopLocationUpdates()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isSending) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationService.startLocationUpdates()
            }
        }
    }
}