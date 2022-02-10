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
    lateinit @Volatile var connectionHandler : Handler
    @Volatile var connectionHandlerSet = false
    init{
        ConnectionHandler().start()
    }

    inner class ConnectionHandler() : Thread(){
        override fun run(){
            Looper.prepare()
            connectionHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_READER_DISCONNECTED->{

                        }
                        MESSAGE_WRITER_DISCONNECTED->{

                        }
                    }

                }
            }
            connectionHandlerSet = true
            Looper.loop()
        }
    }

    fun connect(device : BluetoothDevice){
        var hostSoc = device.createRfcommSocketToServiceRecord(programUUID)
        hostSoc.connect()
        lateinit var reader : BluetoothReader
        childHandler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_READ -> {
                        var communication = msg.obj as Communication;
                        if(communication.msg == ME){
                            bluetoothService = BluetoothService(reader, parentHandler)
                        } else{
                            bluetoothService = BluetoothService(communication.msg, parentHandler)
                            val writtenMsg = parentHandler.obtainMessage(
                                MESSAGECONNECTED
                            )
                            writtenMsg.sendToTarget()
                        }


                    }
                }
            }
        }
        while(!connectionHandlerSet){
        }
        reader = BluetoothReader(childHandler, hostSoc, connectionHandler)
    }










    private fun decodeID(hostID: String): String {
        var b64 = Base64Int(hostID)
        var b16 = Base16Int(b64)
        return b16.toMAC()
    }
}