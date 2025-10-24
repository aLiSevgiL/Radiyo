package com.kafaradio.app

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AlarmEditActivity : AppCompatActivity() {

    private var hour = 8
    private var minute = 0
    private val selectedDays = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_edit)

        val tvTime = findViewById<TextView>(R.id.tvTime)
        val btnPick = findViewById<Button>(R.id.btnPickTime)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val chkHomeOnly = findViewById<CheckBox>(R.id.chkHomeOnly)
        val btnSetHome = findViewById<Button>(R.id.btnSetHome)
        val editStream = findViewById<EditText>(R.id.editStream)

        btnPick.setOnClickListener {
            val tp = TimePickerDialog(this, { _, h, m ->
                hour = h
                minute = m
                tvTime.text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            }, hour, minute, true)
            tp.show()
        }

        // Days checkboxes
        val days = mapOf(
            R.id.chkMon to Calendar.MONDAY,
            R.id.chkTue to Calendar.TUESDAY,
            R.id.chkWed to Calendar.WEDNESDAY,
            R.id.chkThu to Calendar.THURSDAY,
            R.id.chkFri to Calendar.FRIDAY,
            R.id.chkSat to Calendar.SATURDAY,
            R.id.chkSun to Calendar.SUNDAY,
        )
        for ((id, dow) in days) {
            findViewById<CheckBox>(id).setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedDays.add(dow) else selectedDays.remove(dow)
            }
        }

        btnSetHome.setOnClickListener {
            // For simplicity: set home to last known location if available (incomplete - user must grant location)
            // In production use a proper UI to pick home location or geofence.
            Toast.makeText(this, "Ev konumu manuel olarak ya da haritadan seçilecek şekilde geliştirilmeli.", Toast.LENGTH_LONG).show()
        }

        btnSave.setOnClickListener {
            if (selectedDays.isEmpty()) {
                Toast.makeText(this, "Gün seçin", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            val daysArray = selectedDays.toIntArray()
            val alarm = SavedAlarm(1, hour, minute, daysArray)
            AlarmStorage.saveAlarm(this, alarm)
            AlarmStorage.setRingOnlyAtHome(this, chkHomeOnly.isChecked)
            AlarmStorage.setStreamUrl(this, editStream.text.toString().ifEmpty { RadioService.DEFAULT_STREAM })
            AlarmScheduler.scheduleWeekly(this, alarm.id, hour, minute, daysArray)
            Toast.makeText(this, "Alarm kaydedildi", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
