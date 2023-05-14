package com.example.apptempus.api.controller.airPollution


import com.google.gson.annotations.SerializedName

data class Lista (
    @SerializedName("components")
    var components: Components?,
    @SerializedName("dt")
    var dt: Int?,
    @SerializedName("main")
    var main: Main?
)