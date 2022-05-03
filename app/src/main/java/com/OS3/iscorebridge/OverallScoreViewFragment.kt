package com.OS3.iscorebridge

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView


class OverallScoreViewFragment : RefreshableFragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overall_score_view, container, false)
    }

    private fun makeTitle(text : String, view : View) : TextView {
        val title = TextView(view.context)
        title.text = text
        title.textSize = 20F
        title.setTypeface(null, Typeface.BOLD_ITALIC)
        title.setPadding(10,10,10,10)
        return title

    }

    private fun makeText(text : String, view : View) : TextView {
        val textView = TextView(view.context)
        textView.text = text
        textView.setPadding(10,10,10,10)
        return textView

    }

    private fun displayOverallScore(view : View){
        val scores = match.getScores(gameInfo.gameMode)
        val tableLayout = view.findViewById<TableLayout>(R.id.scoreDisplayTable)
        tableLayout.removeAllViews()
        tableLayout.isStretchAllColumns = true
        val titleRow = TableRow(view.context)
        titleRow.addView(makeTitle("Position", view))
        if(gameInfo.gameMode == GAMEMODE_PAIRS){
            titleRow.addView(makeTitle("Pair", view))
            titleRow.addView(makeTitle("Final Score (MPs)", view))
        }
        else{
            titleRow.addView(makeTitle("Team", view))
            titleRow.addView(makeTitle("Final Score (IMPs)", view))
        }
        tableLayout.addView(titleRow)
        val scoresImmut : MutableMap<Int, Int?> = scores
        val sortedScores = scoresImmut.toSortedMap()
        var i = 0
        for(pair in sortedScores.keys.reversed()){
            i++
            val row = TableRow(view.context)
            row.addView(makeText(i.toString(), view))
            row.addView(makeText(pair.toString(), view))
            row.addView(makeText(sortedScores[pair]!!.toString(), view))
            tableLayout.addView(row)
        }

    }

    override fun refresh(view : View) {
        displayOverallScore(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayOverallScore(view)
    }
}