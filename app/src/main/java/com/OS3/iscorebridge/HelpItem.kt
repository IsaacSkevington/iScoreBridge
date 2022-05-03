package com.OS3.iscorebridge

import android.content.res.Resources
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView


var GENERAL = null
var SETUP = HelpItem(
    arrayOf(
        HelpSection("Hosting a game", R.array.hostingagame, R.array.hostingagamenotes),
        HelpSection("Joining a game", R.array.joiningagame, R.array.joiningagamenotes),
        HelpSection("Spectating a game", R.array.spectating, R.array.spectatingnotes),
    )
)
var PLAYERINFO = null
var DURINGAGAME = null

val HELPINFOLIST = arrayOf<Pair<String, HelpItem>>(
    //Pair("General", GENERAL),
    Pair("Setup", SETUP),
    //Pair("In a Game", DURINGAGAME)
    //Pair("Players", PLAYERINFO),
)



class HelpSection(var title : String, var mainArrayID: Int, var notesArrayID: Int) {


    fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        var params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(left, top, right, bottom)
        view.layoutParams = params
    }


    private fun createTitle(layout: LinearLayout) {
        var titleView = TextView(layout.context)
        titleView.textSize = 30f
        titleView.text = title
        setMargins(titleView, 0, 20, 0, 20)
        layout.addView(titleView)
    }


    private fun createText(layout: LinearLayout, text: String) {
        var textView = TextView(layout.context)
        textView.text = text
        setMargins(textView, 0, 10, 0, 0)
        layout.addView(textView)
    }


    fun display(layout: LinearLayout, resources: Resources) {
        val mainArray: Array<String> = resources.getStringArray(mainArrayID)
        val notesArray: Array<String> = resources.getStringArray(notesArrayID)


        //Display title
        createTitle(layout)

        //Set up layout container for contents
        var contentsLayout = LinearLayout(layout.context).also {
            it.orientation = VERTICAL
        }

        //Add contents to layout
        mainArray.forEach {
            createText(contentsLayout, it)
        }

        //Add notes title
        createText(contentsLayout, "Notes: ")

        //Set up layout container for notes
        var notesLayout = LinearLayout(contentsLayout.context).also{
            it.orientation = VERTICAL
            setMargins(it, 10, 0, 10, 0)
        }


        //Add notes to layout
        notesArray.forEach {
            createText(notesLayout, it)
        }

        contentsLayout.addView(notesLayout)
        layout.addView(contentsLayout)
    }
}

class HelpItem (var sections : Array<HelpSection>){

    fun display(layout : LinearLayout, resources: Resources){
        sections.forEach {
            it.display(layout, resources)
        }
    }

}