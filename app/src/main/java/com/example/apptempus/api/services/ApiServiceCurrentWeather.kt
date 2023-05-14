package com.example.apptempus.api.services

import com.example.apptempus.api.controller.CurrentWeatherDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiServiceCurrentWeather {
    @GET
    suspend fun getLocalizacionCiudad(@Url url: String): Response<CurrentWeatherDataResponse>
}