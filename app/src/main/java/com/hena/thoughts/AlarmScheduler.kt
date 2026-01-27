package com.hena.thoughts

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule() {
        val settings = SettingsManager(context)
        if (!settings.isNotificationEnabled) return

        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = nextTriggerTime(settings)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent
        )
    }

    fun cancel() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun nextTriggerTime(settings: SettingsManager): Long {
        val now = Calendar.getInstance()
        val trigger = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, settings.intervalHours)
        }

        val hour = trigger.get(Calendar.HOUR_OF_DAY)
        if (hour < settings.activeStartHour || hour >= settings.activeEndHour) {
            trigger.apply {
                if (hour >= settings.activeEndHour) add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, settings.activeStartHour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        }

        return trigger.timeInMillis
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}
