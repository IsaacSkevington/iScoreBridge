package com.OS3.iscorebridge

const val GAMEMODE_TEAMS = 0
const val GAMEMODE_PAIRS = 1

class GameInfo {

    private var tables : Int
    var movement : Movement
    var movementType : Int
    var gameMode : Int
    var clientList : ArrayList<String>
    var arrowSwitch : Boolean
    var shareAndRelay : Boolean
    private var boards : Int

    val dlm = "&&&&"

    constructor(tables: Int, gameMode : Int, boards : Int, movementType : Int, arrowSwitch: Boolean, shareAndRelay : Boolean, clientList : ArrayList<String>){
        this.tables = tables
        this.gameMode = gameMode
        this.boards = boards
        this.movement = Movement(tables, gameMode, boards, movementType, arrowSwitch, shareAndRelay)
        this.movementType = movementType
        this.clientList = clientList
        this.arrowSwitch = arrowSwitch
        this.shareAndRelay = shareAndRelay
    }
    
    constructor(s : String){
        val params = s.split(dlm)
        this.tables = params[0].toInt()
        this.gameMode = params[1].toInt()
        this.boards = params[2].toInt()
        this.movement = Movement(params[3])
        this.movementType = params[4].toInt()
        val cl = params[4].split(", ")
        this.clientList = ArrayList()
        for(client in cl){
            clientList.add(client)
        }
        this.arrowSwitch = params[5].toBoolean()
        this.shareAndRelay = params[6].toBoolean()

    }



    fun setupTable(tableNumber: Int) : Table{
        return if(movementType == MOVEMENT_NONE){
            Table(tableNumber, arrayListOf(0), 0, 0)
        } else{
            movement.rounds[1]!!.tables[tableNumber]!!
        }
    }

    fun getNextBoard(round: Int, tableNumber: Int) : Int{
        return movement.rounds[round]!!.tables[tableNumber]!!.getNextBoard()
    }

    override fun toString(): String {
        var clientListString = clientList.toString()
        clientListString = clientListString.substring(1, clientListString.length - 1)
        return tables.toString() + dlm + gameMode.toString() + dlm + boards.toString() + dlm + movement.toString() + dlm + movementType.toString() + dlm + clientListString + dlm + arrowSwitch.toString() + dlm + shareAndRelay.toString()
    }

}