package com.example.apptempus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apptempus.adapterForecastWeather.ForecastWeatherAdapter
import com.example.apptempus.api.controller.ForecastWeatherDataResponse
import com.example.apptempus.api.services.ApiServiceForecastWeather
import com.example.apptempus.databinding.ActivityDetailForecastBinding
import com.example.apptempus.tools.PrefSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailForecastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailForecastBinding
    private lateinit var retrofit: Retrofit
    private var latitud: Double? = null
    private var longitud: Double? = null
    private lateinit var forecastDetailAdapter: ForecastWeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailForecastBinding.inflate(layoutInflater)
        coordenadas()
        setContentView(binding.root)
        retrofit = getRetrofit()
        initIU()
    }

    private fun coordenadas() {
        val mapCoordenadas = PrefSetting.getCoordenadas(this)
        if (mapCoordenadas.isNotEmpty() && mapCoordenadas["latitud"] != null && mapCoordenadas["longitud"] != null) {
            latitud = mapCoordenadas["latitud"].toString().toDouble()
            longitud = mapCoordenadas["longitud"].toString().toDouble()
        } else {
            latitud = 38.0928
            longitud = -3.6344
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun initIU() {
        forecastWeather()

        binding.rvDetailForecastWeather.setHasFixedSize(true)
        binding.rvDetailForecastWeather.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        forecastDetailAdapter = ForecastWeatherAdapter()
        binding.rvDetailForecastWeather.adapter = forecastDetailAdapter
    }

    private fun forecastWeather() {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<ForecastWeatherDataResponse> = retrofit.create(
                ApiServiceForecastWeather::class.java
            ).getPrediccionCiudad(
                "data/2.5/forecast?lat=${latitud}&lon=${longitud}&appid=f014a68f430feb66d6a50b19da4956ee&units=metric&lang=sp"
            )

            val response: ForecastWeatherDataResponse? = myResponse.body()
            if (myResponse.isSuccessful) {
                Log.i("montse", "funciona forecast :)")

                if (response != null) {
                    Log.i("montse", response.toString())
                    runOnUiThread {
                        binding.tvNombreCiudadDetailForecast.text = response.city?.name
                        forecastDetailAdapter.updateListaForecastWeather(
                            response.list ?: emptyList()
                        )
                        forecastDetailAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                Log.i("montse", "no funciona forecast :(")
            }
        }
    }
}