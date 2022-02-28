package com.OS3.iscorebridge

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Build
import androidx.annotation.RequiresApi

@Volatile var match : Match = Match()

class Match {

    val pageWidth = 1120
    val pageHeight = 792

    var boards : MutableMap<Int, Board?>
    var dlm = "||||"

    constructor(){
        boards = HashMap<Int, Board?>()
    }


    constructor(matchString: String){

        boards = HashMap<Int, Board?>()
        var boardsAsString = matchString.split(dlm)
        for(board in boardsAsString){
            var b = Board(board)
            boards[b.boardNumber] = b
        }

    }


    override fun toString(): String {
        var out = ""
        for(board in boards.values){
            out += board.toString() + dlm
        }
        return out.substring(0, out.length - dlm.length)
    }


    fun addGame(g : Game) : Game{
        if(!boards.containsKey(g.boardNumber)){
            boards[g.boardNumber] = Board(g.boardNumber)
        }
        boards[g.boardNumber]!!.addGame(g)
        return g
    }

    fun getScores(matchMode : Int) : MutableMap<Int, Int?>{
        var scores = HashMap<Int, Int>() as MutableMap<Int, Int?>
        for(board in boards.values){
            var boardScores = board!!.calculateScores(matchMode)
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



    fun getGame(boardNumber: Int, pairNS: Int, pairEW: Int, suit: Char, trickNumbers: Int, tricksMade: Int, lead: String, declarer: Char, doubled: Boolean, redoubled: Boolean) : Game{
        var b = Board(boardNumber)
        return b.getGame(pairNS, pairEW, suit, trickNumbers, tricksMade, lead, declarer, doubled, redoubled)
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

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun drawScores(page : PdfDocument.Page){
        var title = Paint()
        title.textAlign = Paint.Align.CENTER
        title.isUnderlineText = true
        title.textSize = 30F
        var canvas = page.canvas
        canvas.drawText("Overall scores", (pageWidth/2).toFloat(), 30F, title)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun toPDF(filename : String){
        var document = PdfDocument()
        val pageInfo = PageInfo.Builder(pageWidth, pageHeight, 1).create()

        val pageScores: PdfDocument.Page = document.startPage(pageInfo)
        drawScores(pageScores)
        document.finishPage(pageScores)

        for(i in 1..boards.size + 1){
            val pageInfo = PageInfo.Builder(pageWidth, pageHeight, pageInfo.pageNumber + 1).create()
            var boardPage = document.startPage(pageInfo)
            boards[i]!!.toPDF(boardPage)
            document.finishPage(boardPage)
        }


    }




}