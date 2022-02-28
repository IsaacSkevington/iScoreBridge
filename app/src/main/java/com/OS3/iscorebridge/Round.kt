package com.OS3.iscorebridge

class Round {
    var tables : ArrayList<Table>
    var roundNumber : Int
    var dlm = "&&"


    constructor(roundNumber : Int){
        tables = ArrayList<Table>()
        this.roundNumber = roundNumber
    }

    constructor(s : String){
        var params = s.split(dlm)
        this.roundNumber = params[0].toInt()
        this.tables = ArrayList<Table>()
        for(table in params[1].split(", ")){
            tables.add(Table(table))
        }
    }

    public override fun toString(): String {
        var tablesString = tables.toString()
        tablesString = tablesString.substring(1, tablesString.length - 1)
        return roundNumber.toString() + dlm + tablesString
    }
}