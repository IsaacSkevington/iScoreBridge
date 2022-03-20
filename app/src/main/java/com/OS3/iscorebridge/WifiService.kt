package com.OS3.iscorebridge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.RequiresApi
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket


@Volatile lateinit var wifiService : WifiService
@Volatile lateinit var wifiClient : WifiClient
@Volatile public var clientConnected = false

@Volatile public var peers : ArrayList<WifiP2pDevice> = ArrayList<WifiP2pDevice>()
@Volatile public var peersChanged = false
public val peerListListener = WifiP2pManager.PeerListListener { peerList ->
    val refreshedPeers = peerList.deviceList
    if (refreshedPeers != peers) {
        peers.clear()
        peers.addAll(refreshedPeers)
    }

    if (peers.isEmpty()) {
        return@PeerListListener
    }
    peersChanged = true
}



fun getReaderPort(clientNumber : Int) : Int{
    return if(clientNumber % 2 == 0){
        readwriteport1
    }
    else{
        readwriteport2
    }
}
fun getWriterPort(clientNumber : Int) : Int{
    return if(clientNumber % 2 == 0){
        readwriteport2
    }
    else{
        readwriteport1
    }
}


class WifiService(val manager : WifiP2pManager, val channel : WifiP2pManager.Channel) : BroadcastReceiver(){

    @Volatile lateinit var clientList : ArrayList<String>
    @Volatile lateinit var writer : WifiWriter
    @Volatile lateinit var reader : WifiReader
    @Volatile var writerSet : Boolean = false
    @Volatile var readerSet : Boolean = false
    lateinit var readerID : String
    lateinit var info : WifiP2pInfo
    @Volatile lateinit var parentHandler : Handler
    @Volatile lateinit var serviceHandler : Handler

    init{
        RunningThread().start()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action!!) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {}
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                try {
                    manager.requestPeers(channel, peerListListener)
                }
                catch (e: SecurityException) {}
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO)!!
                if(amHost && info.groupFormed){
                    if(info.isGroupOwner){
                        wifiHost.ConnectThread().start()
                    }
                    else{
                        wifiHost.ConnectThread(info.groupOwnerAddress.hostAddress.toString()).start()
                    }
                    clientConnected = true
                }
                else if (info.groupFormed) {
                    if(info.isGroupOwner){
                        wifiClient.ConnectThread().start()
                    }
                    else{
                        wifiClient.ConnectThread(info.groupOwnerAddress.hostAddress.toString()).start()
                    }
                }
            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device: WifiP2pDevice =
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)!!
                deviceID = device!!.deviceName
            }
        }

    }

    fun processStartData(assignment : ClientAssignment, hostSoc : Socket){
        if (assignment.connectionAddress == ME) {
            ConnectReaderThread(hostSoc.inetAddress.hostAddress).start()
        } else {
            connectP2p(assignment.connectionAddress)
        }
    }

    fun connectP2p(address:String){
        val config = WifiP2pConfig().apply {
            deviceAddress = address
            wps.setup = WpsInfo.PBC
        }

        wifiService.manager.connect(wifiService.channel, config, null)
    }







    fun send(purpose : Int, msg : String){
        var c = Communication(deviceID, purpose, msg)
        writer.sendHandler.obtainMessage(MESSAGE_WRITE, STRING, -1, c.toString()).sendToTarget()
    }


    inner class RunningThread() : Thread(){
        public override fun run(){
            Looper.prepare()
            serviceHandler = object : Handler(Looper.myLooper()!!) {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_READER_DISCONNECTED -> {
                            clientNumber--;
                            if(clientNumber == -1){
                                connectReader(clientList[clientList.size - 2])
                            }
                            else if(clientNumber == 0){
                                connectReader(clientList[clientList.size - 1])
                            }
                            else {
                                connectReader(clientList[clientNumber - 2])
                            }
                        }
                        MESSAGE_WRITER_DISCONNECTED ->{
                            clientNumber--
                            if(clientNumber == clientList.size - 2){
                                connectReader(clientList[1])
                            }
                            else if(clientNumber == clientList.size - 3){
                                connectReader(clientList[0])
                            }
                            else {
                                connectReader(clientList[clientNumber - 2])
                            }
                        }
                    }

                }
            }
            if(readerSet) {
                reader.serviceHandler = serviceHandler
            }
            if(writerSet) {
                writer.serviceHandler = serviceHandler
            }
            Looper.loop()
        }
    }


    public inner class WifiDirectScanner() : Thread(){

        override fun run() {
            while(true) {
                try {
                    wifiService.manager.discoverPeers(wifiService.channel, null)
                } catch (e: SecurityException) {

                }
                sleep(5000)
            }
        }
    }

    public fun setHandler(handler: Handler){
        this.parentHandler = handler
        if(readerSet) {
            this.reader.handler = parentHandler
        }
    }

    inner class ConnectWriterThread() : Thread(){
        public override fun run(){
            var serverSoc = ServerSocket(getWriterPort(clientNumber))
            val writerSoc = serverSoc.accept()
            writer = WifiWriter(writerSoc, serviceHandler)
            while (!writer.sendHandlerSet) {}
            writerSet = true
            if(readerSet) {
                reader.forwardHandlerSet(writer.sendHandler)
            }
            parentHandler.obtainMessage(MESSAGECONNECTEDWRITER).sendToTarget()
        }
    }





    private inner class ConnectReaderThread(val address : String) : Thread() {

        public override fun run() {
            var readerSocket = Socket()
            readerSocket.bind(null)
            readerSocket.connect(InetSocketAddress(address, getReaderPort(clientNumber)), 10000)
            reader = WifiReader(readerSocket, parentHandler, serviceHandler)
            readerSet = true
            if(writerSet) {
                reader.forwardHandlerSet(writer.sendHandler)
            }
            parentHandler.obtainMessage(MESSAGECONNECTEDREADER).sendToTarget()
        }
    }


    fun addReader(r : WifiReader){

        this.reader = r
        readerSet = true
        if(writerSet){
            reader.forwardHandlerSet(writer.sendHandler)
        }
    }


    fun addWriter(w : WifiWriter){
        writerSet = true
        this.writer = w
        if(readerSet){
            reader.forwardHandlerSet(writer.sendHandler)
        }
    }

    fun connectWriter(){
        ConnectWriterThread().start()
    }

    fun kill(){
        try {
            reader.kill()
        }
        catch (e:Exception){}
        try {
            writer.kill()
        }
        catch (e:Exception){}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun connectReader(readerID: String){
        this.readerID = readerID
        if(writerSet){
            if(wifiHost.clients.size == 1){
                reader = WifiReader(writer.socket, parentHandler, serviceHandler)
                while (!reader.connected) {
                }
                readerSet = true
                reader.forwardHandlerSet(writer.sendHandler)
                parentHandler.obtainMessage(MESSAGECONNECTEDREADER).sendToTarget()
                return
            }
        }


    }



}