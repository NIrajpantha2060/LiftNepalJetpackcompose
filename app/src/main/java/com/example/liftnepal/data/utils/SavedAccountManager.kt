package com.example.liftnepal.data.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

data class SavedCredentials(
    val email: String,
    val password: String
)

object SavedAccountManager {

    private const val PREFS_FILE   = "saved_account_prefs"
    private const val MAX_ACCOUNTS = 2

    private fun getPrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /** Save or update credentials. If email already exists, update its password.
     *  If new, fill next empty slot. If both full, replace slot 0. */
    fun save(context: Context, email: String, password: String) {
        val prefs    = getPrefs(context)
        val existing = loadAll(context)

        val slot = when {
            existing.indexOfFirst { it.email == email } >= 0 ->
                existing.indexOfFirst { it.email == email }
            existing.size < MAX_ACCOUNTS -> existing.size
            else -> 0
        }

        prefs.edit()
            .putString("email_$slot", email)
            .putString("pass_$slot",  password)
            .apply()
    }

    /** Load all saved accounts (up to 2) */
    fun loadAll(context: Context): List<SavedCredentials> {
        val prefs  = getPrefs(context)
        val result = mutableListOf<SavedCredentials>()
        for (i in 0 until MAX_ACCOUNTS) {
            val email = prefs.getString("email_$i", null) ?: continue
            val pass  = prefs.getString("pass_$i",  null) ?: continue
            result.add(SavedCredentials(email, pass))
        }
        return result
    }

    /** Remove one account by email */
    fun remove(context: Context, email: String) {
        val prefs = getPrefs(context)
        for (i in 0 until MAX_ACCOUNTS) {
            if (prefs.getString("email_$i", null) == email) {
                prefs.edit().remove("email_$i").remove("pass_$i").apply()
                break
            }
        }
    }

    /** Clear all saved accounts */
    fun clearAll(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}