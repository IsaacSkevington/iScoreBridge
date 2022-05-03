package com.OS3.iscorebridge

class SpectatorInfo {

    val dlm = "%$%"

    var tableNumber : Int
    var playerNumber : Int
    var cardinality : Int
    var confirmation : Boolean

    constructor(string : String){
        var params = string.split(dlm)
        this.tableNumber = params[0].toInt()
        this.playerNumber = params[1].toInt()
        this.cardinality = params[2].toInt()
        this.confirmation = params[3].toBoolean()

    }

    constructor(tableNumber : Int, playerNumber : Int, cardinality: Int){
        this.tableNumber = tableNumber
        this.playerNumber = playerNumber
        this.cardinality = cardinality
        this.confirmation = false

    }

    override fun toString(): String {
        return this.tableNumber.toString() + dlm + this.playerNumber.toString() + dlm + this.cardinality.toString() + dlm + this.confirmation.toString()
    }

}