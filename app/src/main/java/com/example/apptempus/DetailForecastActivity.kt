package com.example.apptempus

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apptempus.adapter.ForecastWeatherAdapter
import com.example.apptempus.api.controller.CurrentWeatherDataResponse
import com.example.apptempus.api.controller.ForecastWeatherDataResponse
import com.example.apptempus.api.services.ApiServiceForecastWeather
import com.example.apptempus.databinding.ActivityDetailForecastBinding
import com.example.apptempus.tools.PrefSetting
import com.example.apptempus.tools.ToolsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailForecastActivity : AppCompatActivity(), ForecastWeatherAdapter.OnItemClick {

    private lateinit var binding: ActivityDetailForecastBinding
    private lateinit var retrofit: Retrofit
    private var latitud: Double? = null
    private var longitud: Double? = null
    private lateinit var forecastDetailAdapter: ForecastWeatherAdapter
    private var positionItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailForecastBinding.inflate(layoutInflater)
        coordenadas()
        setContentView(binding.root)
        retrofit = getRetrofit()
        positionItem = intent.extras?.getInt("posItem", 0) ?: 0
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
        forecastDetailAdapter = ForecastWeatherAdapter(emptyList(), this)
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
                        pintarItem()
                    }
                }
            } else {
                Log.i("montse", "no funciona forecast :(")
            }
        }
    }

    override fun onPositionItem(itemPosicion: Int) {
        positionItem = itemPosicion
        binding.rvDetailForecastWeather.smoothScrollToPosition(itemPosicion)
        pintarItem()
    }

    @SuppressLint("SetTextI18n")
    private fun pintarItem() {
        binding.rvDetailForecastWeather.smoothScrollToPosition(positionItem)

        val item = forecastDetailAdapter.getListaForecastWeather()[positionItem]
        binding.tvDateDetailForecast.text = item.dtTxt
        val animation = item.weather?.get(0)?.icon?.let {
            ToolsApi.getAnimation(it)
        }
        updateBackground(item.weather?.get(0)?.icon ?: "")
        if (animation != null) {
            binding.lottieIconDetailForecast.setAnimation(animation)
            binding.lottieIconDetailForecast.playAnimation()
        }
        binding.tvDescrpcionDetailForecast.text = item.weather?.get(0)?.description.toString()
        binding.tvTemperaturaDetailForecast.text =
            String.format("%s º", item.main?.temp?.toInt())
        binding.tvSensacionTermicaDetailForecast.text =
            String.format("%s º", item.main?.feelsLike?.toInt())
        binding.tvVelocidadVientoDetailForecast.text =
            String.format("%.2f m/s", item.wind?.speed?.toFloat())
        binding.tvPrecipitacionesDetailForecast.text =
            item.pop?.times(100)?.toInt().toString() + " %"
        binding.tvVisibilidadDetailForecast.text =
            String.format("%s metros", item.visibility?.toString())
        binding.tvHumedadDetailForecast.text =
            String.format("%s", item.main?.humidity.toString()) + " %"
        binding.tvNubosidadDetailForecast.text =
            String.format("%s", item.clouds?.all.toString()) + " %"
    }

    private fun updateBackground(idIcon: String) {
        when (idIcon) {
            "01d" -> {
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_cielo_azul)
                binding.ivBackgroundDetailForecast.imageAlpha = 200
            }

            "01n" -> {
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_noche_luna)
                binding.ivBackgroundDetailForecast.imageAlpha = 200
            }

            "02d", "03d" -> {
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_nubes)
                binding.ivBackgroundDetailForecast.imageAlpha = 200
            }

            "02n", "03n" -> {
                //cielo noche con algo de nubes
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_nubes_noche)
                binding.ivBackgroundDetailForecast.imageAlpha = 200
            }

            "04d" -> {
                //cielo cubierto de nubes
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_cubierto_nubes)
                binding.ivBackgroundDetailForecast.imageAlpha = 200
            }

            "04n" -> {
                //cielo cubierto de nubes noche
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_cubierto_nubes_noche)
                binding.ivBackgroundDetailForecast.imageAlpha = 255
            }

            "09d", "10d" -> {
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_lluvia_dia)
                binding.ivBackgroundDetailForecast.imageAlpha = 175
            }

            "09n", "10n" -> {
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_lluvia_noche)
                binding.ivBackgroundDetailForecast.imageAlpha = 150
            }

            "11d" -> {
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_tormenta_dia)
                binding.ivBackgroundDetailForecast.imageAlpha = 200
            }

            "11n" -> {
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_tormenta_noche)
                binding.ivBackgroundDetailForecast.imageAlpha = 200
            }

            "13d", "13n" -> {
                //nieve
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_nieve)
                binding.ivBackgroundDetailForecast.imageAlpha = 200
            }

            "50d", "50n" -> {
                //niebla
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_niebla)
                binding.ivBackgroundDetailForecast.imageAlpha = 255
            }

            else -> {
                binding.ivBackgroundDetailForecast.setImageResource(R.drawable.bg_app_nubes)
                binding.ivBackgroundDetailForecast.imageAlpha = 200
            }
        }
    }
}