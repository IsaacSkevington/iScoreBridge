package com.example.iscorebridge

val MOVEMENT_MITCHELL = 0
val MOVEMENT_HOWELL = 1
val MOVEMENT_NONE = 2

class Movement {

    val dlm = "&&&"

    var rounds : MutableMap<Int, Round>

    constructor(tables : Int, gameMode : Int, selectedMovement : Int){
        rounds = HashMap<Int, Round>()
        createMovement(tables, gameMode, selectedMovement)
    }
    constructor(s:String){
        rounds = HashMap<Int, Round>()
        var roundsString = s.split(dlm)
        for(round in roundsString){
            var r = Round(round)
            rounds[r.roundNumber] = r
        }
    }


    fun createMovement(tables : Int, gameMode : Int, movement : Int){
        if(movement == MOVEMENT_NONE){
            return
        }
    }

    public override fun toString(): String {
        var out = ""
        for(round in rounds.values){
            out += round.toString() + dlm
        }
        out.substring(0, out.length - dlm.length)
        return out
    }
}