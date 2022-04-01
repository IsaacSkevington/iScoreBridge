package com.OS3.iscorebridge


class Table {
    val dlm = "&"
    var tableNumber : Int
    var boards : ArrayList<Int>
    var pairNS : Int
    var pairEW : Int

    constructor(tableNumber : Int, boards: ArrayList<Int>, pairNS : Int, pairEW : Int){

        this.boards = boards
        this.pairNS = pairNS
        this.pairEW = pairEW
        this.tableNumber = tableNumber
    }

    constructor(s : String){
        val table = s.split(dlm)
        val b = table[0].split(", ")
        this.boards = ArrayList()
        for(board in b){
            boards.add(board.toInt())
        }
        this.pairNS = table[1].toInt()
        this.pairEW = table[2].toInt()
        this.tableNumber = table[3].toInt()
    }
     fun getNextBoard() : Int{
         var played = match.getBoards(pairNS, pairEW)
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


    override fun toString() : String{
        var boardListString = boards.toString()
        boardListString = boardListString.substring(1, boardListString.length - 1)
        return boardListString + dlm + pairNS.toString() + dlm + pairEW.toString() + dlm + tableNumber.toString()
    }
}