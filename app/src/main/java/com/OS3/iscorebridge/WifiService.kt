package com.OS3.iscorebridge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.*
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi


@Volatile lateinit var wifiService : WifiService


@Volatile
var peers : ArrayList<WifiP2pDevice> = ArrayList()
@Volatile
var peersChanged = false
val peerListListener = WifiP2pManager.PeerListListener { peerList ->
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





class WifiService(@Volatile var parentHandler: Handler) : BroadcastReceiver(){

    @Volatile lateinit var clientList : ArrayList<String>
    lateinit var info : WifiP2pInfo
    @Volatile lateinit var manager : WifiP2pManager
    @Volatile lateinit var channel : WifiP2pManager.Channel

    constructor(manager : WifiP2pManager, channel : WifiP2pManager.Channel, parentHandler: Handler) : this(parentHandler){
        this.manager = manager
        this.channel = channel
    }

    fun setup(manager : WifiP2pManager, channel : WifiP2pManager.Channel){
        this.manager = manager
        this.channel = channel
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
                val info : WifiP2pInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
                val groupInfo : WifiP2pGroup? = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP)
                if(info != null) {
                    Log.d("Connection Changed", "The connection status changed")
                    if (wifiHostInitialised || wifiClientInitialised) {
                        Log.d("IP : ", info.groupOwnerAddress?.hostAddress + " Am group owner : " + info.isGroupOwner)
                        if (amHost) {
                            if (groupInfo != null) {
                                wifiHost.processGroupInfo(groupInfo)
                            }
                        } else {
                            if (groupInfo != null){
                                wifiClient.ConnectThread(info).start()

                            }
                        }
                    }
                }
            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device: WifiP2pDevice =
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)!!
                if(device.deviceName != myInfo.deviceName) {
                    myInfo.deviceName = device.deviceName
                    parentHandler.obtainMessage(MESSAGE_DEVICE_ID_CHANGED).sendToTarget()
                }
            }
        }

    }

    fun disconnect() {
        manager.removeGroup(channel, null)
    }



    fun connectP2p(address:String){
        val config = WifiP2pConfig().apply {
            deviceAddress = address
            wps.setup = WpsInfo.PBC
            groupOwnerIntent = 0
        }
        try {
            manager.connect(wifiService.channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d("Connection status", "Connection success")
                }

                override fun onFailure(p0: Int) {
                    Log.d("Connection status", "Connection failure")
                }

            })
        }
        catch(e : SecurityException){}
    }

    fun createGroup(){
        try {
            manager.createGroup(channel, null)
        }
        catch(e : SecurityException){}

    }

    fun discoverPeers(){
        try{
            manager.discoverPeers(channel, null)
        }
        catch(e : SecurityException){}
    }
    fun authorise(code : Int, onAuthorise : (Boolean) -> Unit){
        if(amHost) onAuthorise(true)
        else wifiClient.sendForResponse(REQUESTAUTHORISATION, code.toString()){
            onAuthorise(it.msg.toBoolean())
        }
    }

    fun send(type : Int, message: String){
        wifiClient.send(type, message)
    }

    fun kill(){
        if(amHost){
            wifiHost.kill()
        }
        wifiClient.kill()
        manager.removeGroup(channel, null)
    }


    inner class WifiDirectScanner() : Thread(){

        override fun run() {
            while(true) {
                try {
                    discoverPeers()
                } catch (e: SecurityException) {

                }
                sleep(5000)
            }
        }
    }

}