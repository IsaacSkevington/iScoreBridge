package com.OS3.iscorebridge

val MYINFO = ClientInfo()

class ClientInfo{

    val delim = "*****"
    var deviceName : String
    var tableNumber : Int
    var north:Player
    var east:Player
    var south:Player
    var west:Player
    var myNumber : Int
    var starredBoards = ArrayList<Board>()
    var finished : Boolean = false
    lateinit var client : Client


    fun addStarredBoard(board : Board){
        if(!starredBoards.contains(board)){
            starredBoards.add(board)
        }
    }
    fun removeStarredBoard(board : Board){
        starredBoards.remove(board)
    }
    fun isStarred(board : Board) : Boolean{
        return starredBoards.contains(board)
    }

    constructor(clientName : String, tableNumber : Int, north:Player, east:Player, south:Player, west:Player, myNumber : Int){
        this.deviceName = clientName
        this.tableNumber = tableNumber
        this.north = north
        this.east = east
        this.south = south
        this.west = west
        this.myNumber = myNumber
    }

    constructor(){
        deviceName = ""
        tableNumber = -1
        north = Player()
        east = Player()
        south = Player()
        west = Player()
        myNumber = 0
    }

    constructor(string: String){
        var params = string.split(delim)
        this.deviceName = params[0]
        this.tableNumber = params[1].toInt()
        this.north = Player(params[2])
        this.east = Player(params[3])
        this.south = Player(params[4])
        this.west = Player(params[5])
        this.myNumber = params[6].toInt()
    }


    fun calculateNumber(playerNumber : Int) : Int{
        return 0
    }
    override fun toString(): String {
        return deviceName + delim + tableNumber + delim + north.toString() + delim + east.toString() + delim + south.toString() + delim + west.toString() + delim + myNumber.toString()
    }

    fun finishMatch(){
        this.finished = true
        wifiService.send(MATCHFINISHED, "")
    }


}