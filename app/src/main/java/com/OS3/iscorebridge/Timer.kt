package com.OS3.iscorebridge

import android.os.Handler
import android.widget.TextView

class Timer(@Volatile var time : Time, var timerHandler : Handler) : Thread(){

    @Volatile var finished = false
    @Volatile var initTime = Time(time)
    @Volatile var displayThread : DisplayThread? = null

    fun show(dtv : TextView){
        DisplayThread(dtv).also{
            displayThread = it
            it.start()
        }
    }
    fun hide(){
        displayThread?.kill()
    }

    fun restart(){
        time = Time(initTime)
    }

    fun reset() : Timer{
        return Timer(initTime, timerHandler)
    }
    inner class DisplayThread(var displayTextView : TextView) : Thread(){

        @Volatile var killFlag = false
        override fun run() {
            while(!killFlag) {
                if(this.displayTextView!!.text != time.toDisplayString()){
                    this.displayTextView!!.text = time.toDisplayString()
                }
            }
        }

        fun kill(){
            killFlag = true
        }
    }

    override fun run() {
        while(time.decrement()){
            sleep(1000)
        }
        finished = true
        timerHandler.obtainMessage(MESSAGE_TIMER_FINISHED).sendToTarget()
    }

}