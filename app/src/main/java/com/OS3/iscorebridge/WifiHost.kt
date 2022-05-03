package com.OS3.iscorebridge

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import java.net.ServerSocket
import java.net.Socket

@Volatile lateinit var wifiHost: WifiHost
var wifiHostInitialised = false

class WifiHost(@Volatile var parentHandler: Handler){
    
    @Volatile lateinit var hostHandler : Handler
    @Volatile var clients = ArrayList<Client>()
    @Volatile var p2pClientList = ArrayList<String>()
    private var currentPort = HOSTPORT + 1
    lateinit var hostSocket : ServerSocket
    lateinit var hostConnectedSocket : Socket
    @Volatile var connecting = false
    init{
        ClientHandler().start()
    }

    fun processGroupInfo(groupInfo: WifiP2pGroup){
        for(client in groupInfo.clientList){
            if(!p2pClientList.contains(client.deviceAddress)){
                p2pClientList.add(client.deviceAddress)
                ConnectThread(client).start()
            }
        }
    }

    fun hasTable(table : Int) : Boolean{
        clients.forEach {
            if(it.getTableNumber() == table){
                return true
            }
        }
        return false
    }

    fun removeClient(c : Client){
        c.kill()
        clients.remove(c)
    }


    inner class StatusHandler : Thread(){

        fun allClientsFinished() : Boolean{
            clients.forEach {
                if(!it.finished){
                    return false
                }
            }
            return true
        }

        override fun run() {
            while(true){
                if(allClientsFinished()){
                    break
                }
                sleep(10000)
            }
            send(MATCHFINISHED, "")
        }
    }

    inner class ClientHandler() : Thread(){

        override fun run(){
            Looper.prepare()
            hostHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {

                    when (msg.what) {
                        MESSAGE_CLIENT_DISCONNECTED->{
                            removeClient(msg.obj as Client)
                            parentHandler.obtainMessage(MESSAGE_UPDATE_CLIENT).sendToTarget()
                        }
                        MESSAGE_UPDATE_CLIENT ->{
                            parentHandler.obtainMessage(MESSAGE_UPDATE_CLIENT).sendToTarget()
                        }

                        MESSAGE_SEND_DEAL ->{
                            val newDeal = msg.obj as Deal
                            send(SENDGAME, newDeal.toString())
                        }
                        MESSAGE_EDIT_GAME -> {
                            val newGame = msg.obj as Game
                            send(SENDEDITGAME, newGame.toString())
                        }
                        MESSAGE_SEND_GAME ->{
                            val newGame = msg.obj as Game
                            send(SENDGAME, newGame.toString())
                        }
                        else -> {
                            parentHandler.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj).sendToTarget()
                        }

                    }

                }
            }
            Looper.loop()
        }

    }

    inner class ConnectThread(private var device : WifiP2pDevice) : Thread() {


        override fun run(){

            do{}while(connecting)
            Log.d("Connecting", "Initialising connection to client")
            connecting = true
            connect()
        }

        private fun sendConnectionData(socket: Socket){
            Log.d("Sending data", "Sending data to client")
            val clientPort = getNextPort()
            val clientAssignment = ClientAssignment(clientPort, clients.size)

            val message = Communication(MYINFO.deviceName, SENDCONNECTIONINFO, clientAssignment.toString())
            OneTimeWifiWriter(socket, hostHandler, message.toString())
            socket.close()
            connecting = false
            Log.d("Sending data", "Send successful")
            val c = Client(clientPort, clients.size, hostHandler)
            Log.d("Connecting client", "Initialising connection on port $clientPort")
            c.connect()
            Log.d("Connecting client", "Connection successful")
            clients.add(c)
        }

        private fun connect(){
            try {

                hostSocket = ServerSocket(HOSTPORT)
                Log.d("Connecting", "Server socket created")
            }
            catch(e : Exception){
                Log.e("Connection Error", "Server socket crashed")
                return
            }
            hostConnectedSocket = hostSocket.accept()
            Log.d("Connecting", "Server socket accepted")
            hostSocket.close()
            sendConnectionData(hostConnectedSocket)
        }

    }

    fun getClientAddresses() : ArrayList<String>{
        val cls = ArrayList<String>()
        for(client in clients){
            cls.add(client.getAddress())
        }
        return cls
    }

    fun getNextPort() : Int{
        return currentPort++
    }

    fun send(purpose : Int, msg : String){
        val c = Communication(MYINFO.deviceName, purpose, msg)
        for(client in clients){
            client.send(c)
        }
    }


    fun startGame(parentHandler: Handler){
        send(
            SENDSTART,
            gameInfo.toString()
        )
        parentHandler.obtainMessage(MESSAGE_START, gameInfo).sendToTarget()
        StatusHandler().start()
    }

    fun getTables() : ArrayList<Int>{
        var list = ArrayList<Int>()
        clients.forEach {
            var number = it.getTableNumber()
            if(number != 0) {
                list.add(number)
            }
        }
        list.sort()
        return list
    }

    fun kill(){
        for(client in clients){
            client.kill()
        }
        try{
            hostSocket.close()
        }
        catch(e:Exception){}
        try{
            hostConnectedSocket.close()
        }
        catch(e:Exception){}

    }

    fun findClient(tableNumber : Int) : Client?{
        clients.forEach {
            if(it.getTableNumber() == tableNumber){
                return it
            }
        }
        return null
    }

    fun populate(si : SpectatorInfo) : Boolean{
        var client = findClient(si.tableNumber) ?: return false
        try {
            if (si.cardinality == NORTHSOUTH) {
                if (si.playerNumber != client.clientInfo!!.north.id && si.playerNumber != client.clientInfo!!.south.id) {
                    return false
                }
                si.tableNumber = client.clientInfo!!.calculateNumber(si.playerNumber)
            } else {
                if (si.playerNumber != client.clientInfo!!.east.id && si.playerNumber != client.clientInfo!!.west.id) {
                    return false
                }
                si.tableNumber = client.clientInfo!!.calculateNumber(si.playerNumber)
            }
            return true
        }
        catch(e : Exception){
            return false
        }

    }










}