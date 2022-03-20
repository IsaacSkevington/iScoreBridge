package com.OS3.iscorebridge

import android.net.wifi.p2p.WifiP2pManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket


val ME = "HOSTCONNECT"
class WifiHost(var manager : WifiP2pManager, var channel : WifiP2pManager.Channel, @Volatile var parentHandler: Handler){
    
    @Volatile lateinit var hostHandler : Handler
    @Volatile lateinit var myWriter: WifiWriter
    var clients = ArrayList<String>()
    var serverSockets = ArrayList<ServerSocket>()
    @Volatile lateinit var myClient : Socket
    @Volatile var cancel = false
    init{
        ClientHandler().start()
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
                    }

                }
            }
            Looper.loop()
        }

    }

    public inner class ConnectThread() : Thread() {

        var clientIP: String = ""

        constructor(clientIP: String) : this() {
            this.clientIP = clientIP
        }

        override fun run(){
            if(clientIP == ""){
                connect()
            }
            else{
                connect(clientIP)
            }
        }

        fun connect(clientIP : String){
            var socket = Socket()
            socket.bind(null)
            socket.connect(InetSocketAddress(clientIP, hostport), 100000)
            sendStartupInfo(socket)
        }

        fun connect(){
            var serverSoc = ServerSocket(hostport)
            val writerSoc = serverSoc.accept()
            sendStartupInfo(writerSoc)
            serverSoc.close()

        }

        fun sendStartupInfo(soc : Socket){
            var clientAssignment: String = if (clients.size == 0) {
                ME
            } else {
                clients[clients.size - 1]
            }

            var ca = ClientAssignment(clientAssignment, clients.size + 1)
            var c = Communication(deviceID, MESSAGE_CONNECTDEVICE, ca.toString())
            OneTimeWifiWriter(soc, hostHandler, c.toString())

            if (clients.size == 0) {
                wifiService.connectWriter()
            }
            clients.add(soc.inetAddress.hostAddress)
            parentHandler.obtainMessage(MESSAGE_CLIENT_CONNECTED, clients.size + 1, -1, soc.inetAddress.hostName).sendToTarget()
            soc.close()
        }
    }

    inner class StartThread(var parentHandler: Handler, var serviceHandler: Handler, var clientIP: String) : Thread(){


        override fun run(){
            var socket : Socket
            while(true){
                try {
                    socket = Socket()
                    socket.bind(null)
                    socket.connect(InetSocketAddress(clientIP, getReaderPort(clientNumber)), 20000)
                    break
                }
                catch (e : ConnectException){

                }
            }
            wifiService.addReader(WifiReader(socket, parentHandler, serviceHandler))

        }

    }

    fun startGame(parentHandler: Handler, serviceHandler: Handler){
        wifiService.send(
            SENDSTART,
            gameInfo.toString()
        )
        StartThread(parentHandler, serviceHandler, clients[clients.size - 1]).start()

    }










}