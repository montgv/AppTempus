package com.example.apptempus

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apptempus.adapterForecastWeather.ForecastWeatherAdapter
import com.example.apptempus.api.controller.AirPollutionDataResponse
import com.example.apptempus.api.controller.CurrentWeatherDataResponse
import com.example.apptempus.api.controller.ForecastWeatherDataResponse
import com.example.apptempus.api.controller.GeocodingApiDataResponse
import com.example.apptempus.api.services.ApiServiceAirPollution
import com.example.apptempus.api.services.ApiServiceCurrentWeather
import com.example.apptempus.api.services.ApiServiceForecastWeather
import com.example.apptempus.api.services.ApiServiceGeocodingApi
import com.example.apptempus.databinding.ActivityMainBinding
import com.example.apptempus.tools.PrefSetting
import com.example.apptempus.tools.ToolsApi
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private var latitud: Double? = null
    private var longitud: Double? = null
    private lateinit var forecastWeatherAdapter: ForecastWeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
        forecast()
        airPollution()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchByNombreCiudad(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?) = false
        })

        //forecastWeatherAdapter = ForecastWeatherAdapter { navegarAlDetalleForecastWeather(it) }

        binding.rvForecastWeather.setHasFixedSize(true)
        binding.rvForecastWeather.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        forecastWeatherAdapter = ForecastWeatherAdapter()
        binding.rvForecastWeather.adapter = forecastWeatherAdapter

        binding.btnDetalleCurrentWeather.setOnClickListener {
            navigateToDetailCurrenteWeather()
        }

        binding.btnDetalleAirPollution.setOnClickListener {
            natigateToDetailAirPollution()
        }

        binding.btnDetalleForecastWeather.setOnClickListener {
            navigateToDetailForecast()
        }
    }

    private fun navigateToDetailForecast() {
        val intent = Intent(this, DetailForecastActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDetailCurrenteWeather() {
        val intent = Intent(this, DetailCurrentWeatherActivity::class.java)
        startActivity(intent)
    }

    private fun natigateToDetailAirPollution() {
        val intent = Intent(this, DetailAirPollutionActivity::class.java)
        startActivity(intent)
    }


    private fun searchByNombreCiudad(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<GeocodingApiDataResponse> = retrofit.create(
                ApiServiceGeocodingApi::class.java
            )
                .getNombreCiudad("geo/1.0/direct?q=$query&limit=1&appid=f014a68f430feb66d6a50b19da4956ee")
            val response: GeocodingApiDataResponse? = myResponse.body()
            if (myResponse.isSuccessful) {
                Log.i("montse", "funciona busqueda nombre")

                //Tengo que controlar que el array no este vacio, no me vale asi
                withContext(Dispatchers.Main) {
                    if (response != null) {
                        Log.i("montse", response.toString())
                        runOnUiThread {
                            latitud = response[0].lat
                            longitud = response[0].lon
                            guardarCoordenadas()
                            currentWeather()
                        }
                    } else {
                        Log.i("montse", "funciona la comprobacion")
                        Toast.makeText(
                            this@MainActivity,
                            "Esta ciudad no existe",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun guardarCoordenadas() {
        PrefSetting.guardarCoordenadas(latitud, longitud, this)
    }

    private fun airPollution() {
        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<AirPollutionDataResponse> = retrofit.create(
                ApiServiceAirPollution::class.java
            )
                .getContaminacionCiudad("data/2.5/air_pollution?lat=${latitud}&lon=${longitud}&appid=f014a68f430feb66d6a50b19da4956ee")

            val response: AirPollutionDataResponse? = myResponse.body()
            if (myResponse.isSuccessful) {
                Log.i("montse", "funciona air pollution :)")

                if (response != null) {
                    Log.i("montse", response.toString())
                    runOnUiThread {
                        pintarDatosAirPollution(response)
                    }
                }
            } else {
                Log.i("montse", "no funciona air pollution :(")
            }
        }
    }

    private fun pintarDatosAirPollution(datosAir: AirPollutionDataResponse) {

        binding.lottieIconAirPollution.setAnimation(R.raw.atmosphere_scanning)
        binding.lottieIconAirPollution.playAnimation()

        when (datosAir.list?.get(0)?.main?.aqi) {
            1 -> {
                binding.tvAquiAirPollution.text = "Bueno"
            }

            2 -> {
                binding.tvAquiAirPollution.text = "Regular"
            }

            3 -> {
                binding.tvAquiAirPollution.text = "Moderado"
            }

            4 -> {
                binding.tvAquiAirPollution.text = "Malo"
            }

            5 -> {
                binding.tvAquiAirPollution.text = "Muy Malo"
            }

            else -> {
                binding.tvAquiAirPollution.text = "No hay datos"
            }
        }
    }

    private fun forecast() {
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
                        forecastWeatherAdapter.updateListaForecastWeather(
                            response.list ?: emptyList()
                        )
                        forecastWeatherAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                Log.i("montse", "no funciona forecast :(")
            }
        }
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
        binding.tvNombreCiudad.text = datos.name.toString()
        val animation = datos.weather?.get(0)?.icon?.let { ToolsApi.getAnimation(it) }
        if (animation != null) {
            binding.lottieIconCurrentWeather.setAnimation(animation)
            binding.lottieIconCurrentWeather.playAnimation()
        }
        binding.tvTemperaturaCurrentWeather.text =
            String.format("%.2f º", datos.main?.temp?.toFloat())
        binding.tvDescrpcionCurrentWeather.text = datos.weather?.get(0)?.description.toString()
        binding.tvSensacionTermicaCurrentWeather.text =
            String.format("%.2f º", datos.main?.feelsLike?.toFloat())
        binding.tvVelocidadVientoCurrentWeather.text =
            String.format("%.2f m/s", datos.wind?.speed?.toFloat())
        binding.tvDireccionVientoCurrentWeather.text = datos.wind?.deg.toString()

    }

    private fun updateBackground(datosCurrent: CurrentWeatherDataResponse) {
        when (datosCurrent.weather?.get(0)?.icon) {
            "01n", "02n", "03n", "04n", "09n", "10n", "11n", "13n", "50n" -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_noche)
            }
            "01d" -> {
                //cielo con sol
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_noche)
            }

            "02d", "03d", "04d" -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_nubes)
            }

            "09d", "10d", "11d" -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_lluvia)
            }

            "50d" -> {
                //niebla
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_noche)
            }

            else -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_nubes)
            }
        }
    }
}