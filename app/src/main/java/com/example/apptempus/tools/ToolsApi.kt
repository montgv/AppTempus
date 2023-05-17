package com.example.apptempus.tools

import com.example.apptempus.R

object ToolsApi {

    private fun mapIcon(): HashMap<String, Int> {
        val mapa = HashMap<String, Int>()

        mapa.put("01d", R.raw.weather_day_clear_sky)
        mapa.put("02d", R.raw.weather_day_few_clouds)
        mapa.put("03d", R.raw.weather_day_scattered_clouds)
        mapa.put("04d", R.raw.weather_day_broken_clouds)
        mapa.put("09d", R.raw.weather_day_shower_rains)
        mapa.put("10d", R.raw.weather_day_rain)
        mapa.put("11d", R.raw.weather_day_thunderstorm)
        mapa.put("13d", R.raw.weather_day_snow)
        mapa.put("50d", R.raw.weather_day_mist)

        mapa.put("01n", R.raw.weather_night_clear_sky)
        mapa.put("02n", R.raw.weather_night_few_clouds)
        mapa.put("03n", R.raw.weather_night_scattered_clouds)
        mapa.put("04n", R.raw.weather_night_broken_clouds)
        mapa.put("09n", R.raw.weather_night_shower_rains)
        mapa.put("10n", R.raw.weather_night_rain)
        mapa.put("11n", R.raw.weather_night_thunderstorm)
        mapa.put("13n", R.raw.weather_night_snow)
        mapa.put("50n", R.raw.weather_night_mist)

        return mapa
    }

    fun getAnimation(id: String) : Int? {
        return mapIcon()[id]
    }
}