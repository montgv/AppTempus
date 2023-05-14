package com.example.apptempus.api.controller.geocodingApi


import com.google.gson.annotations.SerializedName

data class GeocodingApiDataResponseItem(
    @SerializedName("country")
    var country: String?,
    @SerializedName("lat")
    var lat: Double?,
    @SerializedName("local_names")
    var localNames: LocalNames?,
    @SerializedName("lon")
    var lon: Double?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("state")
    var state: String?
)