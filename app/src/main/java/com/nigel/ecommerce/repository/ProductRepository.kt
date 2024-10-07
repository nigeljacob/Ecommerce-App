package com.nigel.ecommerce.repository

import android.content.Context
import com.nigel.ecommerce.models.Category
import com.nigel.ecommerce.models.Order
import com.nigel.ecommerce.models.OrderItem
import com.nigel.ecommerce.models.Product
import com.nigel.ecommerce.models.Review
import com.nigel.ecommerce.services.ApiService
import com.nigel.ecommerce.utils.SharedPreferenceHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ProductRepository(context: Context) {

    var okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
        .readTimeout(60, TimeUnit.SECONDS) // Read timeout
        .writeTimeout(60, TimeUnit.SECONDS) // Write timeout
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.9:5077")
        .client(okHttpClient)
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
            for(product in tempProductsArray) {
                val newCategory = Category(
                    product.get("id") as String,
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

    suspend fun getOrderHistory(context: Context, id: String): MutableList<Order> {
        val orders = mutableListOf<Order>()
        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.getUserOrders(token, id).execute()
        val message = response.body()?.get("message") as String?
        if(response.isSuccessful) {
            if(message.equals("Recived successfully")) {
                val tempOrderArray = response.body()?.get("data") as List<Map<String, Any>>
                println(tempOrderArray)
                for(order in tempOrderArray) {
                    val newOrder = Order(
                        order.get("orderId") as String,
                        order.get("orderNo") as String,
                        order.get("customerNo") as String,
                        order.get("deliveryAddress") as String,
                        order.get("orderDate") as String,
                        order.get("status") as String,
                        order.get("isCancelRequested") as Boolean?,
                        (order.get("orderLines") as List<Map<String, Any>>).map { orderItem ->
                            OrderItem(
                                orderItem.get("orderLineNo") as String,
                                orderItem.get("productNo") as String,
                                orderItem.get("vendorNo") as String,
                                orderItem.get("orderNo") as String,
                                orderItem.get("status") as String,
                                orderItem.get("qty") as Double,
                                orderItem.get("unitPrice") as Double,
                                orderItem.get("total") as Double,
                                orderItem.get("productName") as String,
                                orderItem.get("vendorName") as String
                            )
                        }
                    )

                    orders.add(0, newOrder)
            }

            }
        } else {
            println(response)

        }

        return orders
    }

    suspend fun getProductById(context: Context, id: String): Product? {
        var product: Product? = null
        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.getProductByID(token, id).execute()
        if(response.isSuccessful) {
            println(response)
            val tempProduct = response.body()?.get("data") as Map<String, Any>?
            if(tempProduct != null) {
                product = Product(
                    tempProduct.get("id") as String,
                    tempProduct.get("name") as String,
                    tempProduct.get("images") as List<String>,
                    tempProduct.get("category") as String,
                    tempProduct.get("subCategory") as String,
                    tempProduct.get("price") as Double,
                    tempProduct.get("description") as String,
                    tempProduct.get("active") as Boolean,
                    tempProduct.get("stockCount") as Double,
                    tempProduct.get("vendorId") as String,
                    tempProduct.get("lowStockThreshold") as Double,
                    tempProduct.get("isPartOfPendingOrder") as Boolean,
                    tempProduct.get("subCategoryName") as String?,
                )
            }
        }

        return product
    }


    suspend fun addReview(context: Context, review: Map<String, Any>): Boolean {

        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.addReview(token, review).execute()
        println(response.body()?.get("message"))
        if(response.isSuccessful) {
            return true
        } else {
            println(response)
        }
        return false
    }

    suspend fun getCustomerReviews(context: Context, id: String): MutableList<Review> {
        val reviews = mutableListOf<Review>()
        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.getCustomerFeedback(token, id).execute()
        if(response.isSuccessful) {
            val tmpReviewArray = response.body()?.get("data") as List<Map<String, Any>>
            for(review in tmpReviewArray) {
                val newReview = Review(
                    review.get("id") as String? ?: "",
                    review.get("customerId") as String? ?: "",
                    review.get("productId") as String? ?: "",
                    review.get("message") as String? ?: "No Message",
                    review.get("rating") as Double? ?: 0.00,
                )

                reviews.add(0, newReview)
            }
        }

        return reviews
    }

    suspend fun getProductReviews(context: Context, id: String): MutableList<Review> {
        val reviews = mutableListOf<Review>()
        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.getProductReviews(token, id).execute()
        if(response.isSuccessful) {
            val tmpReviewArray = response.body()?.get("data") as List<Map<String, Any>>
            for(review in tmpReviewArray) {
                val newReview = Review(
                    review.get("id") as String? ?: "",
                    review.get("customerId") as String? ?: "",
                    review.get("productId") as String? ?: "",
                    review.get("message") as String? ?: "No Message",
                    review.get("rating") as Double? ?: 0.00,
                )

                reviews.add(0, newReview)
            }
        }

        return reviews
    }

    suspend fun createOrder(context: Context, order: Map<String, Any>): Boolean {
        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val requestBody = order
        val response = apiService.createOrder(token, requestBody).execute()
        if(response.isSuccessful) {
            return true
        } else {
            println(response)
        }

        return false
    }

    suspend fun updateReview(context: Context, review: Map<String, Any>, id: String): Boolean {
        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.updateReview(token, review, id).execute()
        if(response.isSuccessful) {
            return true
        } else {
            println(response)
        }
        return false
    }

    suspend fun updateOrder(context: Context, order: Map<String, Any>, id: String): Boolean {
        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.updateOrder(token, order, id).execute()
        if(response.isSuccessful) {
            return true
        } else{
            println(response)
            return false
        }
    }

    suspend fun cancelOrder(context: Context, id: String): Boolean {
        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.cancelOrder(token, id).execute()
        if(response.isSuccessful) {
            return true
        } else{
            println(response)
            return false
        }
    }

}