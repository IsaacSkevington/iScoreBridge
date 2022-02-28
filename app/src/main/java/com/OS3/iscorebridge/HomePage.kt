package com.OS3.iscorebridge

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


@Volatile lateinit var wifiManager : WifiP2pManager
class HomePage : Fragment() {
    val START = "START"
    val JOIN = "JOIN"
    var buttonPress : String = ""
    val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        activity!!.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                if(buttonPress == START){
                    findNavController().navigate(R.id.homeToStart)
                }
                else{

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
            activity!!.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), WIFI_PERMISSIONS_GRANTED)
        }


    }

    private fun setupWifi(){

        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        var channel = manager!!.initialize(context, activity!!.mainLooper, null)
        manager!!.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {

            }

            override fun onFailure(p0: Int) {

            }
        })



        wifiService = WifiService(manager!!, channel)
        activity!!.registerReceiver(wifiService, intentFilter)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                manager!!.requestDeviceInfo(channel, object : WifiP2pManager.DeviceInfoListener {
                    override fun onDeviceInfoAvailable(device: WifiP2pDevice?) {
                        deviceID = device!!.deviceName
                        if(buttonPress == JOIN){
                            findNavController().navigate(R.id.homeToJoin)
                        }
                        else{
                            findNavController().navigate(R.id.homeToStart)
                        }
                    }
                })
            }
            catch(e : SecurityException){

            }
        }
        else{
            if(buttonPress == START){
                findNavController().navigate(R.id.homeToStart)
            }
            else{
                findNavController().navigate(R.id.homeToJoin)
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