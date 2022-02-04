package com.example.iscorebridge

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.RequiresApi
import java.util.*


class BluetoothClient(var hostID : String, var parentHandler: Handler) : Activity(){

    lateinit var childHandler : Handler
    private fun connect(device : BluetoothDevice){
        var hostSoc = device.createRfcommSocketToServiceRecord(programUUID)
        Looper.prepare()
        childHandler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_READ -> {
                        var readBuf: ByteArray = msg.obj as ByteArray;
                        // construct a string from the valid bytes in the buffer
                        var correctConnectionID = String(readBuf, 0, msg.arg1);
                        bluetoothService = BluetoothService(correctConnectionID, bluetoothAdapter, bluetoothAdapter.address, parentHandler)
                        Looper.myLooper()!!.quit()
                    }
                }
            }
        }
        BluetoothReader(childHandler, hostSoc).getAsyncReader().start()
        Looper.loop()
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val pair = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if(encodeID(device.name) == hostID){
                        connect(device)
                    }

                }
            }
        }
    }


    public fun start(){
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            if(device.address == decodeID(this.hostID)){
                connect(device)
                return
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(pair, filter)



    }

    private fun decodeID(hostID: String): String {
        var b64 = Base64Int(hostID)
        var b16 = Base16Int(b64)
        return b16.toMAC()
    }
}