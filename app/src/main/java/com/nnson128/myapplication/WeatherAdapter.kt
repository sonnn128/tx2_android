package com.nnson128.myapplication

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
        private val weatherIcon: ImageView = itemView.findViewById(R.id.weather_icon)
        private val weatherCode: TextView = itemView.findViewById(R.id.weather_code)
        private val weatherDescription: TextView = itemView.findViewById(R.id.weather_description)
        private val weatherType: TextView = itemView.findViewById(R.id.weather_type)
        private val deleteBtn: TextView = itemView.findViewById(R.id.delete_btn)

        fun bind(weather: Weather) {
            weatherCode.text = "Mã khí hậu: ${weather.id}"
            weatherDescription.text = "Mô tả: ${weather.description}"
            weatherType.text = "Loại: ${weather.weatherType}"

            // Set icon based on weather type
            val iconResId = when (weather.weatherType) {
                "Nắng nhẹ" -> R.drawable.w1
                "Nhiều mây" -> R.drawable.w2
                "Mưa" -> R.drawable.w3
                else -> R.drawable.w1
            }
            weatherIcon.setImageResource(iconResId)

            itemView.setOnClickListener {
                onItemClick(weather)
            }

            deleteBtn.setOnClickListener {
                onDeleteClick(weather)
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
