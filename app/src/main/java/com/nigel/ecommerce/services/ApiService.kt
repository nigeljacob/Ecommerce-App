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
import retrofit2.http.Query

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

    @GET("/api/Feedback/customer/{productID}")
    fun getCustomerFeedback(@Header("Authorization") token: String, @Path("productID") productID: String): Call<Map<String, Any>>

    @GET("/api/Feedback/product/{productID}")
    fun getProductReviews(@Header("Authorization") token: String, @Path("productID") productID: String): Call<Map<String, Any>>

    @GET("/api/Feedback/average/{productID}")
    fun getAverageReview(@Header("Authorization") token: String, @Path("productID") productID: String): Call<Map<String, Any>>

    @POST("/api/Feedback/create")
    @JvmSuppressWildcards
    fun addReview(@Header("Authorization") token: String, @Body requestBody: Map<String, Any>): Call<Map<String, Any>>

    @POST("/api/Order/create")
    @JvmSuppressWildcards
    fun createOrder(@Header("Authorization") token: String, @Body requestBody: Map<String, Any>): Call<Map<String, Any>>

    @GET("/api/User/deactivate/{id}")
    fun deactivateAccount(@Header("Authorization") token: String, @Path("id") id: String): Call<Map<String, Any>>

    @PUT("/api/Feedback/update/{id}")
    @JvmSuppressWildcards
    fun updateReview(@Header("Authorization") token: String, @Body requestBody: Map<String, Any>, @Path("id") reviewId: String): Call<Map<String, Any>>

    @PUT("/api/Order/{id}")
    @JvmSuppressWildcards
    fun updateOrder(@Header("Authorization") token: String, @Body requestBody: Map<String, Any>, @Path("id") id: String): Call<Map<String, Any>>

    @PUT("/api/Order/request/{id}")
    @JvmSuppressWildcards
    fun cancelOrder(@Header("Authorization") token: String, @Path("id") id: String, @Query("id") productID: String): Call<Map<String, Any>>

    @PUT("/api/User/{id}")
    fun updateUser(@Header("Authorization") token: String, @Path("id") id: String, @Body requestBody: Map<String, String>): Call<Map<String, Any>>

}