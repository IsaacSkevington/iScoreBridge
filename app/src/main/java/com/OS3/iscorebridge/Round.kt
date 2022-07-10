package com.OS3.iscorebridge

class Round {
    var tables : MutableMap<Int, Table> = HashMap<Int, Table>()
    var roundNumber : Int
    var dlm = "&&"
    var tableSplitter = "***^"

    constructor(roundNumber : Int){
        this.roundNumber = roundNumber
    }

    constructor(s : String){
        val params = s.split(dlm)
        this.roundNumber = params[0].toInt()
        for(table in params[1].split(tableSplitter)){
            var t = Table(table)
            tables[t.tableNumber] = t
        }
    }


    fun getTableWithBoard(board : Int) : Int{
        tables.forEach {
            if(board in it.value.boards){
                return it.value.tableNumber
            }
        }
        return 0
    }

    fun copyTablesPairs(tables : MutableMap<Int, Table>){
        tables.forEach {
            this.tables[it.key]!!.pairNS = it.value.pairNS
            this.tables[it.key]!!.pairEW = it.value.pairEW
        }
    }

    override fun toString(): String {
        var tablesString = ""
        tables.values.forEach {
            tablesString += it.toString() + tableSplitter
        }
        tablesString = tablesString.removeSuffix(tableSplitter)
        return roundNumber.toString() + dlm + tablesString
    }
}