package com.OS3.iscorebridge

import android.widget.TextView

class Timer(@Volatile var time : Time, var onFinish: ()->Unit) : Thread(){

    @Volatile var displayTextView : TextView? = null
    @Volatile var finished = false
    @Volatile var initTime = Time(time)

    fun displayIn(dtv : TextView){
        this.displayTextView = dtv
        this.displayTextView!!.text = time.toDisplayString()
    }

    fun reset() : Timer{
        return Timer(initTime, onFinish)
    }

    override fun run() {
        while(time.decrement()){
            if(displayTextView != null){
                displayTextView!!.text = time.toDisplayString()
            }
        }
        finished = true
        onFinish()
    }

}