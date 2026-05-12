package com.example.myapplication

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
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var edtYear: EditText
    private lateinit var edtProduction: EditText
    private lateinit var edtMarketShare: EditText
    private lateinit var spinnerCompany: Spinner
    private lateinit var btnAdd: android.widget.TextView
    private lateinit var btnEdit: android.widget.TextView
    private lateinit var btnSave: android.widget.TextView
    private lateinit var phoneMarketRecyclerView: RecyclerView
    private lateinit var phoneMarketAdapter: PhoneMarketAdapter
    private var items = mutableListOf<PhoneMarketItem>()
    private var selectedItem: PhoneMarketItem? = null
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
        edtYear = findViewById(R.id.edt_year)
        edtProduction = findViewById(R.id.edt_production)
        edtMarketShare = findViewById(R.id.edt_market_share)
        spinnerCompany = findViewById(R.id.spinner_company)
        btnAdd = findViewById(R.id.btn_add)
        btnEdit = findViewById(R.id.btn_edit)
        btnSave = findViewById(R.id.btn_save)
        phoneMarketRecyclerView = findViewById(R.id.phone_market_recycler_view)

        // Setup spinner
        setupCompanySpinner()

        // Setup RecyclerView
        setupPhoneMarketRecyclerView()

        // Load data
        loadPhoneMarketData()

        // Setup button listeners
        btnAdd.setOnClickListener { addItem() }
        btnEdit.setOnClickListener { editItem() }
        btnSave.setOnClickListener { savePhoneMarketData() }
    }

    private fun setupCompanySpinner() {
        val companies = arrayOf("-- Chọn công ty --", "Apple", "Samsung", "Xiaomi")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, companies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCompany.adapter = adapter
    }

    private fun setupPhoneMarketRecyclerView() {
        phoneMarketAdapter = PhoneMarketAdapter(items, { item ->
            selectedItem = item
            edtYear.setText(item.year)
            edtMarketShare.setText(item.marketShare)
            edtProduction.setText(item.production)
            spinnerCompany.setSelection(getCompanyPosition(item.company))
        }, { item ->
            deleteItem(item)
        })
        phoneMarketRecyclerView.layoutManager = LinearLayoutManager(this)
        phoneMarketRecyclerView.adapter = phoneMarketAdapter
    }

    private fun getCompanyPosition(company: String): Int {
        return when (company) {
            "Apple" -> 1
            "Samsung" -> 2
            "Xiaomi" -> 3
            else -> 0
        }
    }

    private fun addItem() {
        val year = edtYear.text.toString().trim()
        val production = edtProduction.text.toString().trim()
        val marketShare = edtMarketShare.text.toString().trim()
        val company = spinnerCompany.selectedItem.toString()

        if (company == "-- Chọn công ty --") {
            Toast.makeText(this, "Vui lòng Chọn công ty", Toast.LENGTH_SHORT).show()
            return
        }

        if (year.isEmpty() || production.isEmpty() || marketShare.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val item = PhoneMarketItem(nextId++, year, production, marketShare, company)
        items.add(item)
        phoneMarketAdapter.updateList(items)
        clearFields()
        selectedItem = null
        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
    }

    private fun editItem() {
        if (selectedItem == null) {
            Toast.makeText(this, "Vui lòng chọn một mục để sửa", Toast.LENGTH_SHORT).show()
            return
        }

        val year = edtYear.text.toString().trim()
        val production = edtProduction.text.toString().trim()
        val marketShare = edtMarketShare.text.toString().trim()
        val company = spinnerCompany.selectedItem.toString()

        if (company == "-- Chọn công ty --") {
            Toast.makeText(this, "Vui lòng Chọn công ty", Toast.LENGTH_SHORT).show()
            return
        }

        if (year.isEmpty() || production.isEmpty() || marketShare.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val index = items.indexOfFirst { it.id == selectedItem?.id }
        if (index != -1) {
            AlertDialog.Builder(this)
                .setTitle("Xác nhận sửa")
                .setMessage("Bạn có chắc muốn cập nhật mục này?")
                .setPositiveButton("Sửa") { _, _ ->
                    items[index] = PhoneMarketItem(selectedItem!!.id, year, production, marketShare, company)
                    phoneMarketAdapter.updateList(items)
                    clearFields()
                    selectedItem = null
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    private fun deleteItem(item: PhoneMarketItem) {
        items.removeAll { it.id == item.id }
        phoneMarketAdapter.updateList(items)
        clearFields()
        if (selectedItem?.id == item.id) {
            selectedItem = null
        }
        Toast.makeText(this, "Xoá thành công", Toast.LENGTH_SHORT).show()
    }

    private fun clearFields() {
        edtYear.text.clear()
        edtProduction.text.clear()
        edtMarketShare.text.clear()
        spinnerCompany.setSelection(0)
    }

    private fun savePhoneMarketData() {
        val gson = Gson()
        val json = gson.toJson(items)
        val file = File(filesDir, "Phone")
        file.writeText(json)
        Toast.makeText(this, "Dữ liệu đã được lưu", Toast.LENGTH_SHORT).show()
    }

    private fun loadPhoneMarketData() {
        val file = File(filesDir, "Phone")
        if (!file.exists()) {
            return
        }
        val json = file.readText()
        val gson = Gson()
        val type = object : TypeToken<List<PhoneMarketItem>>() {}.type
        val loadedList: List<PhoneMarketItem> = gson.fromJson(json, type)
        items = loadedList.toMutableList()
        phoneMarketAdapter.updateList(items)
        nextId = (items.maxOfOrNull { it.id } ?: 0) + 1
    }
}