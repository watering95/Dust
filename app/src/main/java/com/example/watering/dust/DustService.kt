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
    private var mCurrentTask = DustTask()

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

    inner class DustTask() : AsyncTask<JobParameters, Void, Bundle>() {
        var jobParameters: JobParameters? = null

        override fun doInBackground(vararg params: JobParameters?): Bundle?{
            jobParameters = params[0]

            val bundle = job()

            return bundle
        }
        override fun onPostExecute(result: Bundle?) {
            super.onPostExecute(result)
            sendData(result)
            this@DustService.jobFinished(jobParameters, true)
        }

        fun job(): Bundle {
            val url = URL("http://watering.iptime.org:3000/json/log_dust.json")
            val urlConnection = url.openConnection()

            val inputStream = getStringFromInputStream(BufferedInputStream(urlConnection.getInputStream()))
            val json:JSONObject = JSONObject(inputStream)

            val dust = json.getString("dust")
            val time = json.getString("time")

            val bundle = Bundle()

            bundle.putString("dust",dust)
            bundle.putString("time",time)

            if(dust.toFloat() > 100.0) this@DustService.showNotification(dust)

            return bundle
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
        fun sendData(bundle: Bundle?) {
            val intent = Intent()

            intent.setAction("ACTION_DUST").putExtra("dust", bundle)
            sendBroadcast(intent)
        }
    }
}

