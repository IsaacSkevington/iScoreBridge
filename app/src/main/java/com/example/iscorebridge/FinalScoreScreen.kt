package com.example.iscorebridge

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import org.w3c.dom.Text

class FinalScoreScreen : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun makeTitle(text : String, view : View) : TextView{
        var title = TextView(view.context)
        title.text = text
        title.textSize = 20F
        title.setTypeface(null, Typeface.BOLD_ITALIC)
        return title

    }

    private fun displayBoardList(view : View){
        var boards = match.boards.keys
        var tableLayout = view.findViewById<TableLayout>(R.id.boardsDisplayTable)
        var titleRow = TableRow(view.context)
        titleRow.addView(makeTitle("Board", view))
        titleRow.addView(makeTitle("View", view))
        tableLayout.addView(titleRow)
        for(board in boards){
            var row = TableRow(view.context)
            var boardText = TextView(view.context)
            boardText.text = board.toString()
            row.addView(boardText)
            var boardButton = Button(view.context)
            boardButton.text = "More..."
            boardButton.setOnClickListener {
                displayBoardScore(view, board)
            }
            row.addView(boardButton)
            tableLayout.addView(row)
        }
    }

    private fun displayOverallScore(view : View){
        var scores = match.getScores(gameInfo.gameMode)
        var tableLayout = view.findViewById<TableLayout>(R.id.scoreDisplayTable)
        var titleRow = TableRow(view.context)
        if(gameInfo.gameMode == GAMEMODE_TEAMS){
            titleRow.addView(makeTitle("Pair", view))
            titleRow.addView(makeTitle("Final Score (IMPs)", view))
        }
        else{
            titleRow.addView(makeTitle("Team", view))
            titleRow.addView(makeTitle("Final Score (MPs)", view))
        }
        tableLayout.addView(titleRow)
        var scoresImmut = scores as Map<Int, Int>
        var sortedScores = scoresImmut.toSortedMap()
        for(pair in sortedScores.keys){
            var row = TableRow(view.context)
            var pairText = TextView(view.context)
            pairText.text = pair.toString()
            row.addView(pairText)
            var scoreText = TextView(view.context)
            scoreText.text = sortedScores[pair]!!.toString()
            row.addView(scoreText)
            tableLayout.addView(row)
        }

    }

    private fun displayBoardScore(view : View, boardNumber : Int){

        view.findViewById<TextView>(R.id.boardNumberText).text = "Board " + boardNumber.toString()
        var tableLayout = view.findViewById<TableLayout>(R.id.boardDisplayTable)
        tableLayout.removeAllViews()
        tableLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))

        var titleRow = TableRow(view.context)
        titleRow.addView(makeTitle("Contract", view))
        titleRow.addView(makeTitle("Lead", view))
        titleRow.addView(makeTitle("Tricks", view))
        titleRow.addView(makeTitle("Score (NS)", view))
        var currentScores : MutableMap<Int,Int?>
        if(gameInfo.gameMode == GAMEMODE_TEAMS){
            currentScores = match.boards[boardNumber]!!.teamsScore()
            titleRow.addView(makeTitle("IMPs (NS)", view))
        }
        else{
            currentScores = match.boards[boardNumber]!!.pairScore()
            titleRow.addView(makeTitle("MPs (NS)", view))
        }
        titleRow.gravity = Gravity.CENTER_HORIZONTAL
        tableLayout.addView(titleRow)

        for(game in match.boards[com.example.iscorebridge.boardNumber]!!.games){
            var tableRow = TableRow(view.context)
            var contractView = TextView(view.context)
            contractView.text = game.contract.toDisplayString()
            var leadView = TextView(view.context)
            leadView.text = game.lead.toString()
            var tricksView = TextView(view.context)
            tricksView.text = game.tricks.toString()
            var pointsView = TextView(view.context)
            pointsView.text = game.score.toString()
            var scoreView = TextView(view.context)
            if(currentScores.containsKey(game.pairNS)){
                scoreView.text = currentScores[game.pairNS].toString()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_final_score_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayOverallScore(view)
        displayBoardList(view)
    }
}
