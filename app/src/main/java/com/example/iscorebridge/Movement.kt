package com.example.iscorebridge

val MOVEMENT_MITCHELL = 0
val MOVEMENT_HOWELL = 1
val MOVEMENT_NONE = 2

class Movement {

    val dlm = "&&&"

    var rounds : MutableMap<Int, Round>

    constructor(tables : Int, gameMode : Int, boards: Int, selectedMovement : Int){
        rounds = HashMap<Int, Round>()
        createMovement(tables, gameMode, boards, selectedMovement)
    }
    constructor(s:String) {

        rounds = HashMap<Int, Round>()
        if (s.isNotEmpty()) {
            var roundsString = s.split(dlm)
            for (round in roundsString) {
                var r = Round(round)
                rounds[r.roundNumber] = r
            }
        }
    }

    fun mitchell(tables : Int, boards : Int){
        if(tables % 2 == 0){

        }
        else{

        }
    }


    fun createMovement(tables : Int, boards : Int, gameMode : Int, movement : Int){
        if(movement == MOVEMENT_NONE){
            return
        }
        if(movement == MOVEMENT_HOWELL){

        }
        if(movement == MOVEMENT_MITCHELL){

        }
    }

    public override fun toString(): String {
        var out = ""
        for(round in rounds.values){
            out += round.toString() + dlm
        }
        if(rounds.values.isNotEmpty()){
            out.substring(0, out.length - dlm.length)
        }

        return out
    }
}