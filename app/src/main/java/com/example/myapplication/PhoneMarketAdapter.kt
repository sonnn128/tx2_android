package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhoneMarketAdapter(
    private var items: MutableList<PhoneMarketItem>,
    private val onItemClick: (PhoneMarketItem) -> Unit,
    private val onDeleteClick: (PhoneMarketItem) -> Unit
) : RecyclerView.Adapter<PhoneMarketAdapter.PhoneMarketViewHolder>() {

    inner class PhoneMarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtCompany: TextView = itemView.findViewById(R.id.txt_company)
        private val txtYear: TextView = itemView.findViewById(R.id.txt_year)
        private val txtProduction: TextView = itemView.findViewById(R.id.txt_production)
        private val txtMarketShare: TextView = itemView.findViewById(R.id.txt_market_share)
        private val imgCompanyIcon: ImageView = itemView.findViewById(R.id.img_company_icon)

        fun bind(item: PhoneMarketItem) {
            txtCompany.text = "Công ty: ${item.company}"
            txtYear.text = "Năm: ${item.year}"
            txtProduction.text = "Sản xuất: ${item.production}"
            txtMarketShare.text = "Thị phần: ${item.marketShare}"

            when (item.company) {
                "Apple" -> imgCompanyIcon.setImageResource(R.drawable.w1)
                "Samsung" -> imgCompanyIcon.setImageResource(R.drawable.w3)
                "Xiaomi" -> imgCompanyIcon.setImageResource(R.drawable.w2)
                else -> imgCompanyIcon.setImageResource(R.drawable.w1)
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneMarketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.phone_market_item, parent, false)
        return PhoneMarketViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhoneMarketViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<PhoneMarketItem>) {
        items = newList.toMutableList()
        notifyDataSetChanged()
    }
}
