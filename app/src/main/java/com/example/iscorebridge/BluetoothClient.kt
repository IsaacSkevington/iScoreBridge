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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*


class BluetoothClient(var hostID : String, var parentHandler: Handler){

    lateinit var childHandler : Handler
    fun connect(device : BluetoothDevice){
        var hostSoc = device.createRfcommSocketToServiceRecord(programUUID)
        childHandler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_READ -> {
                        var readBuf: ByteArray = msg.obj as ByteArray;
                        // construct a string from the valid bytes in the buffer
                        var correctConnectionID = String(readBuf, 0, msg.arg1);
                        bluetoothService = if(correctConnectionID == ME){
                            BluetoothService(device, parentHandler)
                        } else{
                            BluetoothService(correctConnectionID, parentHandler)
                        }
                        val writtenMsg = parentHandler.obtainMessage(
                            MESSAGECONNECTED, -1, -1, ""
                        )
                        writtenMsg.sendToTarget()
                    }
                }
            }
        }
        BluetoothReader(childHandler, hostSoc).start()
        //Looper.loop()
    }










    private fun decodeID(hostID: String): String {
        var b64 = Base64Int(hostID)
        var b16 = Base16Int(b64)
        return b16.toMAC()
    }
}