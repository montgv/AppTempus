package com.example.apptempus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apptempus.R
import com.example.apptempus.api.controller.geocodingApi.GeocodingApiDataResponseItem

class AdapterItemListSearch(private val listCiudades: ArrayList<GeocodingApiDataResponseItem>, private val listener: OnItemClickCountry): RecyclerView.Adapter<AdapterItemListSearch.ViewHolder>() {
    interface OnItemClickCountry {
        fun itemClick(item: GeocodingApiDataResponseItem)
    }
    inner class ViewHolder(view:View): RecyclerView.ViewHolder(view) {
        private val textCountry: TextView
        init {
            textCountry = view.findViewById(R.id.listCity)
        }
        fun bind(item: GeocodingApiDataResponseItem) {
            textCountry.text = String.format(itemView.context.getString(R.string.itemCity), item.name, item.state, item.country)
            textCountry.setOnClickListener {
                listener.itemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_search, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listCiudades.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listCiudades[position])
    }
}