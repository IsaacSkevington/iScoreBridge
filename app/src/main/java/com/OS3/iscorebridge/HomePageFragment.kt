package com.OS3.iscorebridge

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.wifi.p2p.WifiP2pManager
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

private const val START = "START"
private const val JOIN = "JOIN"

class HomePage : Fragment() {

    var idSet = false
    var buttonPress : String = ""
    val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }


    val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }



    @RequiresApi(Build.VERSION_CODES.M)
    fun setupPermissions(){
        if(requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED){
            setupWifi()
        }
        else{
            this.registerForActivityResult(ActivityResultContracts.RequestPermission()){
                if(it) setupWifi()
                else Toast.makeText(requireContext(), "Permissions Denied", Toast.LENGTH_LONG).show()
            }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }


    }

    private fun setupWifi() {

        val handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_DEVICE_ID_CHANGED -> {
                        if(!idSet) {
                            idSet = true
                            next()
                        }
                    }
                }
            }
        }
        val channel = manager!!.initialize(context, requireActivity().mainLooper, null)
        manager!!.removeGroup(channel, null)
        wifiService = WifiService(handler)
        requireActivity().registerReceiver(wifiService, intentFilter)
        wifiService.setup(manager!!, channel)
        wifiService.disconnect()
        wifiService.WifiDirectScanner().start()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            manager!!.requestDeviceInfo(channel) {
                if (!idSet) {
                    MYINFO.deviceName = it!!.deviceName
                    idSet = true
                    next()
                }
            }
        }
    }

    fun next(){
        if (buttonPress == JOIN) {
            findNavController().navigate(R.id.homeToJoin)
        } else {
            if(playerList.load(PLAYERLISTFILE, requireContext())){
                Toast.makeText(requireContext(), "Player list loaded", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.homeToStart)
            }
            else{
                Toast.makeText(requireContext(), "Player list load failed", Toast.LENGTH_LONG).show()
            }

        }
    }



    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.playButton).setOnClickListener {
            buttonPress = START
            setupPermissions()

        }
        view.findViewById<Button>(R.id.joingamebutton).setOnClickListener {
            buttonPress = JOIN
            setupPermissions()
        }
    }
}