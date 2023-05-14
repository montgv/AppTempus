package com.example.apptempus.api.services

import com.example.apptempus.api.controller.ForecastWeatherDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiServiceForecastWeather {
    @GET
    suspend fun getPrediccionCiudad(@Url url: String): Response<ForecastWeatherDataResponse>
}