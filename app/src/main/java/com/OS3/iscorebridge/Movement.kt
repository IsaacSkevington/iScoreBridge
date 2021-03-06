package com.OS3.iscorebridge

fun getPlayerPair(tables : ArrayList<Table>, pairNum : Int) : PlayerPair?{
    tables.forEach {
        if(it.pairNS.scoringNumber == pairNum){
            return it.pairNS
        }
        if(it.pairEW.scoringNumber == pairNum){
            return it.pairEW
        }
    }
    return null
}



class Movement : MovementSkeleton{

    val dlm = "&&&"
    val roundListSplitter = "££"
    val secondarydlm = "££"

    constructor(tables : ArrayList<Table>, skeleton : MovementSkeleton) : super(skeleton){
        this.merge(tables)
    }

    constructor(gameMode : Int) : super(gameMode)

    constructor(s:String) : super() {

        var params = s.split(dlm)
        movementType = MovementType.valueOf(params[0])
        var roundsString = params[1]
        rounds = HashMap()
        if (roundsString != "|") {
            val roundsStringList = roundsString.split(roundListSplitter)
            for (round in roundsStringList) {
                val r = Round(round)
                rounds[r.roundNumber] = r
            }
        }
        twoWinner = params[2].toBoolean()
        gameMode = params[3].toInt()
    }


    fun splitPairs() : Pair<ArrayList<PlayerPair>, ArrayList<PlayerPair>>{
        var ret = Pair(ArrayList<PlayerPair>(), ArrayList<PlayerPair>())
        this.rounds[1]!!.tables.forEach {
            ret.first.add(it.value.pairNS)
            ret.second.add(it.value.pairEW)
        }
        return ret
    }

    fun getTablesInPlay(roundNumber : Int) : ArrayList<Table>{
        var out = ArrayList<Table>()
        rounds[roundNumber]!!.tables.forEach {
            if(it.value.getNextBoard() != 0){
                out.add(it.value)
            }
        }
        return out
    }



    fun getNSTable(pair : PlayerPair, roundNumber : Int) : Table?{
        var round = rounds[roundNumber]!!
        round.tables.values.forEach {
            if(it.pairNS == pair){
                return it
            }
        }
        return null
    }

    fun getEWTable(pair : PlayerPair, roundNumber : Int) : Table?{
        var round = rounds[roundNumber]!!
        round.tables.values.forEach {
            if(it.pairEW == pair){
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

    fun getTable(round : Int, table : Int) : Table{
        return rounds[round]!!.tables[table]!!
    }

    fun getNSMovement(round : Int, currentTable : Table) : String{
        var pairNS = currentTable.pairNS
        var nextNS : Table = getNSTable(pairNS, round + 1)!!
        var movementNumber : Int =  nextNS.tableNumber - currentTable.tableNumber
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
    fun getEWMovement(round : Int, currentTable: Table) : String{
        var pairEW = currentTable.pairEW
        var nextEW : Table = getEWTable(pairEW, round + 1)!!
        var movementNumber : Int =  nextEW.tableNumber - currentTable.tableNumber
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
    fun getBoardMovement(round : Int, currentTable : Table) : String{
        var boards = currentTable.boards
        if(boards.size == 0){
            return "Boards: Go grab the boards you need when the round has started"
        }
        var nextBoards : Table = getBoardsTable(boards, round + 1)?: return "Boards: Go to the relay table"
        var movementNumber : Int =  nextBoards.tableNumber - currentTable.tableNumber
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




    fun getTable(pair : PlayerPair, round : Int) : Table{
        return getNSTable(pair, round) ?: return getEWTable(pair, round)!!
    }



    override fun toString(): String {
        var roundsArray = ArrayList(rounds.values)
        var roundsString = "|"
        if(roundsArray.size != 0) {
            roundsString = ""
            roundsArray.forEach {
                roundsString += it.toString() + roundListSplitter
            }
            roundsString = roundsString.removeSuffix(roundListSplitter)
        }
        return movementType.toString() + dlm + roundsString + dlm + twoWinner.toString() + dlm + gameMode.toString()
    }
}