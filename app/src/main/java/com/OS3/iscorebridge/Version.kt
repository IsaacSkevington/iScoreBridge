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