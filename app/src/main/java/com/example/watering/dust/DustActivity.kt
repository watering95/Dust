package com.example.watering.dust

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class DustActivity : AppCompatActivity() {

    private var JOB_ID = 1

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
    }
}
