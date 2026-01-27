package com.hena.thoughts

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("hena_settings", Context.MODE_PRIVATE)

    var isNotificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var intervalHours: Int
        get() = prefs.getInt(KEY_INTERVAL, 3)
        set(value) = prefs.edit().putInt(KEY_INTERVAL, value).apply()

    var activeStartHour: Int
        get() = prefs.getInt(KEY_START_HOUR, 8)
        set(value) = prefs.edit().putInt(KEY_START_HOUR, value).apply()

    var activeEndHour: Int
        get() = prefs.getInt(KEY_END_HOUR, 21)
        set(value) = prefs.edit().putInt(KEY_END_HOUR, value).apply()

    companion object {
        private const val KEY_ENABLED = "notification_enabled"
        private const val KEY_INTERVAL = "interval_hours"
        private const val KEY_START_HOUR = "active_start_hour"
        private const val KEY_END_HOUR = "active_end_hour"
    }
}
