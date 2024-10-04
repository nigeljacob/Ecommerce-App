package com.nigel.ecommerce.repository

import android.content.Context
import com.nigel.ecommerce.models.Category
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.services.ApiService
import com.nigel.ecommerce.utils.SharedPreferenceHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header

class ProductRepository(context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.9:5077")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    suspend fun getAllProducts(context: Context): MutableList<Product> {
        val products = mutableListOf<Product>()
        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.getAllProducts(token).execute()
        val message = response.body()?.get("message") as String?
        if(response.isSuccessful) {
            if(message.equals("Successful")) {
                val tempProductsArray = response.body()?.get("data") as List<Map<String, Any>>
                for(product in tempProductsArray) {
                    val newProduct = Product(
                        product.get("id") as String,
                        product.get("name") as String,
                        product.get("images") as List<String>,
                        product.get("category") as String,
                        product.get("subCategory") as String,
                        product.get("price") as Double,
                        product.get("description") as String,
                        product.get("active") as Boolean,
                        product.get("stockCount") as Double,
                        product.get("vendorId") as String,
                        product.get("lowStockThreshold") as Double,
                        product.get("isPartOfPendingOrder") as Boolean,
                        product.get("subCategoryName") as String?,
                    )
                    products.add(newProduct)
                }
            }
        } else {

        }

        return products
    }

    fun getCategoryImage(name: String): String {
        if(name.equals("Phone")) {
            return "https://static.vecteezy.com/system/resources/thumbnails/006/624/453/small_2x/smartphone-icon-design-phone-symbol-free-vector.jpg"
        } else if(name.equals("Laptop")) {
            return "https://cdn4.vectorstock.com/i/1000x1000/42/78/laptop-icon-in-trendy-flat-style-isolated-on-white-vector-24914278.jpg"
        } else if (name.equals("Tv")) {
            return "https://static.vecteezy.com/system/resources/previews/010/451/460/non_2x/tv-monitor-icon-isolated-on-white-background-free-vector.jpg"
        } else if (name.equals("Mouse")) {
            return "https://www.creativefabrica.com/wp-content/uploads/2021/07/16/Mouse-cursor-icon-Graphics-14825415-1-1-580x386.jpg"
        }
        return "https://t4.ftcdn.net/jpg/03/21/50/71/360_F_321507151_VErgLgPcedXvBcNSmjtBh9ICVrHmNVMi.jpg"
    }

    suspend fun getAllCategories(context: Context): List<Category> {
        val categories = mutableListOf<Category>()

        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.getAllCategories(token).execute()
        if(response.isSuccessful) {
            val tempProductsArray = response.body() as List<Map<String, Any>>
            println(tempProductsArray)
            for(product in tempProductsArray) {
                val newCategory = Category(
                    product.get("name") as String,
                    getCategoryImage(product.get("name") as String)
                )
                categories.add(newCategory)
            }
        } else {
            println("error")
        }

        return categories
    }

}