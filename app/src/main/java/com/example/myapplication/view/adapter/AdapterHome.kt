@file:Suppress("UselessCallOnNotNull", "UselessCallOnNotNull")

package com.example.myapplication.view.adapter


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.model.GetAllProdukItem
import kotlinx.android.synthetic.main.item_product_home.view.*


class AdapterHome(private var onClick : (GetAllProdukItem)->Unit):RecyclerView.Adapter<AdapterHome.ViewHolder>() {
    class ViewHolder(itemView : View):RecyclerView.ViewHolder(itemView)
    private var dataProduk : List<GetAllProdukItem>? = null

    fun setProduk(produk : List<GetAllProdukItem>){
        this.dataProduk = produk
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewitem = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_home,parent, false)
        return ViewHolder(viewitem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.cardProduct.setOnClickListener {
            onClick(dataProduk!![position])
        }
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .override(150,150)
            .skipMemoryCache(true)

        holder.itemView.tvJudul_product.text = dataProduk!![position].nama
        if (dataProduk!![position].gambar.isNullOrEmpty()){
            Glide.with(holder.itemView.context)
                .load(R.drawable.ic_launcher_background)
                .apply(requestOptions)
                .into(holder.itemView.imageProduct)
        }else {
            Glide.with(holder.itemView.context)
                .load(dataProduk!![position].gambar)
                .apply(requestOptions)
                .into(holder.itemView.imageProduct)
        }
        holder.itemView.tvKategori_product.text = dataProduk!![position].kategori
        holder.itemView.tvHarga_product.text = dataProduk!![position].harga.toString()
    }

    override fun getItemCount(): Int {
        return if (dataProduk == null){
            0
        }else{
            dataProduk!!.size
        }
    }
}