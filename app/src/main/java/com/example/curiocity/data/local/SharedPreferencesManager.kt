package com.example.curiocity.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveUserUUID(uuid: String) {
        sharedPreferences.edit().putString(KEY_USER_UUID, uuid).apply()
    }

    fun getUserUUID(): String? {
        return sharedPreferences.getString(KEY_USER_UUID, null)
    }

    companion object {
        private const val PREFS_NAME = "curiocity_prefs"
        private const val KEY_USER_UUID = "user_uuid"
    }
} 