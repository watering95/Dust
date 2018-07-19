package com.example.watering.dust

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.widget.TextView

class DustActivity : AppCompatActivity() {

    private val JOB_ID = 1
    var dust = ""
    var time = ""
    var textViewDustMg3: TextView? = null
    var textViewDustTime: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dust)
        initJobService()
        initLayout()
    }

    fun initJobService() {
        val jobInfo: JobInfo = JobInfo.Builder(JOB_ID, ComponentName(this, DustService::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(1000*1)
                .build()

        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.schedule(jobInfo)
    }

    fun initLayout() {
        textViewDustMg3 = findViewById(R.id.dust_mg3)
        textViewDustTime = findViewById(R.id.dust_time)

        val br = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                updateData(intent)
            }
        }

        this.registerReceiver(br, IntentFilter("ACTION_DUST"))
    }

    fun updateData(intent: Intent?) {
        val bundle = intent?.getBundleExtra("dust")
        if (bundle != null) {
            dust = bundle.getString("dust")
            time = bundle.getString("time")

            textViewDustMg3?.setText(dust)
            textViewDustTime?.setText(time)
        }
    }
}


