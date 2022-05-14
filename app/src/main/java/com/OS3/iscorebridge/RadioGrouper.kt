package com.OS3.iscorebridge

import android.content.Context
import android.widget.RadioButton

class RadioGrouper{

    var buttons : ArrayList<RadioButton> = ArrayList()


    fun addButton(context: Context, onClick : (Int)->Unit) : RadioButton{
        var button = RadioButton(context)
        var bNum = buttons.size
        button.setOnClickListener {
            onClick(bNum)
            onPress(bNum)
        }
        buttons.add(button)
        return button
    }

    fun onPress(buttonNumber : Int){
        uncheckAll()
        check(buttonNumber)
    }

    fun getSelected() : Int{
        for(i in 0 until buttons.size){
            if(buttons[i].isChecked){
                return i
            }
        }
        return -1
    }

    fun check(buttonNumber : Int){
        buttons[buttonNumber].isChecked = true
    }

    fun uncheckAll(){
        buttons.forEach {
            it.isChecked = false
        }
    }

}