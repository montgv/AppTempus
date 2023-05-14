package com.example.apptempus.api.controller


import com.example.apptempus.api.controller.forecastWeather.City
import com.example.apptempus.api.controller.forecastWeather.Lista
import com.google.gson.annotations.SerializedName

data class ForecastWeatherDataResponse(
    @SerializedName("city")
    var city: City?,
    @SerializedName("cnt")
    var cnt: Int?,
    @SerializedName("cod")
    var cod: String?,
    @SerializedName("list")
    var list: List<Lista>?,
    @SerializedName("message")
    var message: Int?
)