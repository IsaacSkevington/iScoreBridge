package com.OS3.iscorebridge

import android.app.AlertDialog
import android.content.Context

val myInfo = ClientInfo()

class ClientInfo{

    val delim = "*****"
    var deviceName : String
    var myPair : PlayerPair
    var starredBoards = ArrayList<Board>()
    var currentRound : Int = 0
    var finished : Boolean = false
    var currentTable : Table
    lateinit var client : Client

    fun nextRound(){
        currentRound++
        currentTable = gameInfo.movement.getTable(myPair, currentRound)
        wifiService.send(CHANGEINFO, this.toString())
    }
    fun getFirstBoard() : Int{
        if(currentRound == 0){
            return 0
        }
        return currentTable.boards[0]
    }
    fun getNSMovement() : String{
        return gameInfo.movement.getNSMovement(currentRound, currentTable)
    }

    fun hasNextRound() : Boolean{
        return gameInfo.movement.rounds.size > currentRound
    }

    fun getEWMovement(): String{
        return gameInfo.movement.getEWMovement(currentRound, currentTable)
    }

    fun getRoundInfo() : String{
        return "Round ${currentRound}\n" +
                "Boards ${currentTable.boards.first()} to ${currentTable.boards.last()}"
    }

    fun getBoardMovement() : String{
        return gameInfo.movement.getBoardMovement(
            currentRound, currentTable)
    }

    fun setup(context: Context){
        var builder = AlertDialog.Builder(context)
        builder.setTitle("Pair Number")
            .setMessage("Pair Numbers Available!\n" +
                    "NS : ${currentTable.pairNS.displayNumber}\n" +
                    "EW : ${currentTable.pairEW.displayNumber}")
            .setPositiveButton("Ok"){_, _ ->}
            .create().show()
    }

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


    constructor(){
        deviceName = ""
        myPair = PlayerPair(0, Player(), Player())
        currentTable = Table()
    }

    constructor(string: String){
        var params = string.split(delim)
        this.deviceName = params[0]
        this.myPair = PlayerPair(params[5])
        this.currentTable = Table(params[6])
    }


    fun calculateNumber(playerNumber : Int) : Int{
        return 0
    }
    override fun toString(): String {
        return deviceName + delim + myPair.toString() + delim + currentTable.toString()
    }

    fun finishMatch(){
        this.finished = true
        wifiService.send(MATCHFINISHED, "")
    }


}