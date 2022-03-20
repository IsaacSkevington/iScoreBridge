package com.OS3.iscorebridge

import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.RequiresApi
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket


class WifiClient(var hostID : String, var parentHandler: Handler) : Thread(){

    lateinit var childHandler : Handler
    lateinit @Volatile var connectionHandler : Handler
    @Volatile var connectionHandlerSet = false
    init{
        ConnectionHandler().start()
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
                    }

                }
            }
            connectionHandlerSet = true
            Looper.loop()
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun run(){
        while(!connectionHandlerSet){
        }
        lateinit var device : WifiP2pDevice
        var connected = false
        while (!connected){
            for (peer in peers) {
                if (encodeID(peer.deviceName) == hostID) {
                    device = peer
                    connected = true
                    break
                }
            }
        }
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        wifiService.manager.connect(wifiService.channel, config, null)
    }

    public inner class ConnectThread() : Thread(){

        var hostIP : String = ""
        constructor(hostIP: String) : this(){
            this.hostIP = hostIP
        }

        override fun run(){
            if(hostIP == ""){
                connect()
            }
            else{
                connect(hostIP)
            }
        }

        fun connect(hostIP : String){
            var soc = Socket()
            soc.bind(null)
            soc.connect(InetSocketAddress(hostIP, hostport))
            recieveStartData(soc)
        }

        fun connect(){
            var soc = ServerSocket(hostport)
            var readerSoc = soc.accept()
            recieveStartData(readerSoc)
        }

        fun recieveStartData(soc : Socket){
            Looper.prepare()
            var handler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_ONETIMEREADER_DATAAVAILABLE -> {

                        }

                    }
                }
            }
            var otReader = OneTimeWifiReader(soc, handler, wifiService.serviceHandler)
            var assignment = ClientAssignment(otReader.data.msg)
            clientNumber = assignment.myNumber
            wifiService.processStartData(assignment, soc)
        }


    }
    public inner class StartConnect() : Thread(){
        override fun run(){
            var socket = ServerSocket(getWriterPort(clientNumber))
            var writerSoc = socket.accept()
            var writer = WifiWriter(writerSoc, wifiService.serviceHandler)
            while(!writer.sendHandlerSet){}
            wifiService.addWriter(writer)
            wifiService.send(
                SENDSTART,
                gameInfo.toString()
            )
        }
    }

    public fun startGame(){
        StartConnect().start()
    }













    private fun decodeID(hostID: String): String {
        var b64 = Base64Int(hostID)
        var b16 = Base16Int(b64)
        return b16.toMAC()
    }
}