package com.OS3.iscorebridge

const val MOVEMENT_MITCHELL = 0
const val MOVEMENT_HOWELL = 1
const val MOVEMENT_NONE = 2

class Movement {

    val dlm = "&&&"
    val secondarydlm = "**"

    var rounds : MutableMap<Int, Round>
    var selectedMovement : Int

    constructor(tables : Int, gameMode : Int, boards: Int, selectedMovement : Int, arrowSwitch : Boolean, shareAndRelay : Boolean){
        rounds = HashMap()
        createMovement(tables, gameMode, boards, selectedMovement, arrowSwitch, shareAndRelay)
        this.selectedMovement = selectedMovement
    }
    constructor(s:String) {

        var params = s.split(dlm + secondarydlm)
        selectedMovement = params[0].toInt()
        var hashMapString = params[1]
        rounds = HashMap()
        if (hashMapString.isNotEmpty()) {
            val roundsString = hashMapString.split(dlm)
            for (round in roundsString) {
                val r = Round(round)
                rounds[r.roundNumber] = r
            }
        }
    }

    fun mitchellPairs(tables : Int, boards : Int, arrowSwitch: Boolean, shareAndRelay: Boolean){
        if(tables % 2 == 0){

        }
        else{
            val boardsPerRound = boards/tables

            for(i in 0 until tables){
                rounds[i + 1] = Round(i+1)
            }

            for(tableNumber in 1..tables){
                for(roundNumber in 1..tables){
                    var pairEW = tables - roundNumber
                    if(pairEW <= tables){
                        pairEW += tables
                    }
                    var startBoard = boardsPerRound * (tableNumber + roundNumber - 2) + 1
                    if(startBoard > boards){
                        startBoard-= boards
                    }
                    var roundBoards = ArrayList<Int>()
                    for(k in startBoard..startBoard+boardsPerRound){
                        roundBoards.add(k)
                    }
                    var table = Table(tableNumber, roundBoards, tableNumber, pairEW)
                    rounds[roundNumber]!!.tables[tableNumber] = table
                }
            }
        }
    }
    fun mitchellTeams(tables:Int, boards: Int){

    }

    fun howell(tables: Int, boards: Int){

    }

    fun getNSTable(pairNumber : Int, roundNumber : Int) : Table?{
        var round = rounds[roundNumber]!!
        round.tables.values.forEach {
            if(it.pairNS == pairNumber){
                return it
            }
        }
        return null
    }

    fun getEWTable(pairNumber : Int, roundNumber : Int) : Table?{
        var round = rounds[roundNumber]!!
        round.tables.values.forEach {
            if(it.pairEW == pairNumber){
                return it
            }
        }
        return null
    }

    fun getBoardsTable(boards : ArrayList<Int>, roundNumber : Int) : Table?{
        var round = rounds[roundNumber]!!
        round.tables.values.forEach {
            var found = true
            it.boards.forEach { board ->
                if(!boards.contains(board)){
                    found = false
                }
            }
            if(found){
                return it
            }
        }
        return null
    }

    fun getNSMovement(round : Int, tableNumber : Int) : String{
        var pairNS = rounds[round]!!.tables[tableNumber]!!.pairNS
        var nextNS : Table = getNSTable(pairNS, round + 1)!!
        var movementNumber : Int =  nextNS.tableNumber - tableNumber
        if(movementNumber < 0){
            movementNumber += rounds[round]!!.tables.size
        }
        return if(movementNumber == 0){
            if(nextNS.pairNS == pairNS)
                "NS: Stay put!"
            else "NS: Change to EW at this table"
        } else{
            if(nextNS.pairNS == pairNS)
                "NS: Go to table ${nextNS.tableNumber}"
            else "NS: Go to table ${nextNS.tableNumber} and sit EW"
        }
    }
    fun getEWMovement(round : Int, tableNumber : Int) : String{
        var pairEW = rounds[round]!!.tables[tableNumber]!!.pairEW
        var nextEW : Table = getEWTable(pairEW, round + 1)!!
        var movementNumber : Int =  nextEW.tableNumber - tableNumber
        if(movementNumber < 0){
            movementNumber += rounds[round]!!.tables.size
        }
        return if(movementNumber == 0){
            if(nextEW.pairEW == pairEW)
                "EW: Stay put!"
            else "EW: Change to NS at this table"
        } else{
            if(nextEW.pairEW == pairEW)
                "EW: Go to table ${nextEW.tableNumber}"
            else "EW: Go to table ${nextEW.tableNumber} and sit NS"
        }
    }
    fun getBoardMovement(round : Int, tableNumber : Int) : String{
        var boards = rounds[round]!!.tables[tableNumber]!!.boards
        var nextBoards : Table = getBoardsTable(boards, round + 1)!!
        var movementNumber : Int =  nextBoards.tableNumber - tableNumber
        if(movementNumber < 0){
            movementNumber += rounds[round]!!.tables.size
        }
        return if(movementNumber == 0){
            "Boards: Stay here!"
        }
        else{
            "Boards: Go to table ${nextBoards.tableNumber}"
        }
    }


    private fun createMovement(tables : Int, boards : Int, gameMode : Int, movement : Int, arrowSwitch: Boolean, shareAndRelay: Boolean){

        if(movement == MOVEMENT_NONE){
            return
        }
        if(movement == MOVEMENT_HOWELL){
            howell(tables, boards)
        }
        if(movement == MOVEMENT_MITCHELL){
            if(gameMode == GAMEMODE_PAIRS) {
                mitchellPairs(tables, boards, arrowSwitch, shareAndRelay)
            }
            else{
                mitchellTeams(tables, boards)
            }
        }
    }

    override fun toString(): String {
        var out = selectedMovement.toString() + dlm + secondarydlm
        for(round in rounds.values){
            out += round.toString() + dlm
        }
        if(rounds.values.isNotEmpty()){
            out.substring(0, out.length - dlm.length)
        }

        return out
    }
}