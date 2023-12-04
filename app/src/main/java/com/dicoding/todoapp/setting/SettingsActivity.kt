package com.dicoding.todoapp.setting

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showToast("Notifications permission granted")
        } else {
            showToast("Notifications will not show without permission")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification =
                findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { preference, newValue ->
                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName

                if (newValue as Boolean) {
                    scheduleDailyReminder()
                } else {
                    cancelDailyReminder()
                }

                true
            }
        }

        private fun cancelDailyReminder() {
            WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminder")
        }

        private fun scheduleDailyReminder() {
            val channelName = getString(R.string.notify_channel_name)

            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                1, TimeUnit.MILLISECONDS
            ).setInputData(workDataOf(NOTIFICATION_CHANNEL_ID to channelName)).build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                "DailyReminder", ExistingPeriodicWorkPolicy.UPDATE, workRequest
            )
        }
    }
}