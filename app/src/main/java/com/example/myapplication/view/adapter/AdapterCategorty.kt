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
import com.example.myapplication.model.GetCategorySellerItem
import com.example.myapplication.view.seller.DaftarJualCategory
import com.example.myapplication.viewmodel.ViewModelProductSeller
import kotlinx.android.synthetic.main.item_product_category.view.*

class AdapterCategorty : RecyclerView.Adapter<AdapterCategorty.ViewHolder> () {
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView)
    fun setDataCategory(list: List<GetCategorySellerItem>){
        this.dataCategory = list
    }
    private var dataCategory : List<GetCategorySellerItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product_category, parent, false)
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
        with(holder.itemView){
                tvCtgy.text =  dataCategory!![position].name
                tvTgl_product_Ctgy.text =  dataCategory!![position].createdAt
        }

        holder.itemView.button_edit_card.setOnClickListener {
            // Create a custom dialog
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.customdialog_editctgy, null)
            val dialogbuilder = AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .create()

            // Get references to views in the custom dialog
            val editTextname = dialogView.findViewById<EditText>(R.id.cd_edt_ctgy)
            val buttonUpdate = dialogView.findViewById<Button>(R.id.btn_editCtgy)

            // Populate the dialog fields with data
            val selectedItem = dataCategory!![position]
            editTextname.setText(selectedItem.name)

            // Set a click listener for the Update button
            buttonUpdate.setOnClickListener {
                // Get the updated data from the dialog
                val updatedId = selectedItem.id
                val updatedName = editTextname.text.toString()

                val viewModelProductSeller = ViewModelProvider(holder.itemView.context as DaftarJualCategory)[ViewModelProductSeller::class.java]
                viewModelProductSeller.editCtgy(updatedId,updatedName)
                Toast.makeText(it.context, "Berhasil Diubah", Toast.LENGTH_SHORT).show()
                (holder.itemView.context as DaftarJualCategory).initView()
                // Dismiss the dialog
                dialogbuilder.dismiss()
            }
            // Show the custom dialog
            dialogbuilder.setCancelable(true)
            dialogbuilder.show()
        }

        holder.itemView.button_delete_card.setOnClickListener {
            val viewModelProductSeller = ViewModelProvider(holder.itemView.context as DaftarJualCategory)[ViewModelProductSeller::class.java]
            AlertDialog.Builder(it.context)
                .setTitle("KONFIRMASI HAPUS")
                .setMessage("Anda Yakin Ingin Menghapus Kategori Ini ?")
                .setPositiveButton("YA"){ _: DialogInterface, _: Int ->
                    Toast.makeText(it.context, "Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                    viewModelProductSeller.deleteCtgy(dataCategory!![position].id)
                    (holder.itemView.context as DaftarJualCategory).initView()
                }
                .setNegativeButton("TIDAK"){ dialogInterface: DialogInterface, _: Int ->
                    Toast.makeText(it.context, "Tidak Jadi Dihapus", Toast.LENGTH_SHORT).show()
                    dialogInterface.dismiss()
                }
                .show()
        }
    }

}