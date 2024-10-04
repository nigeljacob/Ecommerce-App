package com.nigel.ecommerce.utils

import android.content.Context

object TokenManager {
    fun saveAccessToken(context: Context, token: String) {
        SharedPreferenceHelper.saveAccessToken(context, token)
    }

    fun getAccessToken(context: Context): String? {
        return SharedPreferenceHelper.getAccessToken(context)
    }

    fun clearTokens(context: Context) {
        SharedPreferenceHelper.clearTokens(context)
    }
}