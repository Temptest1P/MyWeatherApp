package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.searchButton.setOnClickListener {
            searchWeather()
        }

        binding.cityEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                searchWeather()
                true
            } else {
                false
            }
        }
    }

    private fun searchWeather() {
        val cityName = binding.cityEditText.text.toString().trim()
        if (cityName.isNotEmpty()) {
            viewModel.getWeatherByCity(cityName)
        }
    }

    private fun observeViewModel() {
        viewModel.weatherData.observe(this) { weatherData ->
            binding.cityNameTextView.text = weatherData.name
            binding.temperatureTextView.text = "${weatherData.main.temp.toInt()}°C"
            binding.weatherDescriptionTextView.text = weatherData.weather[0].description.capitalize()
            binding.feelsLikeTextView.text = "Feels like: ${weatherData.main.feels_like.toInt()}°C"
            binding.humidityTextView.text = "Humidity: ${weatherData.main.humidity}%"
            binding.windTextView.text = "Wind: ${weatherData.wind.speed} m/s"

            // Load weather icon
            val iconCode = weatherData.weather[0].icon
            val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
            Glide.with(this)
                .load(iconUrl)
                .into(binding.weatherIconImageView)

            binding.weatherCard.visibility = View.VISIBLE
            binding.errorTextView.visibility = View.GONE
        }

        viewModel.error.observe(this) { errorMsg ->
            binding.errorTextView.text = errorMsg
            binding.errorTextView.visibility = View.VISIBLE
            binding.weatherCard.visibility = View.GONE
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}

// Extension function to capitalize first letter of string
fun String.capitalize(): String {
    return this.replaceFirstChar { it.uppercase() }
}