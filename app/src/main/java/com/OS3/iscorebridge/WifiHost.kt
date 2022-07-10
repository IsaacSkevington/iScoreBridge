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
    
    @Volatile var hostHandler : Handler? = null
    @Volatile var clients = ArrayList<Client>()
    @Volatile var p2pClientList = ArrayList<String>()
    private var currentPort = HOSTPORT + 1
    lateinit var hostSocket : ServerSocket
    lateinit var hostConnectedSocket : Socket
    @Volatile var connecting = false
    @Volatile var authcode : Int = SETTINGS.getPin()
    init{
        ClientHandler().start()
        while(hostHandler == null){}
        var hostClient = HostClient(1, hostHandler!!)
        wifiClient = HostWifiClient(parentHandler, hostClient)
        clients.add(hostClient)

    }

    fun authorise(code : Int) : Boolean{
        return code == authcode
    }

    fun processGroupInfo(groupInfo: WifiP2pGroup){
        for(client in groupInfo.clientList){
            if(!p2pClientList.contains(client.deviceAddress)){
                p2pClientList.add(client.deviceAddress)
                ConnectThread(client).start()
            }
        }
    }

    fun setHandler(handler: Handler){
        parentHandler = handler
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
                    var msgWhat = msg.what
                    var forwardObj : Any = msg.obj
                    when (msg.what) {
                        MESSAGE_CLIENT_DISCONNECTED->{
                            removeClient(msg.obj as Client)
                        }
                        MESSAGE_SEND_DEAL ->{
                            send(msg.what, (msg.obj as Deal).toString())
                        }
                        MESSAGE_EDIT_GAME, MESSAGE_SEND_GAME -> {
                            send(msg.what, (msg.obj as Game).toString())
                        }
                    }
                    parentHandler.obtainMessage(msgWhat, forwardObj)

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

            val message = Communication(myInfo.deviceName, SENDCONNECTIONINFO, clientAssignment.toString())
            OneTimeWifiWriter(socket, hostHandler!!, message.toString())
            socket.close()
            connecting = false
            Log.d("Sending data", "Send successful")
            val c = Client(clientPort, clients.size, hostHandler!!)
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
        val c = Communication(myInfo.deviceName, purpose, msg)
        for(client in clients){
            client.send(c)
        }
    }


    fun getSkeletonTables() : ArrayList<Table>{
        var tables = ArrayList<Table>()
        clients.forEach {
            tables.add(it.clientInfo!!.currentTable)
        }
        return tables
    }

    fun startGame(){
        send(
            MESSAGE_START,
            gameInfo.toString()
        )
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

    fun findClient(pairNumber : Int) : Client?{
        clients.forEach {
            if(it.clientInfo?.currentTable?.pairNS?.displayNumber == pairNumber || it.clientInfo?.currentTable?.pairEW?.displayNumber == pairNumber){
                return it
            }
        }
        return null
    }

    fun nextRound(){
        send(MESSAGE_ROUND_COMPLETE, "")
    }

    fun populate(si : SpectatorInfo) : Boolean{
        var client = findClient(si.pair.displayNumber) ?: return false
        if(client.clientInfo!!.currentTable.pairNS != si.pair && client.clientInfo!!.currentTable.pairEW != si.pair){
            return false
        }
        try {
            if (si.cardinality == NORTHSOUTH) {
                if (si.playerNumber != client.clientInfo!!.currentTable.pairNS.p1.id && si.playerNumber != client.clientInfo!!.currentTable.pairNS.p2.id) {
                    return false
                }
            } else {
                if (si.playerNumber != client.clientInfo!!.currentTable.pairEW.p1.id && si.playerNumber != client.clientInfo!!.currentTable.pairEW.p2.id) {
                    return false
                }
            }
            return true
        }
        catch(e : Exception){
            return false
        }

    }










}