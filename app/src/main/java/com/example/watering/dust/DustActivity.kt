package com.example.watering.dust

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.widget.TextView

class DustActivity : AppCompatActivity() {

    private var JOB_ID = 1
    var dust = ""
    var time = ""
    var textViewDustMg3: TextView? = null
    var textViewDustTime: TextView? = null
    val ACTION_DUST_DATA = "com.example.watering.dust.DATA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dust)

        val jobInfo: JobInfo = JobInfo.Builder(JOB_ID, ComponentName(this, DustService::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(1000*1)
                .setPersisted(true)
                .build()

        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.schedule(jobInfo)

        registerReceiver(mReceiver, IntentFilter(ACTION_DUST_DATA))

        textViewDustMg3 = findViewById(R.id.dust_mg3)
        textViewDustTime = findViewById(R.id.dust_time)
    }

    val mReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_DUST_DATA -> {
                    val bundle = intent.getBundleExtra("DUST_DATA")
                    val dust = bundle.getString("dust")
                    val time = bundle.getString("time")

                    textViewDustMg3?.setText(dust)
                    textViewDustTime?.setText(time)
                }
            }
        }
    }
}


