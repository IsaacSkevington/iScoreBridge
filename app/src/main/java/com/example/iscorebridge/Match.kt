package com.example.iscorebridge

import android.bluetooth.BluetoothDevice

@Volatile lateinit var match : Match

class Match {

    var boards : MutableMap<Int, Board?>
    var dlm = "||||"

    constructor(){
        boards = HashMap<Int, Board?>()
    }


    constructor(matchString: String){

        boards = HashMap<Int, Board?>()
        var boardsAsString = matchString.split(dlm)
        for(board in boardsAsString){
            var b = Board(board)
            boards[b.boardNumber] = b
        }

    }


    override fun toString(): String {
        var out = ""
        for(board in boards.values){
            out += board.toString() + dlm
        }
        return out.substring(0, out.length - dlm.length)
    }



    fun addGame(boardNumber: Int, pairNS: Int, pairEW: Int, suit: Char, trickNumbers: Int, tricksMade: Int, lead: String, declarer: Char, doubled: Boolean, redoubled: Boolean){
        if(!boards.containsKey(boardNumber)){
            boards[boardNumber] = Board(boardNumber)
        }
        boards[boardNumber]?.addGame(pairNS, pairEW, suit, trickNumbers, tricksMade, lead, declarer, doubled, redoubled)
    }

    fun merge(other : Match){
        for(board in other.boards){
            if(!this.boards.containsKey(board.key)){
                this.boards[board.key] = board.value
            }
            else{
                for(game in board.value!!.games){
                    if(!this.boards[board.key]!!.hasGame(game)){
                        this.boards[board.key]!!.addGame(game)
                    }
                }
            }
        }
    }




}