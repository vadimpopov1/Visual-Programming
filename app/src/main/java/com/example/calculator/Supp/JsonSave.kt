package com.example.calculator.data.local

import android.os.Environment
import com.example.calculator.data.LocationData
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

class JsonSave {
    companion object {
        fun saveToJson(locationData: LocationData) {
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(directory, "locations_data.json")
            if (!file.exists()) {
                file.createNewFile()
                file.writeText("[]")
            }
            val jsonArray = JSONArray(file.readText())
            val locationObject = JSONObject().apply {
                put("latitude", locationData.latitude)
                put("longitude", locationData.longitude)
                put("altitude", locationData.altitude)
                put("time", locationData.timestamp.time)
            }
            jsonArray.put(locationObject)
            //        Toast.makeText(this, "Файл обновлен. ${filesDir}", Toast.LENGTH_SHORT).show()
            FileWriter(file).use {
                it.write(jsonArray.toString(4))
            }
        }
    }
}