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
                        updateBackground(response)
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
        binding.tvNombreCiudadDetailCurrent.text = datos.name.toString()
        val animation = datos.weather?.get(0)?.icon?.let { ToolsApi.getAnimation(it) }
        if (animation != null) {
            binding.lottieIconDetailCurrent.setAnimation(animation)
            binding.lottieIconDetailCurrent.playAnimation()
        }
        binding.tvDescrpcionDetailCurrent.text = datos.weather?.get(0)?.description.toString()
        binding.tvTemperaturaDetailCurrent.text =
            String.format("%s º", datos.main?.temp?.toInt())
        binding.tvSensacionTermicaDetailCurrent.text =
            String.format("%s º", datos.main?.feelsLike?.toInt())
        binding.tvVelocidadVientoDetailCurrent.text =
            String.format("%.2f m/s", datos.wind?.speed?.toFloat())
        binding.tvVisibilidadDetailCurrent.text =
            String.format("%s metros", datos.visibility?.toString())
        binding.tvHumedadDetailCurrent.text =
            String.format("%s", datos.main?.humidity.toString()) + " %"
        binding.tvNubosidadDetailCurrent.text =
            String.format("%s", datos.clouds?.all.toString()) + " %"
    }

    private fun updateBackground(datosCurrent: CurrentWeatherDataResponse) {
        when (datosCurrent.weather?.get(0)?.icon) {
            "01d" -> {
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_cielo_azul)
                binding.ivBackgroundDetailCurrent.imageAlpha = 200
            }

            "01n" -> {
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_noche_luna)
                binding.ivBackgroundDetailCurrent.imageAlpha = 200
            }

            "02d", "03d" -> {
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_nubes)
                binding.ivBackgroundDetailCurrent.imageAlpha = 200
            }

            "02n", "03n" -> {
                //cielo noche con algo de nubes
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_nubes_noche)
                binding.ivBackgroundDetailCurrent.imageAlpha = 200
            }

            "04d" -> {
                //cielo cubierto de nubes
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_cubierto_nubes)
                binding.ivBackgroundDetailCurrent.imageAlpha = 200
            }

            "04n" -> {
                //cielo cubierto de nubes noche
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_cubierto_nubes_noche)
                binding.ivBackgroundDetailCurrent.imageAlpha = 255
            }

            "09d", "10d" -> {
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_lluvia_dia)
                binding.ivBackgroundDetailCurrent.imageAlpha = 175
            }

            "09n", "10n" -> {
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_lluvia_noche)
                binding.ivBackgroundDetailCurrent.imageAlpha = 150
            }

            "11d" -> {
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_tormenta_dia)
                binding.ivBackgroundDetailCurrent.imageAlpha = 200
            }

            "11n" -> {
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_tormenta_noche)
                binding.ivBackgroundDetailCurrent.imageAlpha = 200
            }

            "13d", "13n" -> {
                //nieve
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_nieve)
                binding.ivBackgroundDetailCurrent.imageAlpha = 200
            }

            "50d", "50n" -> {
                //niebla
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_niebla)
                binding.ivBackgroundDetailCurrent.imageAlpha = 255
            }

            else -> {
                binding.ivBackgroundDetailCurrent.setImageResource(R.drawable.bg_app_nubes)
                binding.ivBackgroundDetailCurrent.imageAlpha = 200
            }
        }
    }
}