package com.raitha.vartha.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val name: String
)

data class Main(
    val temp: Float,
    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)
