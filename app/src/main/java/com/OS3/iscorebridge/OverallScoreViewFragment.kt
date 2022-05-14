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

    private fun displayScoreTwoWinner(view : View, tableLayout: TableLayout, scores : MutableMap<Int, Int?>){

        var scoresNS = HashMap<Int, Int?>()
        var scoresEW = HashMap<Int, Int?>()

        var pairs = gameInfo.movement.splitPairs()
        var pairsNS = pairs.first
        var pairsEW = pairs.second
        scores.forEach {
            if(PlayerPair(it.key) in pairsNS){
                scoresNS[it.key] = it.value
            }
            else if(PlayerPair(it.key) in pairsEW){
                scoresEW[it.key] = it.value
            }
        }

        tableLayout.addView(TableRow(view.context).also{
            it.addView(makeTitle("North/South", view))
        })
        displayScoreOneWinner(view, tableLayout, scoresNS)

        tableLayout.addView(TableRow(view.context).also{
            it.addView(makeTitle("East/West", view))
        })
        displayScoreOneWinner(view, tableLayout, scoresEW)
    }

    private fun displayScoreOneWinner(view : View, tableLayout: TableLayout, scores : MutableMap<Int, Int?>){
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
        val scoresInt = scoresImmut.entries.associate { (k, v) -> k to v }
        val sortedScores = scoresInt.toSortedMap()
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

    private fun displayOverallScore(view : View){
        val scores = gameInfo.match.getScores(gameInfo.gameMode)
        val tableLayout = view.findViewById<TableLayout>(R.id.scoreDisplayTable)
        tableLayout.removeAllViews()
        tableLayout.isStretchAllColumns = true
        if(gameInfo.movement.twoWinner){
            displayScoreTwoWinner(view, tableLayout, scores)
        }
        else{
            displayScoreOneWinner(view, tableLayout, scores)
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