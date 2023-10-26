package com.example.myapplication.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.GetKomentarItem
import kotlinx.android.synthetic.main.item_komentar.view.*

class AdapterKomentar :RecyclerView.Adapter<AdapterKomentar.ViewHolder>() {

    class ViewHolder(itemView : View):RecyclerView.ViewHolder(itemView)

    private var dataKomentar : List<GetKomentarItem>? = null

    fun setKomentar(komentar : List<GetKomentarItem>){
        this.dataKomentar = komentar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewitem = LayoutInflater.from(parent.context).inflate(R.layout.item_komentar,parent, false)
        return ViewHolder(viewitem)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder.itemView){
                with(dataKomentar!![position]){
                    tvNama_pembeli.text = nama_pembeli
                    tvKomentar.text = komentar
                    tvWaktu.text = create_at
                }
            }
    }

    override fun getItemCount(): Int {
        return if (dataKomentar == null){
            0
        }else{
            dataKomentar!!.size
        }
    }
}