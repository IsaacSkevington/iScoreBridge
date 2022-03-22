package com.OS3.iscorebridge

const val MOVEMENT_MITCHELL = 0
const val MOVEMENT_HOWELL = 1
const val MOVEMENT_NONE = 2

class Movement {

    val dlm = "&&&"

    private var rounds : MutableMap<Int, Round>

    constructor(tables : Int, gameMode : Int, boards: Int, selectedMovement : Int){
        rounds = HashMap()
        createMovement(tables, gameMode, boards, selectedMovement)
    }
    constructor(s:String) {

        rounds = HashMap()
        if (s.isNotEmpty()) {
            val roundsString = s.split(dlm)
            for (round in roundsString) {
                val r = Round(round)
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


    private fun createMovement(tables : Int, boards : Int, gameMode : Int, movement : Int){
        if(movement == MOVEMENT_NONE){
            return
        }
        if(movement == MOVEMENT_HOWELL){

        }
        if(movement == MOVEMENT_MITCHELL){

        }
    }

    override fun toString(): String {
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