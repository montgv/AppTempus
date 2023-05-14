package com.example.apptempus.api.controller.forecastWeather


import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    var all: Int?
)