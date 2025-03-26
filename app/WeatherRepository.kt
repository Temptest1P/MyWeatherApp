package com.example.weatherapp.repository

import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.model.WeatherResponse
import retrofit2.Response

class WeatherRepository {
    private val apiKey = "e104ec36f70baa7af34c38ac7783702f" // Replace with your actual API key

    suspend fun getWeatherByCity(cityName: String): Response<WeatherResponse> {
        return RetrofitInstance.api.getWeatherByCity(cityName, apiKey)
    }
}