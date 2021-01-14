package com.example.coroutinessample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"
    private val JOB_TIMEOUT=5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener() {

            //IO, Main, Default
            CoroutineScope(IO).launch {
                useAsync()
            }
        }
    }

    // easily access of result in async and await rather than normal coroutines..
    private suspend fun useAsync(){

        CoroutineScope(IO).launch { 
            
            val executionTime= measureTimeMillis {
                val result1: Deferred<String> = async {
                    println("debug launching job 1 ${Thread.currentThread().name}")
                    getResult1Api()
                }

                val result2: Deferred<String> = async {
                    println("debug launching job 1 ${Thread.currentThread().name}")
                    getResult2Api()
                }

                setTextInMainThread("Got ${result1.await()}")
                setTextInMainThread("Got ${result2.await()}")
            }
            println("debug total time ${executionTime} ")
        }

    }


    // example of parallel background work in coroutines withiut async/await ...
    private suspend fun getNetworkTimeOuts(){
        withContext(IO){
            /*val job= launch{
             val result= getResult1Api()
             println("debug ${result}")

            }*/

            val job= withTimeoutOrNull(JOB_TIMEOUT){
                val result1= getResult1Api()
                setTextInMainThread("Got $result1")

                val result2= getResult2Api()
                setTextInMainThread("Got $result2")
            }

            if(job==null){
                val cancelMessage="Cancelling job .. take longer than ${JOB_TIMEOUT}"
                println("${cancelMessage}")
                setTextInMainThread(cancelMessage)

            }
        }

    }

    private suspend fun getDataFromResult() {
        val result = getResult1Api()
        println("debug  ${result}")
        setTextInMainThread(result)
        val result2= getResult2Api()
        setTextInMainThread(result2)
    }

    private suspend fun setTextInMainThread(input: String) {
        // by using with context u can switch the thread in coroutines ie Main to IO or default
        withContext(Main) {
            text.text=input
            println("debug  : ${Thread.currentThread().name}")
        }


    }
    private suspend fun getResult1Api(): String {
        logThread("getResultFromApi")
        delay(1000)
        Thread.sleep(1000)
        return RESULT_1
    }

    private suspend fun getResult2Api():String{

        logThread("getResult2Api")
        delay(1000)
        Thread.sleep(1000)
        return RESULT_2
    }

    private fun logThread(methodName: String) {

        println("debug : ${methodName} : ${Thread.currentThread().name}")

    }
}