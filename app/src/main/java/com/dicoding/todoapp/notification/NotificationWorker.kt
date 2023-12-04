package com.dicoding.todoapp.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import com.dicoding.todoapp.ui.detail.DetailTaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.todoapp.utils.TASK_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val channelName = inputData.getString(NOTIFICATION_CHANNEL_ID)

    private fun getPendingIntent(task: Task): PendingIntent? {
        val intent = Intent(applicationContext, DetailTaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(
                    0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    override fun doWork(): Result {
        //TODO 14 : If notification preference on, get nearest active task from repository and show notification with pending intent
        val taskRepository = TaskRepository.getInstance(applicationContext)
        val nearestTask = taskRepository.getNearestActiveTask()
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val pendingIntent = getPendingIntent(nearestTask)

        val formattedDueDate = DateConverter.convertMillisToString(nearestTask.dueDateMillis)

        val notificationContent =
            applicationContext.getString(R.string.notify_content, formattedDueDate)

        val notification = channelName?.let {
            NotificationCompat.Builder(applicationContext, it).setContentTitle(nearestTask.title)
                .setContentText(notificationContent).setSmallIcon(R.drawable.ic_notifications)
                .setContentIntent(pendingIntent).setAutoCancel(true).build()
        }
        if (notification != null) {
            try {
                if (notificationManager.areNotificationsEnabled()) {
                    notificationManager.notify(nearestTask.id, notification)
                } else {
                    Toast.makeText(
                        applicationContext, "Notifications are disabled", Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: SecurityException) {
                Log.d("NotificationWorker", "Exception: ${e.message}")
            }
        }

        return Result.success()
    }
}
