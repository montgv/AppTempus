package com.example.apptempus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.apptempus.api.controller.AirPollutionDataResponse
import com.example.apptempus.api.services.ApiServiceAirPollution
import com.example.apptempus.databinding.ActivityDetailAirPollutionBinding
import com.example.apptempus.tools.PrefSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailAirPollutionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAirPollutionBinding
    private lateinit var retrofit: Retrofit
    private var latitud: Double? = null
    private var longitud: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAirPollutionBinding.inflate(layoutInflater)
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
        airPollution()
    }

    private fun airPollution() {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<AirPollutionDataResponse> = retrofit.create(
                ApiServiceAirPollution::class.java
            )
                .getContaminacionCiudad("data/2.5/air_pollution?lat=${latitud}&lon=${longitud}&appid=f014a68f430feb66d6a50b19da4956ee")

            val response: AirPollutionDataResponse? = myResponse.body()
            if (myResponse.isSuccessful) {
                Log.i("montse", "funciona :)")

                if (response != null) {
                    Log.i("montse", response.toString())
                    runOnUiThread {
                        pintarDatosAirPollution(response)
                    }
                }
            } else {
                Log.i("montse", "no funciona :(")
            }
        }
    }

    private fun pintarDatosAirPollution(datosAirPollution: AirPollutionDataResponse) {

        binding.lottieIconDetailAirPollution.setAnimation(R.raw.atmosphere_scanning)
        binding.lottieIconDetailAirPollution.playAnimation()

        when (datosAirPollution.list?.get(0)?.main?.aqi) {
            1 -> {
                binding.tvCalidadAireAirPollution.text = "Bueno"
            }

            2 -> {
                binding.tvCalidadAireAirPollution.text = "Regular"
            }

            3 -> {
                binding.tvCalidadAireAirPollution.text = "Moderado"
            }

            4 -> {
                binding.tvCalidadAireAirPollution.text = "Malo"
            }

            5 -> {
                binding.tvCalidadAireAirPollution.text = "Muy Malo"
            }

            else -> {
                binding.tvCalidadAireAirPollution.text = "No hay datos"
            }
        }
        binding.tvConcCOAirPollution.text =
            String.format("%s μg/m", datosAirPollution.list?.get(0)?.components?.co.toString())
        binding.tvConcNOAirPollution.text =
            String.format("%s μg/m", datosAirPollution.list?.get(0)?.components?.no.toString())
        binding.tvConcNO2AirPollution.text =
            String.format("%s μg/m", datosAirPollution.list?.get(0)?.components?.no2.toString())
        binding.tvConcO3AirPollution.text =
            String.format("%s μg/m", datosAirPollution.list?.get(0)?.components?.o3.toString())
        binding.tvConcSO2AirPollution.text =
            String.format("%s μg/m", datosAirPollution.list?.get(0)?.components?.so2.toString())
        binding.tvConcPM25AirPollution.text =
            String.format("%s μg/m", datosAirPollution.list?.get(0)?.components?.pm25.toString())
        binding.tvConcPM10AirPollution.text =
            String.format("%s μg/m", datosAirPollution.list?.get(0)?.components?.pm10.toString())
        binding.tvConcNH3AirPollution.text =
            String.format("%s μg/m", datosAirPollution.list?.get(0)?.components?.nh3.toString())
    }
}