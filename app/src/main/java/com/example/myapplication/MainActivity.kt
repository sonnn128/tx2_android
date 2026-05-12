package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var edtAreaCode: EditText
    private lateinit var edtTemperature: EditText
    private lateinit var edtHumidity: EditText
    private lateinit var spinnerWeatherType: Spinner
    private lateinit var btnAdd: android.widget.TextView
    private lateinit var btnEdit: android.widget.TextView
    private lateinit var btnSave: android.widget.TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PhoneMarketAdapter
    private var weatherList = mutableListOf<PhoneMarketItem>()
    private var selectedWeather: PhoneMarketItem? = null
    private var nextId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        edtAreaCode = findViewById(R.id.edt_area_code)
        edtTemperature = findViewById(R.id.edt_temperature)
        edtHumidity = findViewById(R.id.edt_humidity)
        spinnerWeatherType = findViewById(R.id.spinner_weather_type)
        btnAdd = findViewById(R.id.btn_add)
        btnEdit = findViewById(R.id.btn_edit)
        btnSave = findViewById(R.id.btn_save)
        recyclerView = findViewById(R.id.weather_recycler_view)

        // Setup Spinner
        setupSpinner()
2
        // Setup RecyclerView
        setupRecyclerView()

        // Load data from cache
        loadWeatherData()

        // Setup button listeners
        btnAdd.setOnClickListener { addWeather() }
        btnEdit.setOnClickListener { editWeather() }
        btnSave.setOnClickListener { saveWeatherData() }
    }

    private fun setupSpinner() {
        val weatherTypes = arrayOf("-- Chọn công ty --", "Apple", "Samsung", "Xiaomi")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, weatherTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWeatherType.adapter = adapter
    }

    private fun setupRecyclerView() {
        adapter = PhoneMarketAdapter(weatherList, { weather ->
            selectedWeather = weather
            edtAreaCode.setText(weather.areaCode)
            edtHumidity.setText(weather.humidity)
            edtTemperature.setText(weather.temperature)
            spinnerWeatherType.setSelection(getWeatherTypePosition(weather.weatherType))
        }, { weather ->
            deleteWeatherItem(weather)
        })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun getWeatherTypePosition(weatherType: String): Int {
        return when (weatherType) {
            "Apple" -> 1
            "Samsung" -> 2
            "Xiaomi" -> 3
            else -> 0
        }
    }

    private fun addWeather() {
        val areaCode = edtAreaCode.text.toString().trim()
        val temperature = edtTemperature.text.toString().trim()
        val humidity = edtHumidity.text.toString().trim()
        val weatherType = spinnerWeatherType.selectedItem.toString()

        if (weatherType == "-- Chọn công ty --") {
            Toast.makeText(this, "Vui lòng Chọn công ty", Toast.LENGTH_SHORT).show()
            return
        }

        if (areaCode.isEmpty() || temperature.isEmpty() || humidity.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val weather = PhoneMarketItem(nextId++, areaCode, temperature, humidity, weatherType)
        weatherList.add(weather)
        adapter.updateList(weatherList)
        clearFields()
        selectedWeather = null
        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
    }

    private fun editWeather() {
        if (selectedWeather == null) {
            Toast.makeText(this, "Vui lòng chọn một mục để sửa", Toast.LENGTH_SHORT).show()
            return
        }

        val areaCode = edtAreaCode.text.toString().trim()
        val temperature = edtTemperature.text.toString().trim()
        val humidity = edtHumidity.text.toString().trim()
        val weatherType = spinnerWeatherType.selectedItem.toString()

        if (weatherType == "-- Chọn công ty --") {
            Toast.makeText(this, "Vui lòng Chọn công ty", Toast.LENGTH_SHORT).show()
            return
        }

        if (areaCode.isEmpty() || temperature.isEmpty() || humidity.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val index = weatherList.indexOfFirst { it.id == selectedWeather?.id }
        if (index != -1) {
            weatherList[index] = PhoneMarketItem(selectedWeather!!.id, areaCode, temperature, humidity, weatherType)
            adapter.updateList(weatherList)
            clearFields()
            selectedWeather = null
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteWeatherItem(weather: PhoneMarketItem) {
        weatherList.removeAll { it.id == weather.id }
        adapter.updateList(weatherList)
        clearFields()
        if (selectedWeather?.id == weather.id) {
            selectedWeather = null
        }
        Toast.makeText(this, "Xoá thành công", Toast.LENGTH_SHORT).show()
    }

    private fun clearFields() {
        edtAreaCode.text.clear()
        edtTemperature.text.clear()
        edtHumidity.text.clear()
        spinnerWeatherType.setSelection(0)
    }

    private fun saveWeatherData() {
        val gson = Gson()
        val json = gson.toJson(weatherList)
        val file = File(filesDir, "Phone")
        file.writeText(json)
        Toast.makeText(this, "Dữ liệu đã được lưu", Toast.LENGTH_SHORT).show()
    }

    private fun loadWeatherData() {
        val file = File(filesDir, "Phone")
        if (!file.exists()) {
            return
        }
        val json = file.readText()
        val gson = Gson()
        val type = object : TypeToken<List<PhoneMarketItem>>() {}.type
        val loadedList: List<PhoneMarketItem> = gson.fromJson(json, type)
        weatherList = loadedList.toMutableList()
        adapter.updateList(weatherList)
        nextId = (weatherList.maxOfOrNull { it.id } ?: 0) + 1
    }
}