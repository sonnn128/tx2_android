package com.example.myapplication

data class Weather(
    val id: Int,
    val areaCode: String,
    val temperature: String,
    val humidity: String,
    val weatherType: String  // "Nắng nhẹ", "Nhiều mây", "Mưa"
)
