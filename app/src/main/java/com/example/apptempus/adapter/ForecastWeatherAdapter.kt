package com.example.apptempus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apptempus.R
import com.example.apptempus.api.controller.forecastWeather.Lista

class ForecastWeatherAdapter(
    private var forecastWeatherLista: List<Lista> = emptyList(),
    private val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<ForecastWeatherViewHolder>() {

    interface OnItemClick{
        fun onPositionItem(itemPosicion: Int)
    }

    fun updateListaForecastWeather(forecastWeatherLista: List<Lista>) {
        this.forecastWeatherLista = forecastWeatherLista
    }

    fun getListaForecastWeather() : List<Lista> {
        return forecastWeatherLista
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastWeatherViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ForecastWeatherViewHolder(layoutInflater.inflate(R.layout.item_forecast_weather, parent, false))
    }

    override fun getItemCount() = forecastWeatherLista.size

    override fun onBindViewHolder(viewHolder: ForecastWeatherViewHolder, position: Int) {
        val item = forecastWeatherLista[position]
        viewHolder.bind(item, onItemClick, position)
    }
}