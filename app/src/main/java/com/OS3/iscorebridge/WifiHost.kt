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
    var clients = ArrayList<Client>()
    @Volatile var p2pClientList = ArrayList<String>()
    var currentPort = hostport + 1
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


    inner class ClientHandler() : Thread(){

        override fun run(){
            Looper.prepare()
            hostHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_READER_DISCONNECTED->{

                        }
                        MESSAGE_WRITER_DISCONNECTED->{

                        }
                        MESSAGE_READ ->{
                            var c = msg.obj as Communication
                            if (c.purpose == SENDGAME) {
                                var newGame = Game(c.msg)
                                match.addGame(newGame)
                                send(SENDGAME, newGame.toString())
                            }
                        }
                    }

                }
            }
            Looper.loop()
        }

    }

    public inner class ConnectThread(var device : WifiP2pDevice) : Thread() {


        override fun run(){

            do{}while(connecting)
            Log.d("Connecting", "Initialising connection to client")
            connecting = true
            connect()
        }

        fun sendConnectionData(socket: Socket){
            Log.d("Sending data", "Sending data to client")
            var clientPort = getNextPort()
            var clientAssignment = ClientAssignment(clientPort, clients.size)

            var message = Communication(deviceID, SENDCONNECTIONINFO, clientAssignment.toString())
            OneTimeWifiWriter(socket, hostHandler, message.toString())
            socket.close()
            connecting = false
            Log.d("Sending data", "Send successful")
            var c = Client(clientPort, clients.size, hostHandler)
            Log.d("Connecting client", "Initialising connection on port $clientPort")
            c.connect()
            Log.d("Connecting client", "Connection successful")
            clients.add(c)
            parentHandler.obtainMessage(MESSAGE_CLIENT_CONNECTED, device.deviceName).sendToTarget()
        }

        fun connect(){
            try {

                hostSocket = ServerSocket(hostport)
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
        var cls = ArrayList<String>()
        for(client in clients){
            cls.add(client.getAddress())
        }
        return cls
    }

    fun getNextPort() : Int{
        return currentPort++
    }

    fun send(purpose : Int, msg : String){
        var c = Communication(deviceID, purpose, msg)
        for(client in clients){
            client.writer.sendHandler.obtainMessage(MESSAGE_WRITE, STRING, -1, c.toString()).sendToTarget()
        }
    }


    fun startGame(parentHandler: Handler){
        send(
            SENDSTART,
            gameInfo.toString()
        )
        parentHandler.obtainMessage(MESSAGE_START, gameInfo).sendToTarget()
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










}