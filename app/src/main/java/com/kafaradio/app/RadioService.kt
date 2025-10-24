package com.kafaradio.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer

class RadioService : Service() {

    private var player: SimpleExoPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        player = SimpleExoPlayer.Builder(this).build()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val url = intent?.getStringExtra(EXTRA_STREAM_URL) ?: DEFAULT_STREAM
        when (action) {
            ACTION_PLAY -> {
                val item = MediaItem.fromUri(url)
                player?.setMediaItem(item)
                player?.prepare()
                player?.play()
                startForeground(NOTIF_ID, buildNotification())
            }
            ACTION_STOP -> {
                player?.stop()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, NOTIF_CHANNEL)
            .setContentTitle("Kafa Radyo")
            .setContentText("Çalıyor")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pi)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val chan = NotificationChannel(NOTIF_CHANNEL, "Kafa Radyo", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(chan)
        }
    }

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_STREAM_URL = "EXTRA_STREAM_URL"
        const val NOTIF_CHANNEL = "kafaradio_channel"
        const val NOTIF_ID = 1234
        const val DEFAULT_STREAM = "https://your-radio-stream.example/stream"
    }
}
