@file:Suppress("unused")

package com.example.myapplication.view.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.GetHistoryItem
import kotlinx.android.synthetic.main.item_product_seller.view.*

class AdapterTerjual(private  var onClick :(GetHistoryItem)->Unit) : RecyclerView.Adapter<AdapterTerjual.ViewHolder>() {
    private var dataOrder : List<GetHistoryItem>? = null
    fun setDataOrder(list : List<GetHistoryItem>){
        this.dataOrder = list
    }
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product_seller, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.cvSellerHistory.setOnClickListener {
            onClick(dataOrder!![position])
        }
        holder.itemView.tvJudul_product_seller.text = "Nama Produk : ${dataOrder!![position].nama_produk}"
        holder.itemView.tvKategori_product_seller.text = "Jumlah Produk : ${dataOrder!![position].jumlah_produk}"
        holder.itemView.tvHarga_product_seller.text = "Harga : Rp. ${dataOrder!![position].total_harga}"
        holder.itemView.tvStatus_product_seller.text = "Harga Produk : Rp.${dataOrder!![position].harga_produk}"
        holder.itemView.textTanggalUpdate.text = dataOrder!![position].tgl_transaksi
        Glide.with(holder.itemView.context)
            .load(dataOrder!![position].gambar)
            .into(holder.itemView.imageProductSeller)
        holder.itemView.button_edit_card.isInvisible = true
        holder.itemView.button_delete_card.isInvisible = true
        holder.itemView.textTanggalUpdate.isVisible = true
    }

    override fun getItemCount(): Int {
        return if (dataOrder.isNullOrEmpty()) {
            0
        } else {
            dataOrder!!.size
        }
    }
}