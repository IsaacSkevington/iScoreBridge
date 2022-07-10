package com.OS3.iscorebridge

import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.widget.LinearLayout
import android.widget.TextView

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

    fun nextRound(context: Context){
        currentRound++
        currentTable = gameInfo.movement.getTable(myPair, currentRound)
        infoTag.mainActivity.matchHandler.obtainMessage(MESSAGE_START_TIMER).sendToTarget()
        wifiService.send(CHANGEINFO, this.toString())
        infoTag.setMessage(myInfo.getRoundInfo())
        infoTag.setOnClickListener {
            var layout = LinearLayout(context).also {
                it.setPadding(20, 20, 20, 20)
                it.orientation = LinearLayout.VERTICAL
            }
            TextView(context).also{
                it.text = "Time left for round:"
                it.textSize = 20f
                it.typeface = Typeface.DEFAULT_BOLD
                layout.addView(it)
            }
            var timer = infoTag.mainActivity.roundTimer
            TextView(context).also {
                timer.show(it)
                layout.addView(it)
            }
            AlertDialog.Builder(context)
                .setTitle("Round details")
                .setView(layout)
                .setPositiveButton("Ok"){_, _ ->
                    timer.hide()
                }
                .create().show()

        }
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
                "Boards ${currentTable.boardRange()}"
    }

    fun getBoardMovement() : String{
        return gameInfo.movement.getBoardMovement(
            currentRound, currentTable)
    }

    fun firstRound(context: Context){
        nextRound(context)
    }

    fun setup(context: Context){
        firstRound(context)
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
        this.myPair = PlayerPair(params[1])
        this.currentTable = Table(params[2])
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