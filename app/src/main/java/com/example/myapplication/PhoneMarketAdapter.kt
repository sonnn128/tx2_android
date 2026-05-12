package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhoneMarketAdapter(
    private var weatherList: MutableList<PhoneMarketItem>,
    private val onItemClick: (PhoneMarketItem) -> Unit,
    private val onDeleteClick: (PhoneMarketItem) -> Unit
) : RecyclerView.Adapter<PhoneMarketAdapter.PhoneMarketViewHolder>() {

    inner class PhoneMarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtAreaCode: TextView = itemView.findViewById(R.id.txt_area_code)
        private val txtTemperature: TextView = itemView.findViewById(R.id.txt_temperature)
        private val txtHumidity: TextView = itemView.findViewById(R.id.txt_humidity)
        private val txtIntensity: TextView = itemView.findViewById(R.id.txt_intensity)
        private val imgWeatherIcon: ImageView = itemView.findViewById(R.id.img_weather_icon)

        fun bind(weather: PhoneMarketItem) {
            txtAreaCode.text = "Công ty: ${weather.weatherType}"
            txtTemperature.text = "Năm: ${weather.areaCode}"
            txtHumidity.text = "Sản xuất: ${weather.temperature}"
            txtIntensity.text = "Thị phần: ${weather.humidity}"

            when (weather.weatherType) {
                "Apple" -> imgWeatherIcon.setImageResource(R.drawable.w1)
                "Samsung" -> imgWeatherIcon.setImageResource(R.drawable.w3)
                "Xiaomi" -> imgWeatherIcon.setImageResource(R.drawable.w2)
                else -> imgWeatherIcon.setImageResource(R.drawable.w1)
            }

            itemView.setOnClickListener {
                onItemClick(weather)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneMarketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent, false)
        return PhoneMarketViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhoneMarketViewHolder, position: Int) {
        holder.bind(weatherList[position])
    }

    override fun getItemCount(): Int = weatherList.size

    fun updateList(newList: List<PhoneMarketItem>) {
        weatherList = newList.toMutableList()
        notifyDataSetChanged()
    }
}
