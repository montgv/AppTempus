package com.example.apptempus.adapterForecastWeather

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.apptempus.api.controller.forecastWeather.Lista
import com.example.apptempus.databinding.ItemForecastWeatherBinding
import com.squareup.picasso.Picasso

class ForecastWeatherViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemForecastWeatherBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(listaForecastWeather: Lista) {
        binding.tvHoraForescastWeather.text = listaForecastWeather.dtTxt?.split(" ")?.get(1).toString()
        Picasso.get().load("https://openweathermap.org/img/w/${listaForecastWeather.weather?.get(0)?.icon}.png").into(binding.ivIconForescastWeather)
        binding.tvTemperaturaForescastWeather.text = String.format("%.2f º", listaForecastWeather.main?.temp)
        binding.tvPopForescastWeather.text = listaForecastWeather.pop?.times(100)?.toInt().toString() + " %"
    }
}