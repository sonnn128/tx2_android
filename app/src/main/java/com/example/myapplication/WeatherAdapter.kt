package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeatherAdapter(
    private var weatherList: MutableList<Weather>,
    private val onItemClick: (Weather) -> Unit,
    private val onDeleteClick: (Weather) -> Unit
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtAreaCode: TextView = itemView.findViewById(R.id.txt_area_code)
        private val txtTemperature: TextView = itemView.findViewById(R.id.txt_temperature)
        private val txtHumidity: TextView = itemView.findViewById(R.id.txt_humidity)
        private val txtIntensity: TextView = itemView.findViewById(R.id.txt_intensity)
        private val imgWeatherIcon: ImageView = itemView.findViewById(R.id.img_weather_icon)

        fun bind(weather: Weather) {
            txtAreaCode.text = "Mã khu vực: ${weather.areaCode}"
            txtTemperature.text = "Nhiệt độ cao nhất: ${weather.temperature}"
            txtHumidity.text = "Nhiệt độ thấp nhất: ${weather.humidity}"
            txtIntensity.text = "Kiểu thời tiết: ${weather.weatherType}"

            when (weather.weatherType) {
                "Nắng" -> imgWeatherIcon.setImageResource(R.drawable.w1)
                "Mây" -> imgWeatherIcon.setImageResource(R.drawable.w3)
                "Mưa" -> imgWeatherIcon.setImageResource(R.drawable.w2)
                else -> imgWeatherIcon.setImageResource(R.drawable.w1)
            }

            itemView.setOnClickListener {
                onItemClick(weather)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(weatherList[position])
    }

    override fun getItemCount(): Int = weatherList.size

    fun updateList(newList: List<Weather>) {
        weatherList = newList.toMutableList()
        notifyDataSetChanged()
    }
}
