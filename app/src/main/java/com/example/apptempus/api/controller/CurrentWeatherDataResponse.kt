package com.example.apptempus.api.controller


import com.example.apptempus.api.controller.currentWeather.Clouds
import com.example.apptempus.api.controller.currentWeather.Coord
import com.example.apptempus.api.controller.currentWeather.Main
import com.example.apptempus.api.controller.currentWeather.Sys
import com.example.apptempus.api.controller.currentWeather.Weather
import com.example.apptempus.api.controller.currentWeather.Wind
import com.google.gson.annotations.SerializedName

data class CurrentWeatherDataResponse(
    @SerializedName("base")
    var base: String?,
    @SerializedName("clouds")
    var clouds: Clouds?,
    @SerializedName("cod")
    var cod: Int?,
    @SerializedName("coord")
    var coord: Coord?,
    @SerializedName("dt")
    var dt: Int?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("main")
    var main: Main?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("sys")
    var sys: Sys?,
    @SerializedName("timezone")
    var timezone: Int?,
    @SerializedName("visibility")
    var visibility: Int?,
    @SerializedName("weather")
    var weather: List<Weather?>?,
    @SerializedName("wind")
    var wind: Wind?
)