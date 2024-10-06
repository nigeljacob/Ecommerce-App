package com.nigel.ecommerce.utils

import android.content.Context

object TokenManager {
    fun saveAccessToken(context: Context, token: String, email: String, password: String) {
        SharedPreferenceHelper.saveAccessToken(context, token, email, password)
    }

    fun getAccessToken(context: Context): String? {
        return SharedPreferenceHelper.getAccessToken(context)
    }

    fun clearTokens(context: Context) {
        SharedPreferenceHelper.clearTokens(context)
    }
}