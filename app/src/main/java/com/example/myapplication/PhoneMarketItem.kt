package com.example.myapplication

data class PhoneMarketItem(
    val id: Int,
    val areaCode: String,
    val temperature: String,
    val humidity: String,
    val weatherType: String  // "Apple", "Samsung", "Xiaomi"
)
