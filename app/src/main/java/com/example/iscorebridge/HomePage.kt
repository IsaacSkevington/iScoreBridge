package com.example.iscorebridge

import android.Manifest
import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController



/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

@Volatile lateinit var bluetoothAdapter : BluetoothAdapter
class HomePage : Fragment() {
    val START = "START"
    val JOIN = "JOIN"
    var buttonPress : String = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                findNavController().navigate(R.id.homeToStart)
            }
            else{
                Toast.makeText(context, "Bluetooth permissions incorrect", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == BLUETOOTH_PERMISSIONS_GRANTED){
            setupBluetooth()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupPermissions(){
        if(activity!!.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED && activity!!.checkSelfPermission(Manifest.permission.BLUETOOTH) == PERMISSION_GRANTED){
            setupBluetooth()
        }
        else{
            activity!!.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH), BLUETOOTH_PERMISSIONS_GRANTED)
        }


    }

    private fun setupBluetooth(){
        bluetoothAdapter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            var btm = context!!.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            btm.adapter

        } else {
            BluetoothAdapter.getDefaultAdapter()
        }
        if(bluetoothAdapter == null){
            return
        }
        if (!bluetoothAdapter?.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
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