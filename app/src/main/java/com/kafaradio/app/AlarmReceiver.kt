package com.kafaradio.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val homeLat = AlarmStorage.getHomeLat(context)
        val homeLon = AlarmStorage.getHomeLon(context)
        val ringOnlyAtHome = AlarmStorage.getRingOnlyAtHome(context)
        val streamUrl = AlarmStorage.getStreamUrl(context)
        val shouldRing = if (ringOnlyAtHome && homeLat != null && homeLon != null) {
            // simple last-known-location check (best-effort)
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = lm.getProviders(true)
            var last: Location? = null
            for (p in providers) {
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // permission not available -> avoid ringing
                        last = null
                        break
                    }
                    val l = lm.getLastKnownLocation(p)
                    if (l != null && (last == null || l.time > last.time)) last = l
                } catch (e: Exception) { }
            }
            if (last == null) false
            else {
                val dist = FloatArray(1)
                Location.distanceBetween(last.latitude, last.longitude, homeLat, homeLon, dist)
                dist[0] <= AlarmStorage.getHomeRadius(context)
            }
        } else {
            true
        }

        if (shouldRing) {
            val svc = Intent(context, RadioService::class.java).apply {
                action = RadioService.ACTION_PLAY
                putExtra(RadioService.EXTRA_STREAM_URL, streamUrl ?: RadioService.DEFAULT_STREAM)
            }
            context.startForegroundService(svc)
        }
    }
}
