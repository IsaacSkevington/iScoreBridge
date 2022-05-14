package com.OS3.iscorebridge

import android.content.Context
import java.io.File
import java.io.FileNotFoundException
import java.util.*

val SETTINGS = Settings("settings.dat")
class Settings(val fileName : String) {
    val dlm = "(*)"
    var userPin : Int = -1



    fun generatePin(digits : Int) : Int{
        var r = Random()
        var stringOut = ""
        for(i in 0 until digits){
            stringOut += r.nextInt(10)
        }
        return stringOut.toInt()
    }

    fun getPin() : Int{
        return if(userPin == -1){
            generatePin(4)
        }
        else{
            userPin
        }
    }

    fun pinSet() : Boolean{
        return userPin != -1
    }

    fun save(context : Context){
        File(context.filesDir, fileName).delete()
        var file = File(context.filesDir, fileName)
        file.writeText(toString())
    }
    fun load(context : Context) : Boolean{
        return try {
            fromString(File(context.filesDir, fileName).readLines()[0])
            true
        } catch(e : FileNotFoundException){
            false
        }
    }

    fun fromString(s : String){
        var params = s.split(dlm)
        userPin = params[0].toInt()
    }

    override fun toString(): String {
        return userPin.toString()
    }
}