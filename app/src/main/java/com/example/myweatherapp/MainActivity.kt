package com.example.myweatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myweatherapp.databinding.ActivityMainBinding
import com.example.myweatherapp.viewmodel.WeatherViewModel

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
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
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
            binding.temperatureTextView.text = buildString {
                append(weatherData.main.temp.toInt())
                append("°C")
            }
            binding.weatherDescriptionTextView.text =
                weatherData.weather[0].description.capitalize()
            binding.feelsLikeTextView.text = buildString {
                append("Feels like: ")
                append(weatherData.main.feels_like.toInt())
                append("°C")
            }
            binding.humidityTextView.text = buildString {
                append("Humidity: ")
                append(weatherData.main.humidity)
                append("%")
            }
            binding.windTextView.text = buildString {
                append("Wind: ")
                append(weatherData.wind.speed)
                append(" m/s")
            }

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