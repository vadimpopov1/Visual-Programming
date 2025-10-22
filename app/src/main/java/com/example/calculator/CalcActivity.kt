package com.example.calculator

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CalcActivity : AppCompatActivity() {

    private var currentTextOnScreen: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // запретить поворот экрана

        findViewById<TextView>(R.id.button_0).setOnClickListener({ AddToScreen("0") })
        findViewById<TextView>(R.id.button_1).setOnClickListener({ AddToScreen("1") })
        findViewById<TextView>(R.id.button_2).setOnClickListener({ AddToScreen("2") })
        findViewById<TextView>(R.id.button_3).setOnClickListener({ AddToScreen("3") })
        findViewById<TextView>(R.id.button_4).setOnClickListener({ AddToScreen("4") })
        findViewById<TextView>(R.id.button_5).setOnClickListener({ AddToScreen("5") })
        findViewById<TextView>(R.id.button_6).setOnClickListener({ AddToScreen("6") })
        findViewById<TextView>(R.id.button_7).setOnClickListener({ AddToScreen("7") })
        findViewById<TextView>(R.id.button_8).setOnClickListener({ AddToScreen("8") })
        findViewById<TextView>(R.id.button_9).setOnClickListener({ AddToScreen("9") })

        findViewById<TextView>(R.id.button_float).setOnClickListener({ AddToScreen(".") })

        findViewById<TextView>(R.id.button_add).setOnClickListener({ AddToScreen("+") })
        findViewById<TextView>(R.id.button_subtract).setOnClickListener({ AddToScreen("-") })
        findViewById<TextView>(R.id.button_multiply).setOnClickListener({ AddToScreen("×") })
        findViewById<TextView>(R.id.button_divide).setOnClickListener({ AddToScreen("÷") })

        //findViewById<TextView>(R.id.button_plusminus).setOnClickListener({ invertLastNumber() })
        //findViewById<TextView>(R.id.button_percent).setOnClickListener({ TakePercent() })
        findViewById<TextView>(R.id.button_clear).setOnClickListener({ ClearScreen() })
        findViewById<TextView>(R.id.button_calc).setOnClickListener({ Equals() })

        val buttonBack = findViewById<TextView>(R.id.button_back)

        buttonBack.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }
    }

    private fun UpdateScreen(text: String = currentTextOnScreen)
    {
        findViewById<TextView>(R.id.display).text = text
    }

    private fun AddToScreen(symbol: String) {
        if (symbol == ".") {
            if (currentTextOnScreen.isEmpty()) {
                currentTextOnScreen = "0."
                UpdateScreen()
                return
            }

            val parts = currentTextOnScreen.split('+', '-', '×', '÷')
            val lastNum = parts.last()

            if (lastNum.contains('.')) return
            if (lastNum.isEmpty()) {
                currentTextOnScreen += "0."
                UpdateScreen()
                return
            }
        }

        if (symbol in arrayOf("+", "-", "×", "÷")) {
            when {
                currentTextOnScreen.isEmpty() -> {
                    if (symbol != "-") return
                }
                currentTextOnScreen.last() in arrayOf('+', '-', '×', '÷') -> {
                    if (symbol == "-" && currentTextOnScreen.last() != '-') {
                    } else {
                        if (currentTextOnScreen.last() == '-' && symbol != "-") {
                            if (currentTextOnScreen.length > 1) {
                                val charBeforeMinus = currentTextOnScreen[currentTextOnScreen.length - 2]
                                if ((charBeforeMinus in arrayOf('+', '×', '÷')))  {
                                    currentTextOnScreen = currentTextOnScreen.dropLast(1)
                                    UpdateScreen()
                                    return
                                }
                            }
                        }
                        currentTextOnScreen = currentTextOnScreen.dropLast(1)
                    }
                }
            }
        }

        if (currentTextOnScreen == "0" && symbol in arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")) {
            currentTextOnScreen = ""
        }

        currentTextOnScreen += symbol
        UpdateScreen()
    }

    private fun Equals() {
        if (currentTextOnScreen.isEmpty()) return

        try {
            var calc = currentTextOnScreen

            if (calc.startsWith("-")) {
                calc = "0" + calc
            }

            var res = 0.0
            var curnum = ""
            var curOperator = '+'

            for (i in calc.indices) {
                val char = calc[i]

                if (char.isDigit() || char == '.') {
                    curnum += char
                } else if (char == '-' && curnum.isEmpty()) {
                    curnum += char
                } else if (char in "+-×÷") {
                    if (curnum.isNotEmpty()) {
                        if (curOperator == '+') {
                            res += curnum.toDouble()
                        } else if (curOperator == '-') {
                            res -= curnum.toDouble()
                        } else if (curOperator == '×') {
                            res *= curnum.toDouble()
                        } else if (curOperator == '÷') {
                            if (curnum.toDouble() == 0.0) throw ArithmeticException("Division by zero")
                            res /= curnum.toDouble()
                        }
                        curnum = ""
                    }
                    curOperator = char
                }
            }

            if (curnum.isNotEmpty()) {
                if (curOperator == '+') {
                    res += curnum.toDouble()
                } else if (curOperator == '-') {
                    res -= curnum.toDouble()
                } else if (curOperator == '×') {
                    res *= curnum.toDouble()
                } else if (curOperator == '÷') {
                    if (curnum.toDouble() == 0.0) throw ArithmeticException("Division by zero")
                    res /= curnum.toDouble()
                }
            }

            currentTextOnScreen = if (res % 1 == 0.0) {
                res.toInt().toString()
            } else {
                res.toString()
            }

            UpdateScreen()

        } catch (e: Exception) {
            currentTextOnScreen = "Error"
            UpdateScreen()
        }
    }

    private fun ClearScreen() {
        currentTextOnScreen = ""
        UpdateScreen()
    }
}