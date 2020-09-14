package com.wlp.utubed.util

import android.widget.ProgressBar

class ThreadProgressBar(var prBar: ProgressBar)
{
    val process = Thread(InnerRunnable())
    var finish = false

    inner class InnerRunnable() : Runnable {
        override fun run() {

            var i = 0
            var max = 100

            while (i < 100) {

                prBar.progress = i

                    if (finish)
                    {
                        prBar.max = i
                        i = 100
                    }




                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                    }

                    i++
            }
        }
    }

    fun loading(){
        process.start()
    }

    fun stop(){
        val process = Thread(InnerRunnable())
        process.stop()
    }


}