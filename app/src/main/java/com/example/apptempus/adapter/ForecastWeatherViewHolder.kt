package com.example.apptempus.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.apptempus.api.controller.forecastWeather.Lista
import com.example.apptempus.databinding.ItemForecastWeatherBinding
import com.example.apptempus.tools.ToolsApi

class ForecastWeatherViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemForecastWeatherBinding.bind(view)

    @SuppressLint("SetTextI18n")
    fun bind(
        listaForecastWeather: Lista,
        onItemClick: ForecastWeatherAdapter.OnItemClick,
        position: Int
    ) {
        var fecha: String = listaForecastWeather.dtTxt?.split(" ")?.get(0).toString()
        fecha = fecha.split("-").get(2) + "/" + fecha.split("-").get(1)
        var hora: String = listaForecastWeather.dtTxt?.split(" ")?.get(1).toString()
        hora = hora.split(":").get(0) + ":" + hora.split(":").get(1)

        binding.tvFechaForescastWeather.text = fecha
        binding.tvHoraForescastWeather.text = hora
        val animation = listaForecastWeather.weather?.get(0)?.icon?.let { ToolsApi.getAnimation(it) }
        if (animation != null) {
            binding.lottieIconForescastWeather.setAnimation(animation)
            binding.lottieIconForescastWeather.playAnimation()
        }
        binding.tvTemperaturaForescastWeather.text = String.format("%s º", listaForecastWeather.main?.temp?.toInt())
        binding.tvPopForescastWeather.text = listaForecastWeather.pop?.times(100)?.toInt().toString() + " %"
        binding.containerItem.setOnClickListener {
            onItemClick.onPositionItem(position)
        }
    }
}