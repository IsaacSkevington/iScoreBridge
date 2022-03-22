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

class WifiClient(var hostID : String, var parentHandler: Handler) : Thread(){

    @Volatile lateinit  var connectionHandler : Handler
    @Volatile lateinit var reader: WifiReader
    @Volatile lateinit var writer: WifiWriter
    @Volatile var connectionHandlerSet = false
    @Volatile var connecting = false
    @Volatile var clientPort : Int = 0


    init{
        ConnectionHandler().start()
    }

    fun setHandler(handler: Handler){
        parentHandler = handler
    }

    inner class ConnectionHandler() : Thread(){
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
                            var c = msg.obj as Communication
                            if (c.purpose == SENDGAME) {
                                if (c.deviceID != deviceID) {
                                    var newGame = Game(c.msg)
                                    match.addGame(newGame)
                                }
                            } else if (c.purpose == SENDSTART) {
                                var gameInfo = GameInfo(c.msg)
                                parentHandler.obtainMessage(MESSAGE_START, gameInfo).sendToTarget()

                            }
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
                if (peers[i] != null) {
                    if (encodeID(peers[i].deviceName) == hostID) {
                        device = peers[i]
                        connected = true
                        break
                    }
                }
            }
        }
        Log.d("Device search", "Device search complete, found device " + device.deviceName)
        wifiService.connectP2p(device.deviceAddress)
    }

    public fun send(purpose : Int, msg : String){
        var c = Communication(deviceID, purpose, msg)
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
        fun connect(){
            var connected = false
            for(i in 0..3) {
                Log.d("Connection", "Trying to contact $HOSTIP:$hostport. Time #$i")
                var soc = Socket()
                try {
                    soc.bind(null)
                    soc.connect(InetSocketAddress(HOSTIP, hostport), 10000)
                    Log.d("Connection", "Connection successful, preparing to receive data")
                    recieveStartData(soc)
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

        fun recieveStartData(soc : Socket){
            var otReader = OneTimeWifiReader(soc, connectionHandler)
            var assignment = ClientAssignment(otReader.data.msg)
            clientNumber = assignment.myNumber
            clientPort = assignment.port
        }

        fun connectNewPort(port : Int){
            var connected = false
            for(i in 0..3) {
                Log.d("Connection", "Trying to contact $HOSTIP:$port. Time #$i")
                var soc = Socket()
                try {
                    soc.bind(null)
                    soc.connect(InetSocketAddress(HOSTIP, port), 30000)
                    Log.d("Connection", "Connection successful, establishing permanent R/W link with host")
                    reader = WifiReader(soc, connectionHandler)
                    writer = WifiWriter(soc, connectionHandler)
                    connected = true
                    break;
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
                parentHandler.obtainMessage(MESSAGECONNECTEDHOST).sendToTarget()
            }
        }


        fun setHandler(handler: Handler){
            parentHandler = handler
        }

    }


    fun kill(){
        reader.kill()
        writer.kill()
    }
}