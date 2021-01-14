package com.example.coroutinessample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_coroutine_job.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class CoroutineJobActivity : AppCompatActivity() {
    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0;
    private val JOB_TIME = 4000
    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_job)
        job_button.setOnClickListener() {
            if (!::job.isInitialized) {
                initJob()
            }
            job_progress_bar.startJobOrCancel(job)
        }
    }


    fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            println("${job} is already active . cancelling")
            resetJob()
        } else {
            job_button.setText("cancel job")
            CoroutineScope(IO + job).launch {
                println("coroutine ${this} is activated with the job ${job}")
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i

                }
                UpdateJobCompleteText("job is complete")

            }
        }
    }

    private fun UpdateJobCompleteText(text: String) {
        GlobalScope.launch(Main) {
            job_complete_text.text = text
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting job"))
        }
        initJob()
    }

    fun initJob() {
        job_button.text = "Start Job"
        UpdateJobCompleteText("")
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank()) {
                    msg = "Unknown Cancellation Error"
                }
                println("${job} was cancelled. Reason ${msg}")
                showToast(msg)
            }
        }
        job_progress_bar.max = PROGRESS_MAX
        job_progress_bar.progress = PROGRESS_START

    }

    fun showToast(text: String) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@CoroutineJobActivity, text, Toast.LENGTH_LONG).show()

        }

    }
}