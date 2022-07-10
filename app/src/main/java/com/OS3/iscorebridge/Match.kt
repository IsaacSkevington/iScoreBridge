package com.OS3.iscorebridge

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.FileInputStream
import java.io.FileOutputStream

class Match() : Exportable("scores", ".pdf"){

    private val pageWidth = 792
    private val pageHeight = 1120

    var boards : MutableMap<Int, Board?>
    var dlm = "||||"

    init{
        boards = HashMap()
    }


    constructor(matchString: String) : this() {

        boards = HashMap()
        val boardsAsString = matchString.split(dlm)
        if(boardsAsString.first() != "") {
            for (board in boardsAsString) {
                val b = Board(board)
                boards[b.boardNumber] = b
            }
        }

    }




    override fun toString(): String {
        var out = ""
        if(boards.size == 0){
            return ""
        }
        for(board in boards.values){
            out += board.toString() + dlm
        }
        return out.substring(0, out.length - dlm.length)
    }


    fun addGame(g : Game) : Game{
        if(!boards.containsKey(g.boardNumber)){
            boards[g.boardNumber] = Board(g.boardNumber)
        }
        if(boards[g.boardNumber]!!.hasGame(g)){
            boards[g.boardNumber]!!.games.remove(g)
        }
        boards[g.boardNumber]!!.addGame(g)
        return g
    }

    fun getScores(matchMode : Int) : MutableMap<Int, Int?>{
        val scores : MutableMap<Int, Int?> = HashMap()
        for(board in boards.values){
            val boardScores = board!!.calculateScores(matchMode)
            for(pair in boardScores.keys){
                if(!scores.containsKey(pair)){
                    scores[pair] = 0
                }
                if(!scores.containsKey(pair)) {
                    scores[pair] = 0
                }

                scores[pair] = boardScores[pair]!! + scores[pair]!!
            }
        }
        return scores
    }



    fun getGame(boardNumber: Int, pairNS: PlayerPair, pairEW: PlayerPair, suit: Suit, trickNumbers: Int, tricksMade: Int, lead: String, declarer: Cardinality, doubled: Boolean, redoubled: Boolean) : Game{
        val b = Board(boardNumber)
        return b.getGame(pairNS, pairEW, suit, trickNumbers, tricksMade, lead, declarer, doubled, redoubled)
    }

    fun getGames(myPair : PlayerPair) : ArrayList<Game> {

        var games = ArrayList<Game>()
        this.boards.forEach {
            it.value!!.games.forEach { game ->
                if(game.pairNS == myPair || game.pairEW == myPair){
                    games.add(game)
                }
            }
        }
        return games
    }

    fun merge(other : Match){
        for(board in other.boards){
            if(!this.boards.containsKey(board.key)){
                this.boards[board.key] = board.value
            }
            else{
                for(game in board.value!!.games){
                    if(!this.boards[board.key]!!.hasGame(game)){
                        this.boards[board.key]!!.addGame(game)
                    }
                }
            }
        }
    }

    fun getBoards(pairNS: PlayerPair, pairEW: PlayerPair) : ArrayList<Int>{
        var boardsPlayed = ArrayList<Int>()
        for(board in boards.values){
                if(board!!.hasGame(pairNS, pairEW)){
                    boardsPlayed.add(board.boardNumber)
            }
        }
        return boardsPlayed
    }

    fun zeroRemainingBoards(table : Table){
        var played = getBoards(table.pairNS, table.pairEW)
        for(board in table.boards){
            if(board !in played){
                Game(board, table.pairNS, table.pairEW, calculateVulnerability(board), Contract().also{it.setAllPass()}, 0, Card(CARD_NONE)).also {
                    addGame(it)
                    wifiService.send(MESSAGE_SEND_GAME, it.toString())
                }
            }
        }
    }

    fun getNextUnplayedBoard(lastBoard : Int, round: Int, table : Table) : Int{
        if(lastBoard == 0){
            if(round == 0){
                return 0
            }
            return table.boards[0]
        }
        var played = getBoards(table.pairNS, table.pairEW)
        var startBoardIndex = table.boards.indexOf(lastBoard)
        var currentBoardIndex = startBoardIndex + 1
        while(currentBoardIndex != startBoardIndex){
            if(currentBoardIndex == table.boards.size){
                currentBoardIndex = 0
                continue
            }
            if(table.boards[currentBoardIndex] !in played){
                return table.boards[currentBoardIndex]
            }
            currentBoardIndex++

        }
        return 0

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun drawScores(page : PdfDocument.Page, scoringMode: Int){
        val title = Paint()
        title.textAlign = Paint.Align.CENTER
        title.isUnderlineText = true
        title.textSize = 30F
        val canvas = page.canvas
        canvas.drawText("Overall scores", (pageWidth/2).toFloat(), 70F, title)

        val scores = getScores(scoringMode)
        val table = if(scoringMode == GAMEMODE_PAIRS){
            TablePage(arrayOf("Position", "Pair", "Final Score (MPs)"))
        } else{
            TablePage(arrayOf("Position", "Team", "Final Score (IMPs)"))
        }
        val scoresImmut : MutableMap<Int, Int?> = scores
        val scoresInt =  scoresImmut.entries.associate{(k,v)-> k to v}

        val sortedScores = scoresInt.toSortedMap()
        var i = 0
        for(pair in sortedScores.keys.reversed()){
            i++
            table.addRow(arrayOf(i.toString(), pair.toString(), sortedScores[pair]!!.toString()))
        }
        table.draw(page, 20f, 200f, false)
    }

    override fun read(fileInputStream: FileInputStream) : Boolean{
        return false
    }

    override fun write(fileOutputStream: FileOutputStream){
        return toPDF(gameInfo.gameMode, fileOutputStream)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun toPDF(scoringMode : Int, output : FileOutputStream){
        val document = PdfDocument()
        var pageInfo = PageInfo.Builder(pageWidth, pageHeight, 1).create()

        val pageScores: PdfDocument.Page = document.startPage(pageInfo)
        drawScores(pageScores, scoringMode)
        document.finishPage(pageScores)

        for(i in 1..boards.size){
            pageInfo = PageInfo.Builder(pageWidth, pageHeight, pageInfo.pageNumber + 1).create()
            val boardPage = document.startPage(pageInfo)
            boards[i]!!.toPDF(boardPage, scoringMode)
            document.finishPage(boardPage)
        }
        document.writeTo(output)
    }




}