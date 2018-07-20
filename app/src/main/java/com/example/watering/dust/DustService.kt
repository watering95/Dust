package com.example.watering.dust

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
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
        val channelId = "channel"
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)

            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
        val notificationIntent = Intent(applicationContext, DustActivity::class.java)

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val requestID = System.currentTimeMillis().toInt()

        val pendingInt = PendingIntent.getActivity(applicationContext, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentTitle("Dust")
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground))
                .setBadgeIconType(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingInt)

        notificationManager.notify(0, builder.build())
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

