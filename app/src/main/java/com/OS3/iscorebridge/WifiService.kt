package com.OS3.iscorebridge

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.RequiresApi
import java.net.ServerSocket
import java.util.*


@Volatile lateinit var wifiService : WifiService

val hostport = 8888
val readwriteport1 = 8889
var readwriteport2 = 8890

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


fun connectWifi(address : String, manager: WifiP2pManager, channel: WifiP2pManager.Channel){
    var info = WifiP2pConfig().apply{
        this.deviceAddress = address
    }
    try {
        manager.connect(channel, info, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {

            }

            override fun onFailure(reason: Int) {
                if(reason == WifiP2pManager.BUSY || reason == WifiP2pManager.ERROR) {
                    connectWifi(address, manager, channel)
                }
            }
        })
    }
    catch (e : SecurityException){

    }
}





const val MESSAGE_SEND = 6
const val MESSAGE_SEND_MATCH = 11
const val MESSAGE_READ_MATCH = 12
const val ENABLE_DISCOVERABLE = 4
const val MESSAGE_MATCHUPDATE = 5



class WifiService(val manager : WifiP2pManager, val channel : WifiP2pManager.Channel) : BroadcastReceiver(){

    @Volatile var discover = false
    @Volatile lateinit var clientList : ArrayList<String>
    @Volatile lateinit public var activity : Activity
    @Volatile lateinit var writer : WifiWriter
    @Volatile lateinit var reader : WifiReader
    @Volatile var writerSet : Boolean = false
    @Volatile var readerSet : Boolean = false
    @Volatile lateinit var childHandler : Handler
    lateinit var readerID : String
    @Volatile lateinit var parentHandler : Handler
    @Volatile lateinit var serviceHandler : Handler
    @Volatile var connecting = false

    init{
        RunningThread().start()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action!!) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {

            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                if(discover) {
                    try {
                        manager.requestPeers(channel) { peers: WifiP2pDeviceList ->
                            var peersList = peers.deviceList
                            for (device in peersList) {
                                if (encodeID(device.deviceName) == readerID || device.deviceAddress == readerID) {
                                    ConnectReaderThread(device.deviceAddress).start()
                                    discover = false
                                }
                            }

                        }
                    } catch (e: SecurityException) {

                    }
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val info: WifiP2pInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
                if (amHost) {
                    if (info!!.groupFormed) {
                        val info: WifiP2pGroup? =
                            intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP)
                        var connectionAddress = info!!.owner.deviceAddress
                        var config = WifiP2pConfig()
                        config.deviceAddress = connectionAddress
                        try {
                            manager.connect(
                                channel,
                                config,
                                object : WifiP2pManager.ActionListener {
                                    override fun onSuccess() {

                                    }

                                    override fun onFailure(reason: Int) {

                                    }
                                })
                        } catch (e: SecurityException) {

                        }
                    }
                } else {
                    if(info!!.groupFormed) {
                        var address = info!!.groupOwnerAddress.hostAddress.toString()
                        if (!readerSet) {
                            var handler = object : Handler(Looper.myLooper()!!){
                                override fun handleMessage(msg: Message) {
                                    when(msg.what){
                                        MESSAGE_ONETIMEREADER_DATAAVAILABLE->{
                                            var communication = msg.obj as Communication
                                            var assignment = ClientAssignment(communication.msg)
                                            clientNumber = assignment.myNumber
                                            reader = if (assignment.connectionAddress == ME) {
                                                WifiReader(parentHandler, address, serviceHandler, getReaderPort(clientNumber))
                                            } else {
                                                WifiReader(parentHandler, assignment.connectionAddress, serviceHandler, getReaderPort(clientNumber))
                                            }
                                            while (!reader.connected) {
                                            }
                                            readerSet = true
                                            if (writerSet) {
                                                reader.forwardHandlerSet(writer.sendHandler)
                                            }
                                            parentHandler.obtainMessage(MESSAGECONNECTEDREADER).sendToTarget()
                                        }

                                    }
                                }
                            }
                            OneTimeWifiReader(handler, address, serviceHandler, hostport).start()

                        } else {
                            reader = WifiReader(parentHandler, address, serviceHandler, getReaderPort(clientNumber))
                            while (!reader.connected) {
                            }
                            if (writerSet) {
                                reader.forwardHandlerSet(writer.sendHandler)
                            }
                            readerSet = true
                            parentHandler.obtainMessage(MESSAGECONNECTEDREADER).sendToTarget()

                        }

                    }
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                //val device: WifiP2pDevice? = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                //deviceID = device!!.deviceName
            }
        }

    }







    fun send(purpose : Int, msg : String){
        var c = Communication(deviceID, purpose, msg)
        writer.sendHandler.obtainMessage(MESSAGE_WRITE, STRING, -1, c.toString()).sendToTarget()
    }


    inner class RunningThread() : Thread(){
        public override fun run(){
            Looper.prepare()
            serviceHandler = object : Handler(Looper.myLooper()!!) {
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


    public fun setHandler(handler: Handler){
        this.parentHandler = handler
        if(readerSet) {
            this.reader.handler = parentHandler
        }
    }

    inner class ConnectWriterThread() : Thread(){
        public override fun run(){
            initialiseWriter()
        }
        public fun initialiseWriter(){
            var serverSoc = ServerSocket(getWriterPort(clientNumber))
            val writerSoc = serverSoc.accept()
            writer = WifiWriter(writerSoc, serviceHandler)
            while (!writer.sendHandlerSet) {

            }
            writerSet = true
            if(readerSet) {
                reader.forwardHandlerSet(writer.sendHandler)
            }
            parentHandler.obtainMessage(MESSAGECONNECTEDWRITER).sendToTarget()
        }
    }





    private inner class ConnectReaderThread(val address : String) : Thread() {

        public override fun run() {
            var info = WifiP2pConfig()
            info.deviceAddress = address
            try {
                manager.connect(channel, info, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                    }
                    override fun onFailure(reason: Int) {
                    }
                })
            }
            catch(e : SecurityException){

            }
        }
    }


    fun addReader(r : WifiReader){

        this.reader = r
        readerSet = true
    }


    fun addWriter(w : WifiWriter){
        writerSet = true
        this.writer = w
    }

    fun connectWriter(){
        ConnectWriterThread().start()
    }

    fun connectReader(readerID: String){
        this.readerID = readerID
        if(writerSet){
            if(wifiHost.clients.size == 1){
                reader = WifiReader(parentHandler, writer.socket.inetAddress.hostAddress, serviceHandler, getReaderPort(0))
                while (!reader.connected) {
                }
                readerSet = true
                reader.forwardHandlerSet(writer.sendHandler)
                parentHandler.obtainMessage(MESSAGECONNECTEDREADER).sendToTarget()
                return
            }
        }
        try {
            discover = true
            connecting = true
            manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onSuccess() {

                }

                override fun onFailure(reason: Int) {
                }
            })
        } catch (e: SecurityException) {

        }
    }



}