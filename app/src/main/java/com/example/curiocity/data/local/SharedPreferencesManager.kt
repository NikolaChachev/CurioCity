package com.example.curiocity.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.curiocity.presentation.MainViewModel
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

    fun saveLastAppCloseTimestamp(timestamp: Long) {
        sharedPreferences.edit().putLong(KEY_LAST_APP_CLOSE, timestamp).apply()
    }

    fun getLastAppCloseTimestamp(): Long {
        return sharedPreferences.getLong(KEY_LAST_APP_CLOSE, -1L)
    }

    fun saveTimeUntilNextLife(timeLeft: Long) {
        sharedPreferences.edit().putLong(KEY_TIME_UNTIL_NEXT_LIFE_REMAINING, timeLeft).apply()
    }


    fun getTimeUntilNextLife() = sharedPreferences.getLong(
        KEY_TIME_UNTIL_NEXT_LIFE_REMAINING,
        MainViewModel.LIFE_REGEN_INTERVAL
    )

    companion object {
        private const val PREFS_NAME = "curiocity_prefs"
        private const val KEY_USER_UUID = "user_uuid"
        private const val KEY_LAST_APP_CLOSE = "last_app_close"
        private const val KEY_TIME_UNTIL_NEXT_LIFE_REMAINING = "time_until_life"
    }
} 