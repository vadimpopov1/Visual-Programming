package com.example.calculator.Ui.Hub

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculator.Ui.AudioPlayer.AudioPlayerActivity
import com.example.calculator.Ui.Calculator.CalcActivity
import com.example.calculator.Ui.Location.LocationActivity
import com.example.calculator.R
import com.example.calculator.Ui.Telephony.TelephonyActivity

class MainMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // запретить поворот экрана

        val buttonCalc = findViewById<TextView>(R.id.button_calc)
        val buttonPlayer = findViewById<TextView>(R.id.button_player)
        val buttonExit = findViewById<TextView>(R.id.button_exit)
        val buttonLocation = findViewById<TextView>(R.id.button_location)
        val buttonTelephony = findViewById<TextView>(R.id.button_telephony)

        buttonCalc.setOnClickListener {
            startActivity(Intent(this, CalcActivity::class.java))
        }

        buttonPlayer.setOnClickListener {
            startActivity(Intent(this, AudioPlayerActivity::class.java))
        }

        buttonLocation.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }

        buttonTelephony.setOnClickListener {
            startActivity(Intent(this, TelephonyActivity::class.java))
        }

        buttonExit.setOnClickListener {
            finishAffinity()
        }
    }
}