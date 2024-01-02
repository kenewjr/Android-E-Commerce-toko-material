package com.example.myapplication.view.adapter

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.GetPromoItem
import com.example.myapplication.view.seller.DaftarJualPromo
import com.example.myapplication.viewmodel.ViewModelProductSeller
import kotlinx.android.synthetic.main.item_produk_pengiriman.view.*

class AdapterPromo : RecyclerView.Adapter<AdapterPromo.ViewHolder>() {
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView)

    fun setDataPromo(list : List<GetPromoItem>){
        this.dataPromo = list
    }

    private var dataPromo : List<GetPromoItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_pengiriman, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (dataPromo.isNullOrEmpty()) {
            0
        } else {
            dataPromo!!.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView){
            tvPengirimanKendaraan.text = "Harga Diskon : Rp."+dataPromo!![position].harga_diskon
            tvPengirimanHarga.text = "Minimal Harga : Rp."+ dataPromo!![position].min_harga
            tvTgl_product_MaxBerat.text =  "Maksimal Harga : Rp."+dataPromo!![position].max_harga
        }
        holder.itemView.button_edit_card.setOnClickListener {
            // Create a custom dialog
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.customdialog_promo, null)
            val dialogbuilder = AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .create()

            // Get references to views in the custom dialog
            val editTextmin = dialogView.findViewById<EditText>(R.id.cd_min)
            val editTextmax = dialogView.findViewById<EditText>(R.id.cd_max)
            val editTextdiskon = dialogView.findViewById<EditText>(R.id.cd_diskon)
            val buttonUpdate = dialogView.findViewById<Button>(R.id.btn_editPromo)

            // Populate the dialog fields with data
            val selectedItem = dataPromo!![position]
            editTextmin.setText(selectedItem.min_harga)
            editTextmax.setText(selectedItem.max_harga)
            editTextdiskon.setText(selectedItem.harga_diskon)

            // Set a click listener for the Update button
            buttonUpdate.setOnClickListener {
                // Get the updated data from the dialog
                val updatedId = selectedItem.id
                val updatedMin = editTextmin.text.toString()
                val updatedMax = editTextmax.text.toString()
                val updatedDiskon = editTextdiskon.text.toString()
                val minVal = editTextmin.text.toString().toDoubleOrNull()
                val maxVal = editTextmax.text.toString().toDoubleOrNull()
                val diskon = editTextdiskon.text.toString().toDoubleOrNull()
                if (minVal != null && maxVal != null && diskon != null && maxVal > minVal && diskon < minVal ) {
                    val viewModelProductSeller = ViewModelProvider(holder.itemView.context as DaftarJualPromo)[ViewModelProductSeller::class.java]
                    viewModelProductSeller.editPromo(updatedId.toInt(),updatedMin,updatedMax,updatedDiskon)
                    Toast.makeText(it.context, "Berhasil Diubah", Toast.LENGTH_SHORT).show()
                    (holder.itemView.context as DaftarJualPromo).initRecyclerView()
                    // Dismiss the dialog
                    dialogbuilder.dismiss()
                } else if(diskon == null){
                    Toast.makeText(it.context, "Diskon Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(it.context, "Nilai di Maksimal Harga harus lebih besar dari Minimal Harga dan Diskon lebih kecil Dari Minimal Harga", Toast.LENGTH_SHORT).show()
                }
            }
            // Show the custom dialog
            dialogbuilder.setCancelable(true)
            dialogbuilder.show()
        }

        holder.itemView.button_delete_card.setOnClickListener {
            val viewModelProductSeller = ViewModelProvider(holder.itemView.context as DaftarJualPromo)[ViewModelProductSeller::class.java]
            AlertDialog.Builder(it.context)
                .setTitle("KONFIRMASI HAPUS")
                .setMessage("Anda Yakin Ingin Menghapus Promo Ini ?")
                .setPositiveButton("YA"){ _: DialogInterface, _: Int ->
                    Toast.makeText(it.context, "Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                    viewModelProductSeller.deletePromo(dataPromo!![position].id.toInt())
                    (holder.itemView.context as DaftarJualPromo).initView()
                }
                .setNegativeButton("TIDAK"){ dialogInterface: DialogInterface, _: Int ->
                    Toast.makeText(it.context, "Tidak Jadi Dihapus", Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                }
                .show()
        }
    }
}