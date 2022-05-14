package com.OS3.iscorebridge

class SpectatorInfo {

    val dlm = "%$%"

    var pair : PlayerPair
    var playerNumber : Int
    var cardinality : Int
    var confirmation : Boolean

    constructor(string : String){
        var params = string.split(dlm)
        this.pair = PlayerPair(params[0])
        this.playerNumber = params[1].toInt()
        this.cardinality = params[2].toInt()
        this.confirmation = params[3].toBoolean()

    }

    constructor(pair : PlayerPair, playerNumber : Int, cardinality: Int){
        this.pair = pair
        this.playerNumber = playerNumber
        this.cardinality = cardinality
        this.confirmation = false

    }

    override fun toString(): String {
        return this.pair.toString() + dlm + this.playerNumber.toString() + dlm + this.cardinality.toString() + dlm + this.confirmation.toString()
    }

}