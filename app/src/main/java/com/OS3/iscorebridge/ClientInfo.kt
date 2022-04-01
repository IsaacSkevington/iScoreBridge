package com.OS3.iscorebridge

val MYINFO = ClientInfo()

class ClientInfo{


    val delim = "***"
    var deviceName : String
    var tableNumber : Int
    var north:Player
    var east:Player
    var south:Player
    var west:Player
    lateinit var client : Client

    constructor(clientName : String, tableNumber : Int, north:Player, east:Player, south:Player, west:Player){
        this.deviceName = clientName
        this.tableNumber = tableNumber
        this.north = north
        this.east = east
        this.south = south
        this.west = west
    }

    constructor(){
        deviceName = ""
        tableNumber = -1
        north = Player()
        east = Player()
        south = Player()
        west = Player()
    }

    constructor(string: String){
        var params = string.split(delim)
        this.deviceName = params[0]
        this.tableNumber = params[1].toInt()
        this.north = Player(params[2])
        this.east = Player(params[3])
        this.south = Player(params[4])
        this.west = Player(params[5])
    }

    override fun toString(): String {
        return deviceName + delim + tableNumber + delim + north.toString() + delim + east.toString() + delim + south.toString() + delim + west.toString() + delim
    }


}