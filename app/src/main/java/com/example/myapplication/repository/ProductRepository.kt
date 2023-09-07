package and5.abrar.e_commerce.repository

import com.example.myapplication.model.GetCategorySellerItem
import com.example.myapplication.network.ApiService
import javax.inject.Inject

class ProductRepository@Inject constructor(private val api : ApiService) {
    suspend fun getSellerCategory(): List<GetCategorySellerItem>{
        return api.GetCategory()
    }


}