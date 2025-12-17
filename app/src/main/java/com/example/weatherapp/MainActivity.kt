package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {

    private val apiKey = "73816bdf8dda4fff027f8288fbd5bb57"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                WeatherScreen()
            }
        }
    }

    @Composable
    fun WeatherScreen() {
        var city by remember { mutableStateOf("") }
        var result by remember { mutableStateOf("날씨 정보를 여기에 표시합니다.") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAF4FF))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🌤️ Simple Weather App",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = city,
                onValueChange = { city = it },
                placeholder = { Text("도시 이름을 입력하세요 (예: Seoul)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    if (city.isBlank()) {
                        result = "도시 이름을 입력해주세요!"
                    } else {
                        loadWeather(city) {
                            result = it
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("날씨 검색하기", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = result,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(20.dp)
                    .heightIn(min = 150.dp)
            )
        }
    }

    private fun loadWeather(city: String, callback: (String) -> Unit) {
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
                    callback(text)
                }

            } catch (e: Exception) {
                runOnUiThread {
                    callback("⚠️ 날씨 정보를 가져올 수 없습니다.")
                }
            }
        }.start()
    }
}
