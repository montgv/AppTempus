package com.example.apptempus.tools

import android.content.Context

object PrefSetting {
    fun guardarCoordenadas(latitud:Double?, longitud:Double?, context: Context) {
        context.getSharedPreferences("coordenadas", Context.MODE_PRIVATE)
            .edit()
            .putString("latitud", latitud.toString())
            .putString("longitud", longitud.toString())
            .apply()
    }

    fun getCoordenadas(context: Context): HashMap<String, String?> {
        val mapa = HashMap<String, String?>()

        mapa.put("latitud", context.getSharedPreferences("coordenadas", Context.MODE_PRIVATE)
            .getString("latitud", null))

        mapa.put("longitud", context.getSharedPreferences("coordenadas", Context.MODE_PRIVATE)
            .getString("longitud", null))

        return mapa
    }
}