@file:Suppress("SimplifyBooleanWithConstants", "UselessCallOnNotNull")

package com.example.myapplication.view.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.datastore.UserManager
import com.example.myapplication.model.GetHistoryItem
import com.example.myapplication.view.buyer.NotifikasiBuyerActivity
import com.example.myapplication.view.seller.DaftarJualPromo
import com.example.myapplication.viewmodel.ViewModelProductSeller
import kotlinx.android.synthetic.main.item_notifikasi_buyer.view.*

class AdapterNotifikasiBuyer(private  var onClick : (GetHistoryItem)->Unit):RecyclerView.Adapter<AdapterNotifikasiBuyer.ViewHolder>() {
    class ViewHolder(itemView : View):RecyclerView.ViewHolder(itemView)

    private var dataNotif : List<GetHistoryItem>? = null
    private lateinit var  userManager: UserManager
    fun setNotif(Notif : List<GetHistoryItem>){
        this.dataNotif = Notif
    }
    @SuppressLint("NotifyDataSetChanged")
    fun clearNotif() {
        dataNotif = null
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewitem = LayoutInflater.from(parent.context).inflate(R.layout.item_notifikasi_buyer,parent, false)
        return ViewHolder(viewitem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        userManager = UserManager(holder.itemView.context as NotifikasiBuyerActivity)
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .override(150,150)
            .skipMemoryCache(true)
        with(holder.itemView){
            val currentUserStatus = userManager.fetchstatus()
            with(dataNotif!![position]){
                val viewModelProductSeller = ViewModelProvider(holder.itemView.context as NotifikasiBuyerActivity)[ViewModelProductSeller::class.java]
                cardNotifikasiBuyer.setOnClickListener {
                    onClick(dataNotif!![position])
                    if (currentUserStatus == "seller"){
                        viewModelProductSeller.editDibaca(dataNotif!![position].id.toInt(),"sudah")
                    }
                }
                Glide.with(holder.itemView.context)
                    .load(dataNotif!![position].gambar)
                    .error(R.drawable.ic_launcher_background)
                    .override(75,75)
                    .apply(requestOptions)
                    .into(holder.itemView.gambarProdukBuyer)
                if (currentUserStatus == "buyer"){
                    notikasiBuyer_alert.isInvisible = true
                }
                if (dibaca == "belum"){
                    notikasiBuyer_alert.setImageResource(R.drawable.ic_baseline_circle_24)
                }else if (status == "Selesai"){
                    notikasiBuyer_alert.setImageResource(R.drawable.ic_baseline_circle_ijo)
                }else if (dibaca == "sudah"){
                    notikasiBuyer_alert.setImageResource(R.drawable.baseline_circle_24_abu)
                }
                notifikasiBuyer_namaProduk.text = "Nama Produk : $nama_produk"
                notikasiBuyer_statusproduk.text = "Status : $status"
                notifikasiBuyer_harga.text = "Harga : Rp. $harga_produk"
                notifikasiBuyer_tawar.text = "Jumlah : $jumlah_produk"
                notikasiBuyer_waktu.text = tgl_transaksi

            }
        }
    }

    override fun getItemCount(): Int {
        return if (dataNotif.isNullOrEmpty()) {
            0
        } else {
            dataNotif!!.size
        }
    }
}