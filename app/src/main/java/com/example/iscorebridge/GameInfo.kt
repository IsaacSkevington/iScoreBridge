package com.example.iscorebridge

val GAMEMODE_TEAMS = 0
val GAMEMODE_PAIRS = 1

class GameInfo {

    var tables : Int
    var movement : Movement
    var gameMode : Int
    var clientList : ArrayList<String>
    var boards : Int

    val dlm = "&&&&"

    constructor(tables: Int, gameMode : Int, boards : Int, movementType : Int, clientList : ArrayList<String>){
        this.tables = tables
        this.gameMode = gameMode
        this.boards = boards
        this.movement = Movement(tables, gameMode, boards, movementType)
        this.clientList = clientList
    }
    
    constructor(s : String){
        var params = s.split(dlm)
        this.tables = params[0].toInt()
        this.gameMode = params[1].toInt()
        this.boards = params[2].toInt()
        this.movement = Movement(params[3])
        var cl = params[4].split(", ")
        this.clientList = ArrayList<String>()
        for(client in cl){
            clientList.add(client)
        }
    }

    public override fun toString(): String {
        var clientListString = clientList.toString()
        clientListString = clientListString.substring(1, clientListString.length - 1)
        return tables.toString() + dlm + gameMode.toString() + dlm + boards.toString() + dlm + movement.toString() + dlm + clientListString
    }

}