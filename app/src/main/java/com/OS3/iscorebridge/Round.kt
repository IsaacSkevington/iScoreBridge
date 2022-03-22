package com.OS3.iscorebridge

class Round {
    private var tables : ArrayList<Table>
    var roundNumber : Int
    var dlm = "&&"


    constructor(roundNumber : Int){
        tables = ArrayList()
        this.roundNumber = roundNumber
    }

    constructor(s : String){
        val params = s.split(dlm)
        this.roundNumber = params[0].toInt()
        this.tables = ArrayList()
        for(table in params[1].split(", ")){
            tables.add(Table(table))
        }
    }

    override fun toString(): String {
        var tablesString = tables.toString()
        tablesString = tablesString.substring(1, tablesString.length - 1)
        return roundNumber.toString() + dlm + tablesString
    }
}