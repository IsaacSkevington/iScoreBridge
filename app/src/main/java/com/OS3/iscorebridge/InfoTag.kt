package com.OS3.iscorebridge

import android.graphics.Color.WHITE
import android.widget.TextView

lateinit var infoTag : InfoTag
class InfoTag(var display : TextView, var mainActivity: MainActivity) {

    var onClick : () -> Unit = {}

    init{
        display.setTextColor(WHITE)
        display.setOnClickListener { onClick() }
        clear()
    }

    fun setOnClickListener(function : ()->Unit){
        onClick = function
    }

    fun setMessage(text : String){
        display.text = text
    }

    fun clear(){
        onClick = {}
        display.text = ""
    }

}