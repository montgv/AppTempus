package com.example.apptempus.api.services

import com.example.apptempus.api.controller.GeocodingApiDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiServiceGeocodingApi {
    @GET
    suspend fun getNombreCiudad(@Url url: String): Response<GeocodingApiDataResponse>
}