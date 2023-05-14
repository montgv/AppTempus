package com.example.apptempus.api.controller.forecastWeather


import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h")
    var h: Double?
)