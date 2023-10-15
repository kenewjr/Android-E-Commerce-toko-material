package com.example.myapplication.view.adapter


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.model.GetAllProdukItem
import com.example.myapplication.view.seller.DaftarJualActivity
import com.example.myapplication.viewmodel.ViewModelProductSeller
import kotlinx.android.synthetic.main.item_product_seller.view.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class AdapterProductSeller(private  var onClick : (GetAllProdukItem)->Unit) : RecyclerView.Adapter<AdapterProductSeller.ViewHolder> () {

    private var dataProductSeller : List<GetAllProdukItem>? = null

    fun setDataProductSeller(list: List<GetAllProdukItem>){
        this.dataProductSeller = list
    }

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product_seller, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tvJudul_product_seller.text = "Nama Produk : ${dataProductSeller!![position].nama}"
        holder.itemView.tvKategori_product_seller.text = "Kategori : ${dataProductSeller!![position].kategori}"
        holder.itemView.tvHarga_product_seller.text = "Harga : Rp. ${dataProductSeller!![position].harga}"
        holder.itemView.tvStatus_product_seller.text = "Stok : ${dataProductSeller!![position].stok}"
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).override(150,150).skipMemoryCache(true)
        Glide.with(holder.itemView.context)
            .load(dataProductSeller!![position].gambar)
            .apply(requestOptions)
            .into(holder.itemView.imageProductSeller)

        holder.itemView.tvStatus_product_seller.isInvisible = true

        holder.itemView.button_delete_card.setOnClickListener {
            val viewModelProductSeller = ViewModelProvider(holder.itemView.context as DaftarJualActivity)[ViewModelProductSeller::class.java]
            AlertDialog.Builder(it.context)
                .setTitle("KONFIRMASI HAPUS")
                .setMessage("Anda Yakin Ingin Menghapus Data Produk Ini ?")

                .setPositiveButton("YA"){ _: DialogInterface, _: Int ->
                    Toast.makeText(it.context, "Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                    viewModelProductSeller.deleteProduct(delete="produk", id = dataProductSeller!![position].id.toInt())
                    (holder.itemView.context as DaftarJualActivity).initView()
                }
                .setNegativeButton("TIDAK"){ dialogInterface: DialogInterface, _: Int ->
                    Toast.makeText(it.context, "Tidak Jadi Dihapus", Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                }
                .show()
        }

        holder.itemView.button_edit_card.setOnClickListener {
            onClick(dataProductSeller!![position])
        }
    }

    override fun getItemCount(): Int {
        return if (dataProductSeller.isNullOrEmpty()) {
            0
        } else {
            dataProductSeller!!.size
        }
    }

}