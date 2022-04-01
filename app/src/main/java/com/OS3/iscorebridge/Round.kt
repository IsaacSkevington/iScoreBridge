package com.OS3.iscorebridge

class Round {
    var tables : MutableMap<Int, Table> = HashMap<Int, Table>()
    var roundNumber : Int
    var dlm = "&&"


    constructor(roundNumber : Int){
        this.roundNumber = roundNumber
    }

    constructor(s : String){
        val params = s.split(dlm)
        this.roundNumber = params[0].toInt()
        for(table in params[1].split(", ")){
            var t = Table(table)
            tables[t.tableNumber] = t
        }
    }



    override fun toString(): String {
        var tableArray = ArrayList<Table>()
        tables.values.forEach {
            tableArray.add(it)
        }
        var tablesString = tableArray.toString()
        tablesString = tablesString.substring(1, tablesString.length - 1)
        return roundNumber.toString() + dlm + tablesString
    }
}