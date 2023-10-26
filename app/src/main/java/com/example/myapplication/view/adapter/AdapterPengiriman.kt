package com.example.myapplication.view.adapter

import android.annotation.SuppressLint
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
import com.example.myapplication.model.GetAllPengirimanItem
import com.example.myapplication.view.seller.DaftarJualPengiriman
import com.example.myapplication.viewmodel.ViewModelProductSeller
import kotlinx.android.synthetic.main.item_produk_pengiriman.view.*
import kotlinx.android.synthetic.main.item_produk_pengiriman.view.button_delete_card
import kotlinx.android.synthetic.main.item_produk_pengiriman.view.button_edit_card

class AdapterPengiriman : RecyclerView.Adapter<AdapterPengiriman.ViewHolder> () {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView)

    fun setDataPengiriman(list: List<GetAllPengirimanItem>){
        this.dataPengiriman = list
    }

    private var dataPengiriman : List<GetAllPengirimanItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_pengiriman, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (dataPengiriman.isNullOrEmpty()) {
            0
        } else {
            dataPengiriman!!.size
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView){
            tvPengirimanKendaraan.text = "Jenis Kendaraan : "+ dataPengiriman!![position].kendaraan
            tvTgl_product_MaxBerat.text =  "Max Berat : "+dataPengiriman!![position].max_berat
            tvPengirimanHarga.text = "Harga : "+dataPengiriman!![position].harga
        }

        holder.itemView.button_edit_card.setOnClickListener {
            // Create a custom dialog
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.customdialog_pengiriman, null)
            val dialogbuilder = AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .create()

            // Get references to views in the custom dialog
            val editTextkendaraan = dialogView.findViewById<EditText>(R.id.cd_kendaraan)
            val editTextharga = dialogView.findViewById<EditText>(R.id.cd_harga)
            val editTextberat = dialogView.findViewById<EditText>(R.id.cd_berat)
            val buttonUpdate = dialogView.findViewById<Button>(R.id.btn_editPengiriman)

            // Populate the dialog fields with data
            val selectedItem = dataPengiriman!![position]
            editTextkendaraan.setText(selectedItem.kendaraan)
            editTextharga.setText(selectedItem.harga)
            editTextberat.setText(selectedItem.max_berat)

            // Set a click listener for the Update button
            buttonUpdate.setOnClickListener {
                // Get the updated data from the dialog
                val updatedId = selectedItem.id
                val updatedKendaraan = editTextkendaraan.text.toString()
                val updatedBerat = editTextberat.text.toString()
                val updatedHarga = editTextharga.text.toString()

                val viewModelProductSeller = ViewModelProvider(holder.itemView.context as DaftarJualPengiriman)[ViewModelProductSeller::class.java]
                viewModelProductSeller.editPengiriman(updatedId.toInt(),updatedKendaraan,updatedHarga,updatedBerat)
                Toast.makeText(it.context, "Berhasil Diubah", Toast.LENGTH_SHORT).show()
                (holder.itemView.context as DaftarJualPengiriman).initRecyclerView()
                // Dismiss the dialog
                dialogbuilder.dismiss()
            }
            // Show the custom dialog
            dialogbuilder.setCancelable(true)
            dialogbuilder.show()
        }

        holder.itemView.button_delete_card.setOnClickListener {
            val viewModelProductSeller = ViewModelProvider(holder.itemView.context as DaftarJualPengiriman)[ViewModelProductSeller::class.java]
            AlertDialog.Builder(it.context)
                .setTitle("KONFIRMASI HAPUS")
                .setMessage("Anda Yakin Ingin Menghapus Kategori Ini ?")
                .setPositiveButton("YA"){ _: DialogInterface, _: Int ->
                    Toast.makeText(it.context, "Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                    viewModelProductSeller.deletePengiriman(dataPengiriman!![position].id.toInt())
                    (holder.itemView.context as DaftarJualPengiriman).initView()
                }
                .setNegativeButton("TIDAK"){ dialogInterface: DialogInterface, _: Int ->
                    Toast.makeText(it.context, "Tidak Jadi Dihapus", Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                }
                .show()
        }
    }
}