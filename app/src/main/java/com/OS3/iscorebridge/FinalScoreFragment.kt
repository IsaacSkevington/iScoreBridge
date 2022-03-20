package com.OS3.iscorebridge

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.time.LocalDate

class FinalScoreFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun makeTitle(text : String, view : View) : TextView{
        var title = TextView(view.context)
        title.text = text
        title.textSize = 20F
        title.setTypeface(null, Typeface.BOLD_ITALIC)
        title.setPadding(10,10,10,10)
        return title

    }

    private fun makeText(text : String, view : View) : TextView{
        var textView = TextView(view.context)
        textView.text = text
        textView.setPadding(10,10,10,10)
        return textView

    }

    private fun displayBoardList(view : View){
        var boards = match.boards.keys
        var tableLayout = view.findViewById<TableLayout>(R.id.boardsDisplayTable)
        tableLayout.isStretchAllColumns = true
        var titleRow = TableRow(view.context)
        titleRow.addView(makeTitle("Board", view))
        titleRow.addView(makeTitle("View", view))
        tableLayout.addView(titleRow)
        for(board in boards){
            var row = TableRow(view.context)
            row.addView(makeText(board.toString(), view))
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
        tableLayout.isStretchAllColumns = true
        var titleRow = TableRow(view.context)
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
        var scoresImmut = scores as Map<Int, Int>
        var sortedScores = scoresImmut.toSortedMap()
        var i = 0
        for(pair in sortedScores.keys.reversed()){
            i++
            var row = TableRow(view.context)
            row.addView(makeText(i.toString(), view))
            row.addView(makeText(pair.toString(), view))
            row.addView(makeText(sortedScores[pair]!!.toString(), view))
            tableLayout.addView(row)
        }

    }

    private fun displayBoardScore(view : View, boardNumber : Int){

        view.findViewById<TextView>(R.id.boardNumberText).text = "Board " + boardNumber.toString()
        var tableLayout = view.findViewById<TableLayout>(R.id.boardDisplayTable)
        tableLayout.isStretchAllColumns = true
        tableLayout.removeAllViews()
        tableLayout.setBackgroundColor(Color.TRANSPARENT)
        view.findViewById<NestedScrollView>(R.id.greyScrollView).setBackgroundColor(Color.TRANSPARENT)

        var titleRow = TableRow(view.context)
        titleRow.addView(makeTitle("NS", view))
        titleRow.addView(makeTitle("EW", view))
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
        tableLayout.addView(titleRow)
        var gamesSorted = match.boards[boardNumber]!!.sortGamesByScore()

        for(game in gamesSorted){
            var tableRow = TableRow(view.context)
            tableRow.addView(makeText(game.pairNS.toString(), view))
            tableRow.addView(makeText(game.pairEW.toString(), view))
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_final_score_screen, container, false)
    }


        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                val contentResolver = context!!.contentResolver
                    try {
                        var descriptor = contentResolver.openFileDescriptor(uri, "w")
                        FileOutputStream(descriptor?.fileDescriptor).use {
                            match.toPDF(gameInfo.gameMode, it)
                        }
                        Toast.makeText(context!!, "Saved successfully", Toast.LENGTH_LONG).show()
                    } catch (e: FileNotFoundException) {
                        Toast.makeText(context!!, "Failed to save", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            var d = LocalDate.now()
            var dateString = d.dayOfMonth.toString() + d.month.toString() + d.year.toString()
            putExtra(Intent.EXTRA_TITLE, "scores" + LocalDate.now().toString() + ".pdf")
        }
        startActivityForResult(intent, CREATE_FILE)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.exportPDFButton).setOnClickListener {
            createFile()
        }
        displayOverallScore(view)
        displayBoardList(view)
    }
}
