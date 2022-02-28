package com.OS3.iscorebridge

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController





class BoardScoreView : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board_score_view, container, false)
    }

    private fun makeText(text : String, view : View) : TextView{
        var textView = TextView(view.context)
        textView.text = text
        textView.setPadding(10,10,10,10)
        return textView

    }

    private fun display(view : View){
        var tableLayout = view.findViewById<TableLayout>(R.id.ScoreViewTable)
        var currentScores : MutableMap<Int,Int?>
        if(gameInfo.gameMode == GAMEMODE_TEAMS){
            currentScores = match.boards[boardNumber]!!.teamsScore()
            view.findViewById<TextView>(R.id.ScoreTitle).text = "IMPs (NS)"
        }
        else{
            currentScores = match.boards[boardNumber]!!.pairScore()
            view.findViewById<TextView>(R.id.ScoreTitle).text = "MPs (NS)"
        }

        var gamesSorted = match.boards[boardNumber]!!.sortGamesByScore()

        for(game in gamesSorted){
            var tableRow = TableRow(view.context)
            if(game.pairNS == pairNS && game.pairEW == pairEW){
                tableRow.setBackgroundColor(Color.parseColor("#A6DAF2"))
            }
            tableRow.addView(makeText(game.contract.toDisplayString(), view))
            tableRow.addView(makeText(game.lead.toString(), view))
            tableRow.addView(makeText(game.tricks.toString(), view))
            tableRow.addView(makeText(game.score.toString(), view))

            if(currentScores.containsKey(game.pairNS)){
                tableRow.addView(makeText(currentScores[game.pairNS].toString(), view))
            }
            else{
                tableRow.addView(makeText("?", view))
            }
            tableLayout.addView(tableRow)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.nextScoreEntryButton).setOnClickListener {
            findNavController().navigate(R.id.scoreViewToScoreEntry)
        }
        view.findViewById<Button>(R.id.finishButton).setOnClickListener {
            findNavController().navigate(R.id.scoreViewToFinalScore)
        }
        display(view)
    }
}