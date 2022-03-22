package com.OS3.iscorebridge

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.wifi.p2p.WifiP2pManager
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
private const val START = "START"
private const val JOIN = "JOIN"

class HomePage : Fragment() {

    var idSet = false
    var buttonPress : String = ""
    val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        activity!!.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }


    val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                if(buttonPress == START){
                    findNavController().navigate(R.id.homeToStart)
                }
                else{
                    Toast.makeText(context, "Bluetooth permissions incorrect", Toast.LENGTH_LONG).show()
                }

            }
            else{
                Toast.makeText(context, "Bluetooth permissions incorrect", Toast.LENGTH_LONG).show()
            }
        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == WIFI_PERMISSIONS_GRANTED){
            setupWifi()
        }
        else{
            Toast.makeText(context, "Permissions Denied, please try again", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupPermissions(){
        if(activity!!.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED){
            setupWifi()
        }
        else{
            this.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), WIFI_PERMISSIONS_GRANTED)
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
        val channel = manager!!.initialize(context, activity!!.mainLooper, null)
        wifiService = WifiService(handler)
        activity!!.registerReceiver(wifiService, intentFilter)
        wifiService.setup(manager!!, channel)
        wifiService.disconnect()
        wifiService.WifiDirectScanner().start()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            manager!!.requestDeviceInfo(channel) {
                if (!idSet) {
                    deviceID = it!!.deviceName
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
            findNavController().navigate(R.id.homeToStart)
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