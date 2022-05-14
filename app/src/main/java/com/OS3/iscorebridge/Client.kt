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
    @Volatile var finished = false

    init{
        ClientHandler().start()
    }


    inner class ClientHandler() : Thread(){
        override fun run(){
            Looper.prepare()
            clientHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_READER_DISCONNECTED, MESSAGE_WRITER_DISCONNECTED -> {
                            handler.obtainMessage(MESSAGE_CLIENT_DISCONNECTED, this)
                        }
                        MESSAGE_READ -> {
                            val c = msg.obj as Communication
                            var forwardObj : Any? = msg.obj
                            var msgWhat = msg.what
                            when (c.purpose) {

                                MESSAGE_SEND_GAME -> {
                                    gameInfo.match.addGame(Game(c.msg))
                                }
                                MESSAGE_SEND_DEAL -> {

                                    forwardObj = Deal(c.msg)
                                    gameInfo.match.boards[forwardObj.number]!!.deal = forwardObj
                                }
                                MESSAGE_EDIT_GAME -> {
                                    forwardObj = Game(c.msg)
                                    gameInfo.match.boards[forwardObj.boardNumber]!!.getGame(forwardObj)!!.copy(forwardObj)
                                }

                                SENDJOINCOMPLETE -> {
                                    forwardObj = ClientInfo(c.msg)
                                    msgWhat = MESSAGE_UPDATE_CLIENT
                                }
                                CHECKCLIENTDETAILS -> {
                                    var clientInfo = ClientInfo(c.msg)
                                    clientInfo.currentTable.tableNumber = checkTable(clientInfo.currentTable.tableNumber)
                                    resolveClients(clientInfo)
                                    send(Communication(myInfo.deviceName, CHECKCLIENTDETAILS, clientInfo.toString()))
                                }
                                CHECKSPECTATORDETAILS ->{
                                    var spectatorInfo = SpectatorInfo(c.msg)
                                    spectatorInfo.confirmation = wifiHost.populate(spectatorInfo)
                                    send(Communication(myInfo.deviceName, CHECKSPECTATORDETAILS, spectatorInfo.toString()))
                                }
                                MATCHFINISHED -> {
                                    finished = true
                                }
                                REQUESTAUTHORISATION ->{
                                    var status = wifiHost.authorise(c.msg.toInt())
                                    send(Communication(myInfo.deviceName, REQUESTAUTHORISATION, status.toString()))
                                }
                                MESSAGE_DIRECTOR_CALL ->{
                                    forwardObj = this
                                }
                                CHANGEINFO ->{
                                    clientInfo = ClientInfo(c.msg)
                                }
                            }
                            handler.obtainMessage(msgWhat, forwardObj)
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
        return clientInfo?.currentTable?.tableNumber ?: 0
    }

    fun resolveClients(clientInfo : ClientInfo){
        playerList.populate(clientInfo.currentTable.pairNS.p1)
        playerList.populate(clientInfo.currentTable.pairEW.p1)
        playerList.populate(clientInfo.currentTable.pairNS.p2)
        playerList.populate(clientInfo.currentTable.pairEW.p2)
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