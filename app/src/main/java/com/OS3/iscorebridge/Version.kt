package com.OS3.iscorebridge

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView


val VERSIONS = arrayOf(
    Version(
        "0.0.1",
        arrayOf(
            Feature("Movements", "Added the ability to select a specific movement"),
            Feature("Menu", "Added menu bar"),
            Feature("Player recognition", "Added mapping of player numbers to names")
        ),
        arrayOf(
            Bug("Incorrect error message", "Some input boxes had incorrect error messages")
        )
    ),

    Version(
        "0.2.1",
        arrayOf(
            Feature("Hands", "Added the ability to add specific hands to a game"),
            Feature("Bidding", "Added the ability to add bidding to a game"),
            Feature("Starring", "You can now star games"),
            Feature("Stats view", "View stats at the end of the match"),
            Feature("Automatic player recognition", "Club numbers are mapped to data on the members")
        ),
        arrayOf()

    ),
    Version(
        "0.3.1",
        arrayOf(
            Feature("Settings", "Adding settings menu"),
            Feature("Help", "Added help menu and manual"),
            Feature("Deal imports", "Added the ability to import deals from auto-deal machine"),
            Feature("Director Menu", "Added menu for director to change board details"),
            Feature("Round timer", "Added timer for rounds which shows up on each device"),
            Feature("UI Look", "Changed the UI look and feel")
        ),
        arrayOf()
    )

)

open class Addition(val title : String, val contents: String){
    fun display(context: Context) : View{
        var text = TextView(context)
        text.text = "$title:\n$contents\n\n"
        return text
    }
}

class Feature(title : String, contents: String) : Addition(title, contents)
class Bug(title : String, contents: String) : Addition(title, contents)

class Version(val number: String, val features: Array<Feature>, val bugs: Array<Bug>) {

    fun display(context : Context, layout : LinearLayout){

        var title = layout.findViewById<TextView>(R.id.versionTitle)
        title.text = number


        var featuresLayout = layout.findViewById<LinearLayout>(R.id.featuresLayout)
        features.forEach {
            featuresLayout.addView(it.display(context))
        }

        var bugsLayout = layout.findViewById<LinearLayout>(R.id.bugsLayout)
        bugs.forEach {
            bugsLayout.addView(it.display(context))
        }
    }


}