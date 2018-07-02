package com.example.watering.dust

import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

class DustService : JobService() {
    private var mCurrentTask = DustTask(this)

    override fun onStopJob(params: JobParameters?): Boolean {
        mCurrentTask.cancel(true)

        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        mCurrentTask.execute(params)

        return true
    }

    fun showNotification(text:String) {
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, DustActivity::class.java), 0)

        val notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker(text)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build()

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, notification)
    }

    class DustTask(private val jobService: DustService) : AsyncTask<JobParameters, Void, Void>() {

        override fun doInBackground(vararg params: JobParameters?): Void? {
            val jobParameters = params[0]

            jobService.showNotification("Hahaha")
            jobService.jobFinished(jobParameters, true)

            return null
        }
    }
}

