package com.example.iscorebridge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.navigation.fragment.findNavController


/**
 * A simple [Fragment] subclass.
 * Use the [BoardScoreView.newInstance] factory method to
 * create an instance of this fragment.
 */



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

    private fun display(view : View){
        var tableLayout = view.findViewById<TableLayout>(R.id.ScoreViewTable)
        var currentScores : MutableMap<Int,Int?>
        if(teams){
            currentScores = match.boards[boardNumber]!!.teamsScore()
            view.findViewById<TextView>(R.id.ScoreTitle).text = "IMPs (NS)"
        }
        else{
            currentScores = match.boards[boardNumber]!!.pairScore()
            view.findViewById<TextView>(R.id.ScoreTitle).text = "MPs (NS)"
        }

        for(game in match.boards[boardNumber]!!.games){
            var tableRow = TableRow(view.context)
            var contractView = TextView(view.context)
            contractView.text = game.contract.toString()
            var tricksView = TextView(view.context)
            tricksView.text = game.tricks.toString()
            var pointsView = TextView(view.context)
            pointsView.text = game.score.toString()
            var scoreView = TextView(view.context)
            if(currentScores.containsKey(pairNS)){
                scoreView.text = currentScores[pairNS].toString()
            }
            else{
                scoreView.text = "?"
            }
            tableRow.addView(contractView)
            tableRow.addView(tricksView)
            tableRow.addView(pointsView)
            tableRow.addView(scoreView)
            tableLayout.addView(tableRow)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.nextScoreEntryButton).setOnClickListener {
            findNavController().navigate(R.id.scoreViewToScoreEntry)
        }
        display(view)
    }
}