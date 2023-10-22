package com.example.myapplication.repository

import com.example.myapplication.model.GetAllPengirimanItem
import com.example.myapplication.model.GetCategorySellerItem
import com.example.myapplication.model.GetDataProductSellerItemItem
import com.example.myapplication.network.ApiService
import javax.inject.Inject

class ProductRepository@Inject constructor(private val api : ApiService) {
    suspend fun getSellerCategory(): List<GetCategorySellerItem>{
        return api.GetCategory()
    }

    suspend fun getPengiriman(): List<GetAllPengirimanItem>{
        return api.getPengiriman()
    }
}