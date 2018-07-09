package com.example.watering.dust

import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import org.json.JSONObject
import java.io.*
import java.net.URL

class DustService : JobService() {
    val ACTION_DUST_DATA = "com.example.watering.dust.DATA"

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

            val url = URL("http://watering.iptime.org:3000/json/log_dust.json")
            val urlConnection = url.openConnection()

            val inputStream = getStringFromInputStream(BufferedInputStream(urlConnection.getInputStream()))
            val json:JSONObject = JSONObject(inputStream)

            val dust = json.getString("dust")
            val time = json.getString("time")

            val bundle = Bundle()

            bundle.putString("dust",dust)
            bundle.putString("time",time)

            broadcastData(jobService.ACTION_DUST_DATA, bundle)

            if(dust.toFloat() > 100.0) jobService.showNotification(dust)
            jobService.jobFinished(jobParameters, true)

            return null
        }

        fun getStringFromInputStream(inputStream: InputStream): String {
            val sb = StringBuilder()

            try {
                val br = BufferedReader(InputStreamReader(inputStream))
                for(line in br.readLine()) {
                    sb.append(line)
                }
                br.close()
            }
            catch (e: IOException) {
                e.printStackTrace()
            }

            return sb.toString()
        }

        fun broadcastData(action:String, data: Bundle) {
            val DUST = "DUST_DATA"
            val intent = Intent(action)

            intent.putExtra(DUST, data)
            jobService.sendBroadcast(intent)
        }
    }
}

