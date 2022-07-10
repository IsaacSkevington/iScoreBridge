package com.OS3.iscorebridge

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import java.io.BufferedReader

open class MovementSkeleton{


    var boardsPerRound = 0
    var movementType : MovementType = MovementType.None

    var rounds : MutableMap<Int, Round> = HashMap()
    var twoWinner : Boolean = true
    var arrowSwitch = false
    var gameMode : Int = GAMEMODE_PAIRS

    constructor(gameMode : Int){
        this.gameMode = gameMode
    }
    constructor()

    constructor(other : MovementSkeleton){
        var string = other.stringify()
        fromString(string)
        this.movementType = other.movementType
        this.gameMode = other.gameMode
    }


    fun getTotalTables() : Int{
        return rounds[1]!!.tables.size
    }

    fun stringify() : String{
        var tableSplitter = "$$"
        var paramsSplitter = "/"
        var out = ""
        var firstLine = "${paramsSplitter}?NS${paramsSplitter}EW${paramsSplitter}StartBoard${paramsSplitter}EndBoard${paramsSplitter}${tableSplitter}"
        out += firstLine
        rounds.forEach {round ->
            out += "\n"
            round.value.tables.forEach { table ->
                out+="${table.value.pairNS.displayNumber}${paramsSplitter}${table.value.pairEW.displayNumber}${paramsSplitter}${table.value.boards.first()}${paramsSplitter}${table.value.boards.last()}"
                out += tableSplitter
            }
            out = out.removeSuffix(tableSplitter)
        }
        out += "\n<-- Number of Winners -->\n"
        out += if(twoWinner){
            "2"
        }
        else{
            "1"
        }
        return out
    }

    fun center(tv : TextView){
        tv.width = ViewGroup.LayoutParams.MATCH_PARENT
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
    }


    fun display(layout : TableLayout, outerLayout : LinearLayout){
        var topRow = TableRow(layout.context)
        outerLayout.addView(TextView(layout.context).also {
            it.text = "Round"
            it.typeface = Typeface.DEFAULT_BOLD
            it.setPadding(20, 20, 20, 20)
        })
        var params = TableRow.LayoutParams()

        //Rounds row
        params.span = 3
        rounds.forEach { round ->
            topRow.addView(TextView(layout.context).also {
                it.text = round.key.toString()
                it.layoutParams = params
                it.typeface = Typeface.DEFAULT_BOLD
                it.setPadding(20, 20, 20, 20)
                center(it)

            })
        }
        layout.addView(topRow)

        //Headings Row
        var expansionRow = TableRow(layout.context)
        outerLayout.addView(TextView(layout.context).also{
            it.text = "Table"
            it.setPadding(20, 20, 20, 20)
        })
        rounds.forEach{round ->
            expansionRow.addView(TextView(layout.context).also{
                it.text = "NS"
                it.setPadding(20, 20, 20, 20)
            })
            expansionRow.addView(TextView(layout.context).also{
                it.text = "EW"
                it.setPadding(20, 20, 20, 20)
            })
            expansionRow.addView(TextView(layout.context).also{
                it.text = "Boards"
                it.setPadding(20, 20, 20, 20)
            })
        }
        layout.addView(expansionRow)

        //Set up row for each table
        var rows = HashMap<Int, TableRow>()
        params = TableRow.LayoutParams()
        rounds[1]!!.tables.forEach {table ->
            rows[table.key] = TableRow(layout.context)
            outerLayout.addView(TextView(layout.context).also{
                it.text = table.key.toString()
                center(it)
            })
        }
        rounds.forEach {round ->
            round.value.tables.forEach { table ->
                rows[table.key]!!.addView(TextView(layout.context).also {
                    it.text = table.value.pairNS.displayNumber.toString()
                    center(it)
                })
                rows[table.key]!!.addView(TextView(layout.context).also {
                    it.text = table.value.pairEW.displayNumber.toString()
                    center(it)
                })
                rows[table.key]!!.addView(TextView(layout.context).also{
                    it.text = table.value.boardRange()
                    center(it)
                })
            }
        }
        for(i in 1..rounds[1]!!.tables.size){
            layout.addView(rows[i]!!)
        }
    }

    fun getSummary(): String{
        return "$movementType (${rounds.size} rounds, ${getTotalBoards()} boards) - " +
                if(twoWinner){
                    "2 Winners"
                }
        else{
            "1 Winner"
                }
    }


    fun arrowSwitch(){
        rounds[rounds.size]!!.tables.forEach {
            it.value.switchPairs()
        }
        twoWinner = false
        arrowSwitch = true
    }

    fun isWhole(float: Float) : Boolean{
        return float.toInt().toFloat() - float == 0f
    }

    fun scale(newBoardsPerRound : Int){
        rounds.forEach {round ->
            round.value.tables.forEach { table->
                var newBoards = ArrayList<Int>()
                var boardSet = (table.value.boards[0] - 1)/boardsPerRound
                var addVal = boardSet * (newBoardsPerRound - boardsPerRound)
                for(i in 0 until newBoardsPerRound){
                    newBoards.add(table.value.boards[0] + i + addVal)
                }
                table.value.boards = newBoards
            }
        }
        boardsPerRound = newBoardsPerRound
    }

    fun processParam(p : String, n : Int) : Int {
        if (!p.contains('n')) {
            return p.toInt()
        }
        var replaced = p.replace("n", n.toString())
        return Expression(replaced).evaluate().toInt()

    }

    fun fromArray(rounds : ArrayList<String>, n :Int){
        var twoWinner = rounds.last().toInt() == 2
        rounds.removeLast()

        var roundNum = 1
        var tableSplitter = "$$"
        var paramSplitter = "/"
        var paramPositions = mutableMapOf<String, Int>(
            "NS" to 0,
            "EW" to 1,
            "StartBoard" to 2,
            "EndBoard" to 3
        )
        rounds.forEach {round->
            if(!round.contains("<--")) {
                if (!round[0].isDigit()) {
                    var splitter = round.indexOf("?")
                    paramSplitter = round.substring(0, splitter)
                    var order = round.substring(splitter + 1).split(paramSplitter)
                    for (i in 0 until order.size - 1) {
                        paramPositions[order[i]] = i
                    }
                    tableSplitter = order.last()
                } else {
                    this.rounds[roundNum] = Round(roundNum)
                    var tables = round.split(tableSplitter)
                    var tableNum = 1
                    tables.forEach { table ->

                        var params = table.split(paramSplitter)
                        var pairNS = PlayerPair(processParam(params[paramPositions["NS"]!!], n))
                        var pairEW = PlayerPair(processParam(params[paramPositions["EW"]!!], n))
                        var bStart = processParam(params[paramPositions["StartBoard"]!!], n)
                        var bEnd = processParam(params[paramPositions["EndBoard"]!!], n)
                        var boards = ArrayList<Int>()
                        for (i in bStart..bEnd) {
                            boards.add(i)
                        }
                        this.rounds[roundNum]!!.tables[tableNum] =
                            Table(tableNum, boards, pairNS, pairEW)
                        tableNum++
                    }
                    roundNum++
                }
            }
        }
        boardsPerRound = this.rounds[1]!!.tables[1]!!.boards.size
        this.twoWinner = twoWinner
    }

    fun fromString(s : String){
        fromArray(ArrayList(s.split("\n")), 0)
    }

    fun fromFile(reader : BufferedReader, movementType : MovementType, n : Int = 0){

        var rounds = ArrayList(reader.readLines())
        fromArray(rounds, n)
        this.movementType = movementType

    }

    fun merge(initialTables : ArrayList<Table>){

        var tables = HashMap<Int, Table>()
        initialTables.forEach {
            tables[it.tableNumber] = it
        }
        rounds[1]!!.tables.forEach {
            tables[it.key]!!.pairNS.scoringNumber = it.value.pairNS.scoringNumber
            tables[it.key]!!.pairNS.displayNumber = it.value.pairNS.displayNumber
            tables[it.key]!!.pairEW.scoringNumber = it.value.pairEW.scoringNumber
            tables[it.key]!!.pairEW.displayNumber = it.value.pairEW.displayNumber
        }
        var players = HashMap<Int, PlayerPair>()
        tables.forEach {
            players[it.value.pairNS.displayNumber] = it.value.pairNS
            players[it.value.pairEW.displayNumber] = it.value.pairEW
        }

        rounds.forEach { round ->
            round.value.tables.forEach { table ->
                table.value.pairNS = players[table.value.pairNS.displayNumber]!!
                table.value.pairEW = players[table.value.pairEW.displayNumber]!!

            }
        }
    }

    fun getTotalBoards() : Int{
        return boardsPerRound * this.rounds.size
    }






}