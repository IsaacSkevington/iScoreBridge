package com.OS3.iscorebridge

const val GAMEMODE_TEAMS = 0
const val GAMEMODE_PAIRS = 1

class GameInfo {

    var movement : Movement
    var gameMode : Int
    var clientList : ArrayList<String>
    var boards : Int
    var players : ArrayList<PlayerPair>
    var match : Match
    var roundTime : Time

    val dlm = "&&&&"

    constructor(tables : ArrayList<Table>, gameMode : Int, clientList : ArrayList<String>, roundTime: Time, skeleton: MovementSkeleton) {
        this.gameMode = gameMode
        this.boards = skeleton.getTotalBoards()
        this.movement = Movement(tables, skeleton)
        this.clientList = clientList
        this.players = getPlayers(tables)
        this.match = Match()
        this.roundTime = roundTime

    }

    
    constructor(s : String){
        val params = s.split(dlm)
        this.gameMode = params[0].toInt()
        this.boards = params[1].toInt()
        this.movement = Movement(params[2])
        val cl = params[3].split(", ")
        this.clientList = ArrayList()
        for(client in cl){
            clientList.add(client)
        }
        this.players = ArrayList()
        var pl = params[4].split(", ")
        for(player in pl){
            players.add(PlayerPair(player))
        }
        this.match = Match(params[5])
        this.roundTime = Time(params[6])

    }

    fun getPlayers(tables : ArrayList<Table>) : ArrayList<PlayerPair>{
        var ret = ArrayList<PlayerPair>()
        tables.forEach {
            ret.add(it.pairNS)
            ret.add(it.pairEW)
        }
        return ret
    }

    fun getNextBoard(round: Int, tableNumber: Int) : Int{
        return movement.rounds[round]!!.tables[tableNumber]!!.getNextBoard()
    }

    fun getTablesInPlay(roundNumber : Int) : ArrayList<Table>{
        return movement.getTablesInPlay(roundNumber)
    }


    fun getPlayerPair(pair : Int) : PlayerPair?{
        players.forEach {
            if(it.displayNumber == pair){
                return it
            }
        }
        return null
    }

    override fun toString(): String {
        var clientListString = clientList.toString()
        clientListString = clientListString.substring(1, clientListString.length - 1)
        var playerListString = players.toString()
        playerListString = playerListString.substring(1, clientListString.length - 1)
        return gameMode.toString() + dlm + boards.toString() + dlm + movement.toString() + dlm + dlm + clientListString + dlm + playerListString + dlm + match.toString() + dlm + roundTime.toString()
    }

}