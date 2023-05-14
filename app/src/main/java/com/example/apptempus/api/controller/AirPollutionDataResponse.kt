package com.example.apptempus.api.controller


import com.example.apptempus.api.controller.airPollution.Coord
import com.example.apptempus.api.controller.airPollution.Lista
import com.google.gson.annotations.SerializedName

data class AirPollutionDataResponse(
    @SerializedName("coord")
    var coord: Coord?,
    @SerializedName("list")
    var list: List<Lista>?
)