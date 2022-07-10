package com.OS3.iscorebridge


class Table {
    val dlm = "&"
    var tableNumber : Int
    var boards : ArrayList<Int>
    var pairNS : PlayerPair
    var pairEW : PlayerPair

    constructor(tableNumber: Int){
        this.tableNumber = tableNumber
        this.boards = ArrayList<Int>()
        this.pairNS = PlayerPair()
        this.pairEW = PlayerPair()
    }

    constructor() : this(0)

    constructor(tableNumber : Int, boards: ArrayList<Int>, pairNS : PlayerPair, pairEW : PlayerPair){

        this.boards = boards
        this.pairNS = pairNS
        this.pairEW = pairEW
        this.tableNumber = tableNumber
    }
    constructor(tableNumber: Int, pairNS: PlayerPair, pairEW: PlayerPair) : this(tableNumber, ArrayList(), pairNS, pairEW)

    constructor(s : String){
        val table = s.split(dlm)
        this.boards = ArrayList()
        if(table[0] != "") {
            val b = table[0].split(", ")
            for (board in b) {
                boards.add(board.toInt())
            }
        }
        this.pairNS = PlayerPair(table[1])
        this.pairEW = PlayerPair(table[2])
        this.tableNumber = table[3].toInt()
    }

    fun boardRange() : String{
        return if(boards.size > 1){
            "${boards.first()} - ${boards.last()}"
        } else{
            boards.first().toString()
        }
    }

    fun getNextBoard() : Int{
        var played = gameInfo.match.getBoards(pairNS, pairEW)
        if(played.size == boards.size){
            return 0
        }
        else{
           boards.forEach {
               if(!played.contains(it)){
                   return it
               }
           }
        }
        return 0
    }



    fun switchPairs(){
        var temp = pairNS
        pairNS = pairEW
        pairEW = temp
    }

    override fun toString() : String{
        var boardListString = boards.toString()
        boardListString = boardListString.substring(1, boardListString.length - 1)
        return boardListString + dlm + pairNS.toString() + dlm + pairEW.toString() + dlm + tableNumber.toString()
    }
}