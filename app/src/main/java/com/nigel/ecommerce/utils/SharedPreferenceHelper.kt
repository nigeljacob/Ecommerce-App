package com.nigel.ecommerce.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SharedPreferenceHelper {
    private const val PREFS_NAME = "EcommercePreference"
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val LOGGED_IN_EMAIL = "LoggedInEmail"
    private const val LOGGED_IN_PASSWORD = "LoggedInPassword"

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

    fun saveAccessToken(context: Context, token: String, email: String, password: String) {
        val prefs = getPrefs(context)
        prefs.edit().putString(ACCESS_TOKEN_KEY, token).apply()
        prefs.edit().putString(LOGGED_IN_EMAIL, email).apply()
        prefs.edit().putString(LOGGED_IN_PASSWORD, password).apply()
    }

    fun getAccessToken(context: Context): String? {
        return getPrefs(context).getString(ACCESS_TOKEN_KEY, null)
    }

    fun getCredentials(context: Context): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map.put("email", getPrefs(context).getString(LOGGED_IN_EMAIL, "") ?: "")
        map.put("password", getPrefs(context).getString(LOGGED_IN_PASSWORD, "") ?: "")
        return map
    }

    fun clearTokens(context: Context) {
       getPrefs(context).edit().remove(ACCESS_TOKEN_KEY).apply()
       getPrefs(context).edit().remove(LOGGED_IN_EMAIL).apply()
       getPrefs(context).edit().remove(LOGGED_IN_PASSWORD).apply()
    }
}