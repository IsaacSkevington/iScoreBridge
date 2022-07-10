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

open class WifiClient(var parentHandler: Handler){

    @Volatile lateinit  var connectionHandler : Handler
    @Volatile lateinit var reader: WifiReader
    @Volatile lateinit var writer: WifiWriter
    @Volatile var connectionHandlerSet = false
    @Volatile var connecting = false
    @Volatile var clientPort : Int = 0
    @Volatile var responsePending = false
    @Volatile var response : Pair<Int, (Communication)->Unit> = Pair(-1, fun(_: Communication){})


    init{
        setupMessageHandler()
    }

    fun setHandler(handler: Handler){
        parentHandler = handler
    }


    fun setupMessageHandler(){
        connectionHandler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_READER_DISCONNECTED->{ }
                    MESSAGE_WRITER_DISCONNECTED->{ }
                    MESSAGE_READ ->{
                        var forwardObj : Any? = msg.obj
                        val c = msg.obj as Communication
                        var msgWhat = c.purpose
                        if(responsePending && c.purpose == response.first){
                                responsePending = false
                                response.second(c)
                        }
                        when(c.purpose){
                            MESSAGE_SEND_GAME ->
                                forwardObj = Game(c.msg).also { if (c.deviceID != myInfo.deviceName) gameInfo.match.addGame(it) }
                            MESSAGE_SEND_DEAL ->
                                forwardObj = Deal(c.msg).also{gameInfo.match.boards[it.number]!!.deal = it}
                            MESSAGE_EDIT_GAME ->
                                forwardObj = Game(c.msg).also{ gameInfo.match.boards[it.boardNumber]!!.getGame(it)!!.copy(it)}
                            MESSAGE_START ->
                                forwardObj = GameInfo(c.msg).also {
                                    myInfo.myPair = it.movement.getTable(1, myInfo.currentTable.tableNumber).pairNS
                                    wifiService.clientList = it.clientList
                                    gameInfo = it
                                }
                            MATCHFINISHED -> myInfo.finished = true
                        }
                        parentHandler.obtainMessage(msgWhat, forwardObj).sendToTarget()
                    }
                }

            }
        }
        connectionHandlerSet = true
    }


    inner class ConnectionThread(var hostID: String) : Thread(){
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
    }



    open fun send(purpose : Int, msg : String){
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


    fun sendForResponse(purpose : Int, msg : String, onResponse : (response : Communication)->Unit){
        responsePending = true
        response = Pair(purpose, onResponse)
        send(purpose, msg)
    }


    fun kill(){
        reader.kill()
        writer.kill()
    }
}