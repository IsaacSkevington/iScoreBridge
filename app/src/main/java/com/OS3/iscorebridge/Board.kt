package com.OS3.iscorebridge

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs

fun scoringModeToString(scoringMode: Int) : String{
    return when(scoringMode) {
        GAMEMODE_TEAMS -> "IMPs"
        GAMEMODE_PAIRS -> "MPs"
        else -> ""
    }
}

fun getDealer(boardNumber: Int) : Cardinality{
    val index = boardNumber % 4
    return CARDINALITIES[index]
}

const val TEAMS : Int = 0
const val PAIRS : Int = 1
val IMPCONVERSION : Array<Int> = arrayOf(0, 20, 50, 90, 130, 170, 220, 270, 320, 370, 430, 500,
    600, 750, 900, 1100, 1300, 1500, 1750, 2000, 2250, 2500, 3000, 3500, 4000)


class Board {
    var boardNumber: Int
    var games: ArrayList<Game> = ArrayList()
    private var vulnerability: Vulnerability
    var dlm = "|||"
    var deal : Deal? = null

    constructor(boardNumber: Int){
        this.boardNumber = boardNumber
        this.vulnerability = calculateVulnerability(boardNumber)
    }

    constructor(board:String){
        val parametersAsString = board.split("$dlm$")
        val gamesAsString = parametersAsString[1].split(dlm)
        for(game in gamesAsString){
            games.add(Game(game))
        }
        this.boardNumber = parametersAsString[0].toInt()
        this.vulnerability = Vulnerability(parametersAsString[2])
    }

    override fun toString():String{
        var out = "$boardNumber$dlm$"
        if(games.size > 0){
            out += games[0].toString()
            for(i in 1..games.size){
                out += dlm + games.toString()
            }
        }
        out += "$dlm$$vulnerability"
        return out

    }

    fun sortGamesByScore() : ArrayList<Game>{
        val sortedList = ArrayList<Game>()
        for(i in 0 until games.size){
            var added = false
            for(j in 0 until sortedList.size){
                if(games[i].score > sortedList[j].score){
                    sortedList.add(j, games[i])
                    added = true
                    break
                }
            }
            if(!added){
                sortedList.add(games[i])
            }
        }
        return sortedList


    }



    fun getGame(pairNS: Int, pairEW: Int) : Game?{
        for(game in games){
            if(game.pairNS == pairNS && game.pairEW == pairEW){
                return game
            }
        }
        return null
    }

    fun getGame(game : Game) : Game?{
        return getGame(game.pairNS, game.pairEW)
    }

    fun hasGame(pairNS : Int, pairEW: Int) : Boolean{
        getGame(pairNS, pairEW) ?: return false
        return true
    }


    fun hasGame(number : Int) : Boolean{
        this.games.forEach {
            if(it.pairNS == number || it.pairEW == number){
                return true
            }
        }
        return false
    }

    fun hasGame(compareGame : Game) : Boolean{
        return hasGame(compareGame.pairNS, compareGame.pairEW)
    }

    fun addGame(game: Game) : Game{
        if(!hasGame(game)) {
            this.games.add(game)
        }
        return game
    }

    fun getGame(
        pairNS: Int,
        pairEW: Int,
        suit: Suit,
        trickNumbers: Int,
        tricksMade: Int,
        lead: String,
        declarer: Cardinality,
        doubled: Boolean,
        redoubled: Boolean
    ): Game {

        val contract = Contract(suit, trickNumbers, declarer, doubled, redoubled)
        val leadCard = Card(lead)
        return Game(boardNumber, contract, pairNS, pairEW, tricksMade, leadCard, vulnerability)
    }

    private fun IMPConversion(score: Int) : Int{
        for(i in 0 until IMPCONVERSION.size - 1){
            if(score >= IMPCONVERSION[i] && score <= IMPCONVERSION[i+1]){
                return i
            }
        }
        return 24
    }

    fun teamsScore() : MutableMap<Int, Int?>{
        val scores = HashMap<Int, Int?>()
        for(game1 in this.games){
            for(game2 in this.games){
                if(game1.pairNS == game2.pairEW && game1.pairEW == game2.pairNS && !scores.containsKey(game1.pairNS)){
                    val overallScorewrtNS = game1.score - game2.score
                    val IMPs : Int = IMPConversion(abs(overallScorewrtNS))
                    if(overallScorewrtNS > 0){
                        scores[game1.pairNS] = IMPs
                        scores[game1.pairEW] = IMPs * -1
                    }
                    else{
                        scores[game1.pairNS] = IMPs * -1
                        scores[game1.pairEW] = IMPs
                    }
                }
            }
        }
        return scores
    }

    fun pairScoreToPercent(score: Int) : Int{
        return ((score.toDouble() / (games.size - 1).toDouble()) * 100.0).toInt()
    }

    fun pairScore() : MutableMap<Int, Int?>{
        val scores : MutableMap<Int, Int?> = HashMap()
        for(game1 in this.games){
            for(game2 in this.games){
                if(game1.pairNS != game2.pairNS){
                    if(!scores.containsKey(game1.pairNS)){
                        scores[game1.pairNS] = 0
                    }
                    scores[game1.pairNS] = when {
                        game1.score == game2.score -> scores[game1.pairNS] !!+ 1
                        game1.score > game2.score -> scores[game1.pairNS] !!+ 2
                        else -> scores[game1.pairNS]
                    }
                }
                if(game1.pairEW != game2.pairEW){
                    if(!scores.containsKey(game1.pairEW)){
                        scores[game1.pairEW] = 0
                    }
                    scores[game1.pairEW] = when {
                        game1.score == game2.score -> scores[game1.pairEW] !!+ 1
                        game1.score < game2.score -> scores[game1.pairEW] !!+ 2
                        else -> scores[game1.pairEW]
                    }
                }
            }
        }
        return scores
    }

    fun calculateScores(scoringMode : Int) : MutableMap<Int, Int?>{
        return when(scoringMode) {
            GAMEMODE_TEAMS -> teamsScore()
            GAMEMODE_PAIRS -> pairScore()
            else -> HashMap()
        }
    }

    private fun calculateVulnerability(boardNumber: Int) : Vulnerability{
        return when(boardNumber % 4){
            0->Vulnerability(CARDINALITIES)
            1-> Vulnerability(arrayOf())
            2-> Vulnerability(arrayOf(NORTH, SOUTH))
            3-> Vulnerability(arrayOf(EAST, WEST))
            else -> Vulnerability(arrayOf())
        }
    }


    private fun display(view : View){

    }


    private fun makeText(text : String, view : View) : TextView{
        val textView = TextView(view.context)
        textView.text = text
        textView.setPadding(10,10,10,10)
        return textView

    }




    fun getDisplayScore(myPair : Int) : String{
        var postfix : String
        val currentScores : MutableMap<Int,Int?> = if(gameInfo.gameMode == GAMEMODE_TEAMS){

            postfix = "IMPs"
            teamsScore()
        }
        else{
            postfix = "MPs"
            pairScore()
        }
        for(game in games) {
            if (game.pairNS == myPair || game.pairEW == myPair) {
                return "${currentScores[myPair]!!} $postfix"
            }
        }
        return "0 $postfix"
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")

    fun viewHand(view : View, boardNumber : Int, layoutInflater : LayoutInflater){
        var builder = AlertDialog.Builder(view.context)
        var boardView = layoutInflater.inflate(R.layout.deal_view, null)
        var deal = match.boards[boardNumber]!!.deal
        builder.setMessage("Board $boardNumber")
            .setPositiveButton("Ok"){_, _ ->

            }
            .setView(boardView)
        deal!!.display(boardView)
        var dialog = builder.create()
        dialog.show()
    }

    fun viewBidding(view : View, pairNS : Int, pairEW: Int, boardNumber : Int, layoutInflater : LayoutInflater){
        var builder = AlertDialog.Builder(view.context)
        var biddingView = layoutInflater.inflate(R.layout.bidding_view, null)
        biddingView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200)
        match.boards[boardNumber]!!.getGame(pairNS, pairEW)!!.bidding.updateBiddingTable(biddingView)
        builder.setMessage("Bidding for board $boardNumber")
            .setPositiveButton("Ok"){_, _ ->

            }
            .setView(biddingView)
        var dialog = builder.create()
        dialog.setView(biddingView)
        dialog.show()
    }

    fun handBuilder(fragMan: FragmentManager, view : View, boardNumber: Int){
        showHandConstructorDiag(fragMan){
            if(it.validate()){
                it.number = boardNumber
                match.boards[boardNumber]!!.deal = it
                wifiService.send(SENDNEWDEAL, it.toString())
                Toast.makeText(view.context, "Board created successfully", Toast.LENGTH_LONG).show()
                true
            }
            else{
                Toast.makeText(view.context, "Deal is not complete", Toast.LENGTH_LONG).show()
                false
            }
        }
    }

    fun biddingBuilder(fragMan : FragmentManager, view : View, pairNS: Int, pairEW : Int, boardNumber: Int){
        showBiddingConstructorDiag(fragMan, boardNumber){bidding ->
            this.getGame(pairNS, pairEW)?.also {game ->
                game.bidding = bidding
                wifiService.send(SENDEDITGAME, game.toString())
            }
        }

    }


    fun createHand(fragMan: FragmentManager, view : View, boardNumber: Int){

        var builder = AlertDialog.Builder(view.context)
        builder.setMessage("Board $boardNumber hasn't be created yet. Would you like to input the cards now?")
            .setPositiveButton("Yes"){_, _ ->
                handBuilder(fragMan, view, boardNumber)
            }
            .setNegativeButton("No"){_, _ ->

            }
        var dialog = builder.create()
        dialog.show()
    }

    fun createBidding(fragMan: FragmentManager, view : View, pairNS : Int, pairEW : Int,  boardNumber: Int){

        var builder = AlertDialog.Builder(view.context)
        builder.setMessage("Board $boardNumber doesn't have bidding yet. Would you like to input the bids now?")
            .setPositiveButton("Yes"){_, _ ->
                biddingBuilder(fragMan, view, pairNS, pairEW, boardNumber)
            }
            .setNegativeButton("No"){_, _ ->

            }
        var dialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun displayScore(fragMan: FragmentManager, myPair: Int, view: View, layoutInflater: LayoutInflater){
        games.forEach {
            if(it.pairNS == myPair){
                return displayScore(fragMan, myPair, it.pairEW, view, layoutInflater)
            }
            if(it.pairEW == myPair){
                return displayScore(fragMan, it.pairNS, myPair, view, layoutInflater)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun displayScore(fragMan: FragmentManager, pairNS : Int, pairEW : Int, view : View, layoutInflater: LayoutInflater){
        var tableLayout = view.findViewById<TableLayout>(R.id.ScoreViewTable)
        val currentScores : MutableMap<Int,Int?> = if(gameInfo.gameMode == GAMEMODE_TEAMS){

            tableLayout.findViewById<TextView>(R.id.ScoreTitle).text = "IMPs (NS)"
            teamsScore()
        }
        else{
            tableLayout.findViewById<TextView>(R.id.ScoreTitle).text = "MPs (NS)"
            pairScore()
        }

        val gamesSorted = sortGamesByScore()

        for(game in gamesSorted){
            val tableRow = TableRow(tableLayout.context)
            if(game.pairNS == pairNS && game.pairEW == pairEW){
                tableRow.setBackgroundColor(Color.parseColor("#A6DAF2"))
            }
            tableRow.addView(makeText(game.contract.toDisplayString(), tableLayout))
            tableRow.addView(makeText(game.lead.toString(), tableLayout))
            tableRow.addView(makeText(game.tricks.toString(), tableLayout))
            tableRow.addView(makeText(game.score.toString(), tableLayout))

            if(currentScores.containsKey(game.pairNS)){
                tableRow.addView(makeText(currentScores[game.pairNS].toString(), tableLayout))
            }
            else{
                tableRow.addView(makeText("?", tableLayout))
            }
            tableLayout.addView(tableRow)
        }
        view.findViewById<FloatingActionButton>(R.id.viewBoardButton).setOnClickListener {
            if(deal == null){
                createHand(fragMan, view, boardNumber)
            }
            else {
                viewHand(view, boardNumber, layoutInflater)
            }
        }
        view.findViewById<FloatingActionButton>(R.id.viewBiddingButton).setOnClickListener {
            if(getGame(pairNS, pairEW) != null && !getGame(pairNS, pairEW)!!.bidding.canBid()){
                viewBidding(view, pairNS, pairEW, boardNumber, layoutInflater)
            }
            else {
                createBidding(fragMan, view, pairNS, pairEW, boardNumber)
            }
        }


        setStarListener(view.findViewById(R.id.boardViewStarButton))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setStarListener(s : StarButton){
        s.also {
            it.checked = MYINFO.isStarred(this)
            it.update()

            it.setOnTurnOn {
                MYINFO.addStarredBoard(this)
            }
            it.setOnTurnOff {
                MYINFO.removeStarredBoard(this)
            }

        }
    }

    override fun equals(other: Any?): Boolean {
        return try{
            var otherBoard = other as Board
            otherBoard.boardNumber == this.boardNumber
        } catch(e : ClassCastException){
            false
        }
    }



    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun toPDF(page : PdfDocument.Page, scoringMode: Int){

        val scoringString = scoringModeToString(scoringMode) + " (N/S)"
        val scoringMap = calculateScores(scoringMode)
        val table = TablePage(arrayOf("N/S", "E/W", "Contract", "Lead", "Tricks", "Score", scoringString))
        for(game in games){
            val valuesArray : ArrayList<String> = game.toArray()
            val toAdd = scoringMap[valuesArray[0].toInt()]!!.toString()
            valuesArray.add(toAdd)
            table.addRow(valuesArray.toTypedArray())
        }
        val titleText = Paint()
        titleText.textAlign = Paint.Align.CENTER
        titleText.isUnderlineText = true
        titleText.textSize = 30F
        page.canvas.drawText("Board $boardNumber", (page.canvas.width/2).toFloat(), 70f, titleText)
        if(deal == null){
            table.draw(page, 20f, 200f, false)
        }
        else{
            deal!!.toPDF(page, (page.canvas.width/2).toFloat(), 200f)
            table.draw(page, 20f, 200f, false)
        }


    }

    fun playedBy(myNumber: Int): Boolean {
        return hasGame(myNumber)
    }
}