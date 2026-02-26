package com.example.calculator.data.remote

import com.example.calculator.data.LocationData
import org.json.JSONObject
import org.zeromq.ZMQ

class LocationDataSender(private val serverAddress: String) {

    private val context = ZMQ.context(1)
    private val socket = context.socket(ZMQ.REQ)
    private var isSending = false

    init {
        socket.connect("tcp://$serverAddress")
        socket.setReceiveTimeOut(3000)
    }

    fun sendLocationData(locationData: LocationData, deviceIdentifier: String?, cellInfoList: String?): String? {
        return try {
            val jsonData = JSONObject().apply {
                put("latitude", locationData.latitude)
                put("longitude", locationData.longitude)
                put("altitude", locationData.altitude)
                put("accuracy", locationData.accuracy)
                put("timestamp", locationData.timestamp.time)
                put("imei", deviceIdentifier)
                put("cellInfoList", cellInfoList)
            }.toString()

            socket.send(jsonData)
            socket.recvStr()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun close() {
        socket.close()
        context.term()
    }
}