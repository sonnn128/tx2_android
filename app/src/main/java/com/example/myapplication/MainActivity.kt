package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var edtAreaCode: EditText
    private lateinit var edtTemperature: EditText
    private lateinit var edtHumidity: EditText
    private lateinit var spinnerWeatherType: Spinner
    private lateinit var btnAdd: android.widget.TextView
    private lateinit var btnEdit: android.widget.TextView
    private lateinit var btnSave: android.widget.TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeatherAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var weatherList = mutableListOf<Weather>()
    private var selectedWeather: Weather? = null
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

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("WeatherApp", Context.MODE_PRIVATE)

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

        // Setup RecyclerView
        setupRecyclerView()

        // Load data from cache
        loadWeatherData()

        // Setup button listeners
        btnAdd.setOnClickListener { addWeather() }
        btnEdit.setOnClickListener { deleteSelectedWeather() }
        btnSave.setOnClickListener { saveWeatherData() }
    }

    private fun setupSpinner() {
        val weatherTypes = arrayOf("-- Chọn kiểu thời tiết --", "Nắng", "Mây", "Mưa")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, weatherTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWeatherType.adapter = adapter
    }

    private fun setupRecyclerView() {
        adapter = WeatherAdapter(weatherList, { weather ->
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
            "Nắng" -> 1
            "Mây" -> 2
            "Mưa" -> 3
            else -> 0
        }
    }

    private fun addWeather() {
        val areaCode = edtAreaCode.text.toString().trim()
        val temperature = edtTemperature.text.toString().trim()
        val humidity = edtHumidity.text.toString().trim()
        val weatherType = spinnerWeatherType.selectedItem.toString()

        if (weatherType == "-- Chọn kiểu thời tiết --") {
            Toast.makeText(this, "Vui lòng chọn kiểu thời tiết", Toast.LENGTH_SHORT).show()
            return
        }

        if (areaCode.isEmpty() || temperature.isEmpty() || humidity.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val weather = Weather(nextId++, areaCode, temperature, humidity, weatherType)
        weatherList.add(weather)
        adapter.updateList(weatherList)
        clearFields()
        selectedWeather = null
        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
    }

    private fun deleteSelectedWeather() {
        val selected = selectedWeather
        if (selected == null) {
            Toast.makeText(this, "Vui lòng chọn một mục để xóa", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa mục này?")
            .setPositiveButton("Xóa") { _, _ ->
                weatherList.removeAll { it.id == selected.id }
                adapter.updateList(weatherList)
                clearFields()
                selectedWeather = null
                Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteWeatherItem(weather: Weather) {
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
        sharedPreferences.edit()
            .putString("weather_data", json)
            .apply()
        Toast.makeText(this, "Dữ liệu đã được lưu", Toast.LENGTH_SHORT).show()
    }

    private fun loadWeatherData() {
        val json = sharedPreferences.getString("weather_data", null) ?: return
        val gson = Gson()
        val type = object : TypeToken<List<Weather>>() {}.type
        val loadedList: List<Weather> = gson.fromJson(json, type)
        weatherList = loadedList.toMutableList()
        adapter.updateList(weatherList)
        nextId = (weatherList.maxOfOrNull { it.id } ?: 0) + 1
    }
}