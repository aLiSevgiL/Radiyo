package com.kafaradio.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private var player: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView

    private val requestPerms = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
        // noop, just request
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.playerView)
        val editStream = findViewById<EditText>(R.id.editStream)
        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnAlarm = findViewById<Button>(R.id.btnAlarm)

        btnPlay.setOnClickListener {
            val url = editStream.text.toString().ifEmpty { DEFAULT_STREAM }
            startService(Intent(this, RadioService::class.java).apply {
                putExtra(RadioService.EXTRA_STREAM_URL, url)
                action = RadioService.ACTION_PLAY
            })
        }
        findViewById<Button>(R.id.btnStop).setOnClickListener {
            startService(Intent(this, RadioService::class.java).apply { action = RadioService.ACTION_STOP })
        }

        btnAlarm.setOnClickListener {
            startActivity(Intent(this, AlarmEditActivity::class.java))
        }

        requestPermissionsIfNeeded()
    }

    private fun requestPermissionsIfNeeded() {
        val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        val needed = perms.filter { ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (needed.isNotEmpty()) requestPerms.launch(needed.toTypedArray())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val DEFAULT_STREAM = "https://your-radio-stream.example/stream"
    }
}
