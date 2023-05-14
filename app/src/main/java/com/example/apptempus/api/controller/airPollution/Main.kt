package com.example.apptempus.api.controller.airPollution


import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("aqi")
    var aqi: Int?
)