package com.nigel.ecommerce.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SharedPreferenceHelper {
    private const val PREFS_NAME = "EcommercePreference"
    private const val ACCESS_TOKEN_KEY = "access_token"

    private fun getPrefs(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveAccessToken(context: Context, token: String) {
        val prefs = getPrefs(context)
        prefs.edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }

    fun getAccessToken(context: Context): String? {
        return getPrefs(context).getString(ACCESS_TOKEN_KEY, null)
    }

    fun clearTokens(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().clear().apply()
    }
}