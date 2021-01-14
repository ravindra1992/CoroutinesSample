package com.example.coroutinessample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_parallel_background_task.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class ParallelBackgroundTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parallel_background_task)

        button.setOnClickListener(){
            setNewText("Clicked!")
            getSequentialReq()

        }
    }


    private  fun getSequentialReq(){
        CoroutineScope(IO).launch {

            val exeTime= measureTimeMillis {

                val result1= async {
                    println("debug launching thread ${Thread.currentThread().name}")
                    getResultFrom1()
                }.await()

                val result2= async {
                    println("debug launching thread ${Thread.currentThread().name}")
                    getResultFrom2(result1)
                }.await()
                println("debug got result 2 ${result2}")

            }
            println("debug total time ${exeTime}")

        }

    }

    private fun fakeRequest() {
        CoroutineScope(IO).launch {
            val job1= launch {
                val time1= measureTimeMillis {
                    println("launching job1 thread ${Thread.currentThread().name} ")
                    val result1= getResultFrom1()
                    setTextOnMainThread("Got $result1")
                }
                println("completed job1 in $time1 ms")
            }

            job1.join() // if want to do operation one after another then use it .
            val job2= launch {
                val time2 = measureTimeMillis {
                    println("launching job2 thread ${Thread.currentThread().name} ")
                    val result2= getResultFrom2("")
                    setTextOnMainThread("Got $result2")
                }
                println("completed job2 in $time2 ms")
            }
        }


    }

    private fun setNewText(input:String){
        val newText= text.text.toString() + "\n$input"
        text.text = newText
     }

    private suspend fun setTextOnMainThread(input:String){
        withContext(Main){
            setNewText(input)
        }
    }
    private suspend fun getResultFrom1():String{
        delay(1000)
        return "Result #1"
    }

    private suspend fun getResultFrom2(result1:String):String{
        delay(1000)
        if(result1.equals("Result #1")){
            return "Result #2"
        }
      throw CancellationException("Result 1 is incoreect")
    }

}