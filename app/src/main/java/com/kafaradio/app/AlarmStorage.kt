package com.kafaradio.app

import android.content.Context
import org.json.JSONObject

data class SavedAlarm(val id:Int, val hour:Int, val minute:Int, val daysOfWeek:IntArray)

object AlarmStorage {
    private const val PREF = "kafaradio_prefs"
    private const val KEY = "saved_alarm"

    fun saveAlarm(context: Context, alarm: SavedAlarm) {
        val jo = JSONObject()
        jo.put("id", alarm.id)
        jo.put("hour", alarm.hour)
        jo.put("minute", alarm.minute)
        jo.put("days", alarm.daysOfWeek.joinToString(","))
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, jo.toString()).apply()
    }

    fun getSavedAlarm(context: Context): SavedAlarm? {
        val s = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, null) ?: return null
        val jo = JSONObject(s)
        val id = jo.getInt("id")
        val hour = jo.getInt("hour")
        val minute = jo.getInt("minute")
        val days = jo.getString("days").split(",").filter{it.isNotEmpty()}.map{it.toInt()}.toIntArray()
        return SavedAlarm(id, hour, minute, days)
    }

    // Home location & options
    fun setHome(context: Context, lat: Double, lon: Double, radius: Float) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putFloat("home_lat", lat.toFloat())
            .putFloat("home_lon", lon.toFloat())
            .putFloat("home_radius", radius)
            .apply()
    }
    fun getHomeLat(context: Context): Double? {
        val v = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getFloat("home_lat", Float.MIN_VALUE)
        return if (v==Float.MIN_VALUE) null else v.toDouble()
    }
    fun getHomeLon(context: Context): Double? {
        val v = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getFloat("home_lon", Float.MIN_VALUE)
        return if (v==Float.MIN_VALUE) null else v.toDouble()
    }
    fun getHomeRadius(context: Context): Float {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getFloat("home_radius", 200f)
    }

    fun setRingOnlyAtHome(context: Context, yes:Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putBoolean("ring_home", yes).apply()
    }
    fun getRingOnlyAtHome(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getBoolean("ring_home", false)
    }

    fun setStreamUrl(context: Context, url:String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString("stream", url).apply()
    }
    fun getStreamUrl(context: Context): String? {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString("stream", RadioService.DEFAULT_STREAM)
    }
}
