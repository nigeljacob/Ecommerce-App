package com.nigel.ecommerce.services

import com.google.gson.JsonObject
import com.nigel.ecommerce.models.Review
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("/api/User/login")
    fun login(@Body requestBody: Map<String, String>): Call<Map<String, Any>>

    @POST("/api/User/self-register")
    fun register(@Body requestBody: Map<String, String>): Call<Map<String, Any>>

    @GET("/api/Product/")
    fun getAllProducts(@Header("Authorization") token: String): Call<Map<String, Any>>

    @GET("/api/MasterData/GetCategories")
    fun getAllCategories(@Header("Authorization") token: String): Call<List<Map<String, Any>>>

    @GET("/api/User/token")
    fun getUserDetails(@Header("Authorization") token: String): Call<Map<String, Any>>

    @GET("/api/Order/history/{id}")
    fun getUserOrders(@Header("Authorization") token: String, @Path("id") id: String):Call<Map<String, Any>>

    @GET("/api/Product/{id}")
    fun getProductByID(@Header("Authorization") token: String, @Path("id") id: String): Call<Map<String, Any>>

    @GET("/api/Feedback/customer")
    fun getCustomerFeedback(@Header("Authorization") token: String): Call<Map<String, Any>>

    @POST("/api/Feedback/create")
    @JvmSuppressWildcards
    fun addReview(@Header("Authorization") token: String, @Body requestBody: Map<String, Any>): Call<Map<String, Any>>

    @POST("/api/Order/create")
    @JvmSuppressWildcards
    fun createOrder(@Header("Authorization") token: String, @Body requestBody: Map<String, Any>): Call<Map<String, Any>>

    @GET("/api/User/deactivate/{id}")
    fun deactivateAccount(@Header("Authorization") token: String, @Path("id") id: String): Call<Map<String, Any>>

}