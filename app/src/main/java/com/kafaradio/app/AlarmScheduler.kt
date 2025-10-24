package com.kafaradio.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object AlarmScheduler {

    fun scheduleWeekly(context: Context, id: Int, hour: Int, minute: Int, daysOfWeek: IntArray) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (d in daysOfWeek) {
            val cal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, d)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) add(Calendar.WEEK_OF_YEAR, 1)
            }
            val pi = PendingIntent.getBroadcast(context, id * 100 + d, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        }
    }

    fun cancelWeekly(context: Context, id: Int, daysOfWeek: IntArray) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (d in daysOfWeek) {
            val pi = PendingIntent.getBroadcast(context, id * 100 + d, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            am.cancel(pi)
        }
    }

    fun rescheduleAll(context: Context) {
        // Placeholder: load saved alarms and reschedule
        // AlarmStorage should hold saved alarms; here we re-schedule a single example alarm if exists
        val alarm = AlarmStorage.getSavedAlarm(context)
        alarm?.let {
            scheduleWeekly(context, it.id, it.hour, it.minute, it.daysOfWeek)
        }
    }
}
