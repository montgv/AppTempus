package com.example.apptempus

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apptempus.adapter.ForecastWeatherAdapter
import com.example.apptempus.adapter.AdapterItemListSearch
import com.example.apptempus.api.controller.AirPollutionDataResponse
import com.example.apptempus.api.controller.CurrentWeatherDataResponse
import com.example.apptempus.api.controller.ForecastWeatherDataResponse
import com.example.apptempus.api.controller.GeocodingApiDataResponse
import com.example.apptempus.api.controller.geocodingApi.GeocodingApiDataResponseItem
import com.example.apptempus.api.services.ApiServiceAirPollution
import com.example.apptempus.api.services.ApiServiceCurrentWeather
import com.example.apptempus.api.services.ApiServiceForecastWeather
import com.example.apptempus.api.services.ApiServiceGeocodingApi
import com.example.apptempus.databinding.ActivityMainBinding
import com.example.apptempus.tools.PrefSetting
import com.example.apptempus.tools.ToolsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), ForecastWeatherAdapter.OnItemClick,
    AdapterItemListSearch.OnItemClickCountry {

    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private var latitud: Double? = null
    private var longitud: Double? = null
    private lateinit var forecastWeatherAdapter: ForecastWeatherAdapter
    private lateinit var listCitys: ArrayList<GeocodingApiDataResponseItem>
    private lateinit var adapterCitys: AdapterItemListSearch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        coordenadas()
        listCitys = ArrayList()
        setContentView(binding.root)
        retrofit = getRetrofit()
        initIU()
    }

    override fun onRestart() {
        super.onRestart()
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

        binding.rvListSearch.layoutManager = LinearLayoutManager(this)
        adapterCitys = AdapterItemListSearch(listCitys, this)
        binding.rvListSearch.adapter = adapterCitys


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.rvListSearch.visibility = View.VISIBLE
                searchByNombreCiudad(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    binding.rvListSearch.visibility = View.VISIBLE
                    searchByNombreCiudad(newText)
                } else {
                    listCitys.clear()
                    adapterCitys.notifyDataSetChanged()
                }
                return false
            }
        })

        binding.rvForecastWeather.setHasFixedSize(true)
        binding.rvForecastWeather.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        forecastWeatherAdapter = ForecastWeatherAdapter(emptyList(), this)
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
            try {
                val myResponse: Response<GeocodingApiDataResponse> = retrofit.create(
                    ApiServiceGeocodingApi::class.java
                )
                    .getNombreCiudad("geo/1.0/direct?q=$query&limit=5&appid=f014a68f430feb66d6a50b19da4956ee")
                val response: GeocodingApiDataResponse? = myResponse.body()
                if (myResponse.isSuccessful) {
                    Log.i("montse", "funciona busqueda nombre")

                    if (!response.isNullOrEmpty()) {
                        Log.i("montse", response.toString())
                        runOnUiThread {
                            listCitys.clear()
                            listCitys.addAll(myResponse.body() ?: GeocodingApiDataResponse())
                            adapterCitys.notifyDataSetChanged()
                        }
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "No se ha podido conectar con openweathermap.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun guardarCoordenadas() {
        PrefSetting.guardarCoordenadas(latitud, longitud, this)
    }

    override fun itemClick(item: GeocodingApiDataResponseItem) {
        latitud = item.lat
        longitud = item.lon
        guardarCoordenadas()
        currentWeather()
        binding.rvListSearch.visibility = View.GONE
        hideKeyBoard()
    }

    private fun hideKeyBoard() {
        val inm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
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

    override fun onPositionItem(itemPosicion: Int) {
        val intent = Intent(this, DetailForecastActivity::class.java)
        intent.putExtra("posItem", itemPosicion)
        startActivity(intent)
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
            String.format("%s º", datos.main?.temp?.toInt())
        binding.tvDescrpcionCurrentWeather.text = datos.weather?.get(0)?.description.toString()
        binding.tvSensacionTermicaCurrentWeather.text =
            String.format("%s º", datos.main?.feelsLike?.toInt())
        binding.tvVelocidadVientoCurrentWeather.text =
            String.format("%.2f m/s", datos.wind?.speed?.toFloat())
    }

    private fun updateBackground(datosCurrent: CurrentWeatherDataResponse) {
        when (datosCurrent.weather?.get(0)?.icon) {
            "01d" -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_cielo_azul)
                binding.ivBackgroundMain.imageAlpha = 200
            }

            "01n" -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_noche_luna)
                binding.ivBackgroundMain.imageAlpha = 200
            }

            "02d", "03d" -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_nubes)
                binding.ivBackgroundMain.imageAlpha = 200
            }

            "02n", "03n" -> {
                //cielo noche con algo de nubes
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_nubes_noche)
                binding.ivBackgroundMain.imageAlpha = 200
            }

            "04d" -> {
                //cielo cubierto de nubes
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_cubierto_nubes)
                binding.ivBackgroundMain.imageAlpha = 200
            }

            "04n" -> {
                //cielo cubierto de nubes noche
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_cubierto_nubes_noche)
                binding.ivBackgroundMain.imageAlpha = 255
            }

            "09d", "10d" -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_lluvia_dia)
                binding.ivBackgroundMain.imageAlpha = 175
            }

            "09n", "10n" -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_lluvia_noche)
                binding.ivBackgroundMain.imageAlpha = 150
            }

            "11d" -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_tormenta_dia)
                binding.ivBackgroundMain.imageAlpha = 200
            }

            "11n" -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_tormenta_noche)
                binding.ivBackgroundMain.imageAlpha = 200
            }

            "13d", "13n" -> {
                //nieve
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_nieve)
                binding.ivBackgroundMain.imageAlpha = 200
            }

            "50d", "50n" -> {
                //niebla
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_niebla)
                binding.ivBackgroundMain.imageAlpha = 255
            }

            else -> {
                binding.ivBackgroundMain.setImageResource(R.drawable.bg_app_nubes)
                binding.ivBackgroundMain.imageAlpha = 200
            }
        }
    }

}