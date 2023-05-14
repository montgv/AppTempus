package com.example.apptempus.api.controller.forecastWeather


import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("pod")
    var pod: String?
)