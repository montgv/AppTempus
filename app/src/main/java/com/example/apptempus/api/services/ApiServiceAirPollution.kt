package com.example.apptempus.api.services

import com.example.apptempus.api.controller.AirPollutionDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiServiceAirPollution {
    @GET
    suspend fun getContaminacionCiudad(@Url url: String): Response<AirPollutionDataResponse>
}