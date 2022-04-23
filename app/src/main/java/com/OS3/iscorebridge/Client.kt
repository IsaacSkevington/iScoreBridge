package com.OS3.iscorebridge

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.net.ServerSocket

class Client(private var port : Int, var number:Int, var handler : Handler){

    private lateinit var reader : WifiReader
    lateinit var writer: WifiWriter
    @Volatile private lateinit var clientHandler : Handler
    @Volatile private var clientHandlerReady = false
    @Volatile var clientInfo : ClientInfo? = null

    init{
        ClientHandler().start()
    }


    inner class ClientHandler() : Thread(){
        override fun run(){
            Looper.prepare()
            clientHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_READER_DISCONNECTED -> {
                            handler.obtainMessage(MESSAGE_CLIENT_DISCONNECTED, this)
                        }
                        MESSAGE_WRITER_DISCONNECTED -> {
                            handler.obtainMessage(MESSAGE_CLIENT_DISCONNECTED, this)
                        }
                        MESSAGE_READ -> {
                            val c = msg.obj as Communication

                            when (c.purpose) {
                                SENDGAME -> {
                                    val newGame = Game(c.msg)
                                    match.addGame(newGame)
                                    handler.obtainMessage(MESSAGE_SEND_GAME, newGame)
                                }
                                SENDNEWDEAL -> {

                                    var deal = Deal(c.msg)
                                    match.boards[deal.number]!!.deal = deal
                                    handler.obtainMessage(MESSAGE_SEND_DEAL, deal)
                                }
                                SENDEDITGAME -> {
                                    var game = Game(c.msg)
                                    match.boards[game.boardNumber]!!.getGame(game)!!.copy(game)
                                    handler.obtainMessage(MESSAGE_EDIT_GAME, game)
                                }
                                SENDCLIENTDETAILS -> {
                                    clientInfo = ClientInfo(c.msg)
                                    handler.obtainMessage(MESSAGE_UPDATE_CLIENT, clientInfo)
                                }
                                CHECKCLIENTDETAILS -> {
                                    var clientInfo = ClientInfo(c.msg)
                                    clientInfo.tableNumber = checkTable(clientInfo.tableNumber)
                                    resolveClients(clientInfo)
                                    send(Communication(MYINFO.deviceName, CHECKCLIENTDETAILS, clientInfo.toString()))
                                }
                            }
                        }
                    }
                }
            }
            clientHandlerReady = true
            Looper.loop()
        }
    }

    fun checkTable(table : Int) : Int{
        return if(wifiHost.hasTable(table)){
            0
        }
        else{
            table
        }
    }

    fun getTableNumber() : Int{
        return clientInfo?.tableNumber ?: 0
    }

    fun resolveClients(clientInfo : ClientInfo){
        playerList.populate(clientInfo.north)
        playerList.populate(clientInfo.east)
        playerList.populate(clientInfo.south)
        playerList.populate(clientInfo.west)
    }

    fun send(c : Communication){
        writer.sendHandler.obtainMessage(MESSAGE_WRITE, STRING, -1, c.toString()).sendToTarget()
    }

    fun connect(){
        val serverSoc = ServerSocket(port)
        val connectedSoc = serverSoc.accept()
        serverSoc.close()
        do{}while(!clientHandlerReady)
        reader = WifiReader(connectedSoc, clientHandler)
        writer = WifiWriter(connectedSoc, clientHandler)

    }

    fun getAddress() : String{
        return reader.socket.inetAddress.hostAddress!!
    }

    fun kill(){
        writer.kill()
        reader.kill()
    }


}