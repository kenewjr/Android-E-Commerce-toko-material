package com.example.myapplication.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.GetCategorySellerItem
import com.example.myapplication.view.HomeActivity
import com.example.myapplication.view.seller.DaftarJualActivity
import kotlinx.android.synthetic.main.item_product_homectgy.view.*

class AdapterHomeCategory(private var onClick : (GetCategorySellerItem)->Unit) : RecyclerView.Adapter<AdapterHomeCategory.ViewHolder> () {
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView)
    var row_index = -1
    private var isRecyclerViewEnabled = true
    // ... other existing code

    fun setRecyclerViewEnabled(isEnabled: Boolean) {
        isRecyclerViewEnabled = isEnabled
        notifyDataSetChanged()
    }
    fun setDataCategory(list: List<GetCategorySellerItem>){
        this.dataCategory = list
    }
    private var dataCategory : List<GetCategorySellerItem>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product_homectgy, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (dataCategory.isNullOrEmpty()) {
            0
        } else {
            dataCategory!!.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.cvHomeCtgy.setOnClickListener {
                isRecyclerViewEnabled
                onClick(dataCategory!![position])
                row_index = position
                notifyDataSetChanged()
        }
        if (row_index == position) {
            holder.itemView.rvl_kategori.setBackgroundResource(R.color.darker)
            holder.itemView.tvCtgy.setTextColor(Color.parseColor("#FFFFFFFF"))
        } else {
            holder.itemView.rvl_kategori.setBackgroundResource(R.color.soft)
            holder.itemView.tvCtgy.setTextColor(Color.parseColor("#3F72AF"))
        }

        if(!isRecyclerViewEnabled){
            holder.itemView.rvl_kategori.setBackgroundResource(R.color.soft)
            holder.itemView.tvCtgy.setTextColor(Color.parseColor("#3F72AF"))
        }

        holder.itemView.tvCtgy.text = dataCategory!![position].name
    }
}