package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val apiKey = "73816bdf8dda4fff027f8288fbd5bb57"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cityInput = findViewById<EditText>(R.id.cityInput)
        val resultText = findViewById<TextView>(R.id.resultText)
        val searchBtn = findViewById<Button>(R.id.searchBtn)

        searchBtn.setOnClickListener {
            val city = cityInput.text.toString().trim()

            if (city.isEmpty()) {
                resultText.text = "도시 이름을 입력해주세요!"
            } else {
                loadWeather(city, resultText)
            }
        }
    }

    private fun loadWeather(city: String, resultText: TextView) {
        Thread {
            try {
                val url =
                    "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric&lang=kr"

                val response = URL(url).readText()
                val json = JSONObject(response)

                val temp = json.getJSONObject("main").getDouble("temp")
                val feelsLike = json.getJSONObject("main").getDouble("feels_like")
                val desc = json.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("description")

                val text = """
                    📍 도시: $city
                    
                    🌡 현재 온도: $temp°C
                    🤗 체감온도: $feelsLike°C
                    ☁ 날씨: $desc
                """.trimIndent()

                runOnUiThread {
                    resultText.text = text
                }

            } catch (e: Exception) {
                runOnUiThread {
                    resultText.text = "⚠️ 날씨 정보를 가져올 수 없습니다.\n도시 이름이 정확한지 확인해주세요."
                }
            }
        }.start()
    }
}
