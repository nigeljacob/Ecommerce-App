package com.nigel.ecommerce.repository

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Email
import com.nigel.ecommerce.models.ApiResponse
import com.nigel.ecommerce.models.TokenResponse
import com.nigel.ecommerce.models.User
import com.nigel.ecommerce.services.ApiService
import com.nigel.ecommerce.utils.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepository(private val context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.9:5077")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    suspend fun login(email: String, password: String, onResponse: (success: Boolean, message: String) -> Unit) {
        withContext(Dispatchers.IO) {
            val requestBody = mapOf(
                "email" to email,
                "password" to password
            )
            val response = apiService.login(requestBody).execute()
            val message = response.body()?.get("message") as String?

            if(response.isSuccessful) {
                if(message.equals("Login successful")) {
                    val token = response.body()?.get("data") as Map<String, Any>
                    SharedPreferenceHelper.saveAccessToken(context, token.get("token") as String)
                    println(token.get("token"))
                    onResponse(true, message ?: "Success")
                }
            } else {
                if(response.code() == 401) {
                    onResponse(false, "Invalid password")
                } else if(response.code() == 400) {
                    onResponse(false, "User not found")
                } else {
                    onResponse(false, "Unknown error occurred")
                }
            }
        }
    }

    suspend fun getUserDetails(): User? {

        val token = "Bearer " + SharedPreferenceHelper.getAccessToken(context)
        val response = apiService.getUserDetails(token).execute()
        val message = response.body()?.get("message") as String?

        if(response.isSuccessful) {
            if(message.equals("Successful")) {
                val tempUser = response.body()?.get("data") as Map<String, Any>
                val newUser = User(
                    tempUser.get("id") as String?,
                    tempUser.get("name") as String?,
                    tempUser.get("email") as String?,
                    tempUser.get("role") as String?,
                    tempUser.get("status") as String?,
                    tempUser.get("active") as Boolean,
                )
                return newUser
            }
        }
        return null
    }


    fun signUp(name: String, email: String, password: String) {

    }

    fun checkLogin(): Boolean {
        val accessToken = SharedPreferenceHelper.getAccessToken(context)
        if(accessToken == null) {
            return false
        }
        return true
    }

    fun logout() {

    }

}