package com.OS3.iscorebridge


import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.annotation.RequiresApi
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException


@Volatile lateinit var wifiClient : WifiClient
var wifiClientInitialised = false

class WifiClient(private var hostID : String, var parentHandler: Handler) : Thread(){

    @Volatile lateinit  var connectionHandler : Handler
    @Volatile lateinit var reader: WifiReader
    @Volatile lateinit var writer: WifiWriter
    @Volatile var connectionHandlerSet = false
    @Volatile var connecting = false
    @Volatile var clientPort : Int = 0
    @Volatile var responsePending = false
    @Volatile var response : Pair<Int, Communication?> = Pair(-1, null)


    init{
        ConnectionHandler().start()
    }

    fun setHandler(handler: Handler){
        parentHandler = handler
    }

    inner class ConnectionHandler : Thread(){
        override fun run(){
            Looper.prepare()
            connectionHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_READER_DISCONNECTED->{

                        }
                        MESSAGE_WRITER_DISCONNECTED->{

                        }

                        MESSAGE_READ ->{
                            var forwardObj : Any? = msg.obj
                            var msgWhat = msg.what
                            val c = msg.obj as Communication
                            if(responsePending){
                                if(c.purpose == response.first){
                                    response.copy(c.purpose, c)
                                    responsePending = false
                                }
                            }
                            when(c.purpose){
                                MESSAGE_SEND_GAME ->  {
                                    if (c.deviceID != myInfo.deviceName) {
                                        gameInfo.match.addGame(Game(c.msg))
                                    }
                                }
                                MESSAGE_SEND_DEAL -> {
                                    var deal = Deal(c.msg)
                                    gameInfo.match.boards[deal.number]!!.deal = deal
                                }
                                MESSAGE_START -> {
                                    val inf = GameInfo(c.msg)
                                    for (player in gameInfo.players) {
                                        if(player == myInfo.myPair){
                                            myInfo.myPair = player
                                        }
                                    }
                                    wifiService.clientList = inf.clientList
                                    gameInfo = inf
                                    forwardObj = gameInfo

                                }
                                MATCHFINISHED -> {
                                    myInfo.finished = true
                                }
                            }
                            parentHandler.obtainMessage(msgWhat, forwardObj).sendToTarget()
                        }
                    }

                }
            }
            connectionHandlerSet = true
            Looper.loop()
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun run(){
        lateinit var device : WifiP2pDevice
        var connected = false
        while (!connected){
            for (i in 0 until peers.size) {
                if (encodeID(peers[i].deviceName) == hostID) {
                    device = peers[i]
                    connected = true
                    break
                }
            }
        }
        Log.d("Device search", "Device search complete, found device " + device.deviceName)
        wifiService.connectP2p(device.deviceAddress)
    }

    fun send(purpose : Int, msg : String){
        val c = Communication(myInfo.deviceName, purpose, msg)
        writer.sendHandler.obtainMessage(MESSAGE_WRITE, STRING, -1, c.toString()).sendToTarget()
    }

    inner class ConnectThread(var info : WifiP2pInfo) : Thread(){

        override fun run(){

            if(!connecting) {
                Log.d("Connection", "Connection initialising")
                connecting = true
                connect()
            }
            else{
                parentHandler.obtainMessage(MESSAGE_CONNECTION_FAILED).sendToTarget()
            }
        }
        private fun connect(){
            var connected = false
            for(i in 0..3) {
                Log.d("Connection", "Trying to contact $HOSTIP:$HOSTPORT. Time #$i")
                val soc = Socket()
                try {
                    soc.bind(null)
                    soc.connect(InetSocketAddress(HOSTIP, HOSTPORT), 10000)
                    Log.d("Connection", "Connection successful, preparing to receive data")
                    receiveStartData(soc)
                    Log.d("Connection", "Data successfully received")
                    connected = true
                    break
                } catch (e: ConnectException) {
                    Log.e("Connection error", e.toString())
                }
                catch(e : SocketTimeoutException){
                    Log.e("Connection error", e.toString())
                }
                finally {
                    Log.d("Connection", "Closing socket")
                    soc.close()
                }
                sleep(1000)

            }
            if(!connected) {
                parentHandler.obtainMessage(MESSAGE_CONNECTION_FAILED).sendToTarget()
            }
            else{
                connectNewPort(clientPort)
            }

        }

        private fun receiveStartData(soc : Socket){
            val otReader = OneTimeWifiReader(soc, connectionHandler)
            val assignment = ClientAssignment(otReader.data.msg)
            clientNumber = assignment.myNumber
            clientPort = assignment.port
        }

        private fun connectNewPort(port : Int){
            var connected = false
            for(i in 0..3) {
                Log.d("Connection", "Trying to contact $HOSTIP:$port. Time #$i")
                val soc = Socket()
                try {
                    soc.bind(null)
                    soc.connect(InetSocketAddress(HOSTIP, port), 30000)
                    Log.d("Connection", "Connection successful, establishing permanent R/W link with host")
                    reader = WifiReader(soc, connectionHandler)
                    writer = WifiWriter(soc, connectionHandler)
                    connected = true
                    break
                } catch (e: ConnectException) {
                    Log.e("Connection error", e.toString())
                    soc.close()
                }
                catch(e : SocketTimeoutException){
                    Log.e("Connection error", e.toString())
                    soc.close()
                }
                sleep(1000)

            }
            if(!connected) {
                parentHandler.obtainMessage(MESSAGE_CONNECTION_FAILED).sendToTarget()
            }
            else{
                parentHandler.obtainMessage(MESSAGE_CONNECTED_HOST).sendToTarget()
            }
        }


        fun setHandler(handler: Handler){
            parentHandler = handler
        }

    }

    fun sendForResponse(purpose : Int, msg : String) : Communication{
        val c = Communication(myInfo.deviceName, purpose, msg)
        responsePending = true
        response.copy(purpose, null)
        writer.sendHandler.obtainMessage(MESSAGE_WRITE, STRING, -1, c.toString()).sendToTarget()
        while(responsePending){}
        return response.second!!
    }


    fun kill(){
        reader.kill()
        writer.kill()
    }
}