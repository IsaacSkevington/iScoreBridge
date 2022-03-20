package com.OS3.iscorebridge


class Table {
    val dlm = "&"
    var board : Int
    var pairNS : Int
    var pairEW : Int

    constructor(board: Int, pairNS : Int, pairEW : Int){
        this.board = board
        this.pairNS = pairNS
        this.pairEW = pairEW
    }

    constructor(s : String){
        var table = s.split(dlm)
        this.board = table[0].toInt()
        this.pairNS = table[1].toInt()
        this.pairEW = table[2].toInt()
    }
    override fun toString() : String{
        return board.toString() + dlm + pairNS.toString() + dlm + pairEW.toString()
    }
}