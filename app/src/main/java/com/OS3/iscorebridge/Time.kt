package com.OS3.iscorebridge

import kotlin.math.floor
import kotlin.math.roundToInt

class Time(var seconds : Int) {

    constructor(hours : Int, minutes : Int, seconds: Int) :
            this(hours * 60 * 60 + minutes * 60 + seconds)

    constructor(s : String) : this(s.toInt())
    constructor(other : Time):this(other.seconds)
    fun az(i : Int) : String{
        var iString = i.toString()
        if(iString.length == 1){
            iString = "0$iString"
        }
        return iString
    }

    fun decrement() : Boolean{
        if(!isZero()) {
            seconds--
            return true
        }
        return false
    }

    fun isZero() : Boolean{
        return seconds == 0
    }

    override fun toString(): String {
        return seconds.toString()
    }
    fun toDisplayString(): String {
        var currentSeconds = seconds
        var hours = floor(currentSeconds/(60f * 60f)).roundToInt()
        currentSeconds -= hours * 60 * 60
        var minutes = floor(currentSeconds/60f).roundToInt()
        currentSeconds -= minutes * 60
        return "${az(hours)}:${az(minutes)}:${az(currentSeconds)}"

    }

}