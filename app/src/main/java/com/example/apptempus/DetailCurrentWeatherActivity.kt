package com.example.apptempus

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.apptempus.api.controller.CurrentWeatherDataResponse
import com.example.apptempus.api.services.ApiServiceCurrentWeather
import com.example.apptempus.databinding.ActivityDetailCurrentWeatherBinding
import com.example.apptempus.tools.PrefSetting
import com.example.apptempus.tools.ToolsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailCurrentWeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCurrentWeatherBinding
    private lateinit var retrofit: Retrofit
    private var latitud: Double? = null
    private var longitud: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCurrentWeatherBinding.inflate(layoutInflater)
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
        currentWeather()
    }

    private fun currentWeather() {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<CurrentWeatherDataResponse> = retrofit.create(
                ApiServiceCurrentWeather::class.java
            ).getLocalizacionCiudad(
                "data/2.5/weather?lat=${latitud}&lon=${longitud}&appid=f014a68f430feb66d6a50b19da4956ee&units=metric&lang=sp"
            )

            val response: CurrentWeatherDataResponse? = myResponse.body()
            if (myResponse.isSuccessful) {
                Log.i("montse", "funciona current :)")

                if (response != null) {
                    Log.i("montse", response.toString())
                    runOnUiThread {
                        //updateBackground(response)
                        pintarDatosCurrentWeather(response)
                    }
                }
            } else {
                Log.i("montse", "no funciona current :(")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun pintarDatosCurrentWeather(datos: CurrentWeatherDataResponse) {
        val animation = datos.weather?.get(0)?.icon?.let { ToolsApi.getAnimation(it) }
        if (animation != null) {
            binding.lottieIconDetailCurrent.setAnimation(animation)
            binding.lottieIconDetailCurrent.playAnimation()
        }
        binding.tvDescrpcionDetailCurrent.text = datos.weather?.get(0)?.description.toString()
        binding.tvTemperaturaDetailCurrent.text =
            String.format("%.2f º", datos.main?.temp?.toFloat())
        binding.tvSensacionTermicaDetailCurrent.text =
            String.format("%.2f º", datos.main?.feelsLike?.toFloat())
        binding.tvVelocidadVientoDetailCurrent.text =
            String.format("%.2f m/s", datos.wind?.speed?.toFloat())
        binding.tvDireccionVientoDetailCurrent.text = datos.wind?.deg.toString()
        binding.tvVisibilidadDetailCurrent.text = String.format("%s metros", datos.visibility?.toString())
        binding.tvHumedadDetailCurrent.text =
            String.format("%s", datos.main?.humidity.toString()) + " %"
        binding.tvNubosidadDetailCurrent.text =
            String.format("%s", datos.clouds?.all.toString()) + " %"
    }
}