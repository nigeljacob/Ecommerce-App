package com.nigel.ecommerce.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("/api/User/login")
    fun login(@Body requestBody: Map<String, String>): Call<Map<String, Any>>

    @GET("/api/Product/")
    fun getAllProducts(@Header("Authorization") token: String): Call<Map<String, Any>>

    @GET("/api/MasterData/GetCategories")
    fun getAllCategories(@Header("Authorization") token: String): Call<List<Map<String, Any>>>

    @GET("/api/User/token")
    fun getUserDetails(@Header("Authorization") token: String): Call<Map<String, Any>>

}