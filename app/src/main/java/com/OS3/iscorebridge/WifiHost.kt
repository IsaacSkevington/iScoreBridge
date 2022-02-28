package com.OS3.iscorebridge

import android.net.wifi.p2p.WifiP2pManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.net.ServerSocket
import java.net.Socket


val ME = "HOSTCONNECT"
class WifiHost(var manager : WifiP2pManager, var channel : WifiP2pManager.Channel, @Volatile var parentHandler: Handler) : Thread(){
    
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

    override fun run(){

        val txtListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomain, record, device ->

        }

        val servListener =
            WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, resourceType ->

            }

        try {
            manager.discoverServices(
                channel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        // Success!
                    }

                    override fun onFailure(code: Int) {
                    }
                }
            )
        }
        catch(e : SecurityException){

        }


        manager.setDnsSdResponseListeners(channel, servListener, txtListener)

        while(!cancel) {
            try {
                manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {

                    }

                    override fun onFailure(p0: Int) {

                    }
                })
            }
            catch(e : SecurityException){

            }
            var serverSoc = ServerSocket(hostport)
            serverSoc.use{
                val writerSoc = serverSoc.accept()
                var clientAssignment: String = if (clients.size == 0) {
                    ME
                } else {
                    clients[clients.size - 1]
                }
                parentHandler.obtainMessage(MESSAGE_CLIENT_CONNECTED, clients.size + 1, -1, writerSoc.inetAddress.canonicalHostName).sendToTarget()
                var ca = ClientAssignment(clientAssignment, clients.size + 1)
                var c = Communication(deviceID, MESSAGE_CONNECTDEVICE, ca.toString())
                OneTimeWifiWriter(writerSoc, hostHandler, c.toString())

                if(clients.size == 0){
                    wifiService.connectWriter()
                }
                clients.add(writerSoc.inetAddress.hostAddress)
            }
        }
    }




}