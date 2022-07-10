package com.OS3.iscorebridge

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.net.ServerSocket

open class Client(private var port : Int, var number:Int, var handler : Handler) : Thread(){

    @Volatile private lateinit var reader : WifiReader
    @Volatile lateinit var writer: WifiWriter
    @Volatile
    lateinit var clientHandler : Handler
    @Volatile private var clientHandlerReady = false
    @Volatile var clientInfo : ClientInfo? = null
    @Volatile var finished = false



    init{
        start()
    }

    var messageMap = mutableMapOf<Int, (Communication)-> Any?>(
        MESSAGE_SEND_GAME to fun(c : Communication) : Any {
            return Game(c.msg)
        },
        MESSAGE_SEND_DEAL to fun(c : Communication) : Any {
            return Deal(c.msg)
        },
        MESSAGE_EDIT_GAME to fun(c : Communication) : Any {
            return Game(c.msg)
        },
        MESSAGE_JOIN_COMPLETE to fun(c : Communication) : Any {
            return ClientInfo(c.msg)
        },
        CHECKCLIENTDETAILS to fun(c : Communication) : Any {
            return ClientInfo(c.msg).also {
                it.currentTable.tableNumber =
                    checkTable(it.currentTable.tableNumber)
                resolveClients(it)
                send(
                    Communication(
                        myInfo.deviceName,
                        CHECKCLIENTDETAILS,
                        it.toString()
                    )
                )
            }
        },
        CHECKSPECTATORDETAILS to fun(c : Communication) : Any{
            return SpectatorInfo(c.msg).also {
                it.confirmation = wifiHost.populate(it)
                send(
                    Communication(
                        myInfo.deviceName,
                        CHECKSPECTATORDETAILS,
                        it.toString()
                    )
                )
            }
        },

        MATCHFINISHED to fun(c : Communication) : Any {
            finished = true
            return true
        },
        REQUESTAUTHORISATION to fun(c : Communication) : Any{
            return wifiHost.authorise(c.msg.toInt()).also {
                send(
                    Communication(
                        myInfo.deviceName,
                        REQUESTAUTHORISATION,
                        it.toString()
                    )
                )
            }
        },
        MESSAGE_DIRECTOR_CALL to fun(c: Communication) : Any{
            return this
        },
        CHANGEINFO to fun(c : Communication) : Any {
            return ClientInfo(c.msg).also {
                clientInfo = it
            }
        }
    )

    fun processMessage(msg : Message){
        when (msg.what) {
            MESSAGE_READER_DISCONNECTED, MESSAGE_WRITER_DISCONNECTED -> {
                handler.obtainMessage(MESSAGE_CLIENT_DISCONNECTED, this)
            }
            MESSAGE_READ -> {
                (msg.obj as Communication).also {
                    var msgWhat = msg.what
                    if (it.purpose == MESSAGE_JOIN_COMPLETE) {
                        msgWhat = MESSAGE_UPDATE_CLIENT
                    }
                    var forwardObj = messageMap[it.purpose]?.invoke(it) ?: msg.obj
                    handler.obtainMessage(msgWhat, forwardObj)
                }

            }
        }
    }


    override fun run() {
        Looper.prepare()
        clientHandler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                processMessage(msg)
            }
        }
        clientHandlerReady = true
        Looper.loop()
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

    open fun send(c : Communication){
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

    open fun getAddress() : String{
        return reader.socket.inetAddress.hostAddress!!
    }

    fun kill(){
        try{
            writer.kill()
        }
        catch(e : Exception){}
        try {
            reader.kill()
        }
        catch(e:Exception){}
    }


}