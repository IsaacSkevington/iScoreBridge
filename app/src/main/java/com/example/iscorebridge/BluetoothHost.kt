package com.example.iscorebridge

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.os.Message

const val MESSAGE_CONNECT = 7

class BluetoothHost(private val deviceAdapter : BluetoothAdapter, var myMAC : String) : Thread(){

    lateinit var hostHandler : Handler
    lateinit var writer: BluetoothWriter
    var clients = ArrayList<String>()
    @Volatile var cancel = false


    override fun run(){
        while(!cancel) {
            var serverSoc = deviceAdapter.listenUsingRfcommWithServiceRecord(
                "iscorebridge",
                programUUID
            )
            val writerSoc = serverSoc.accept()
            var clientAssignment: String
            clientAssignment = if (clients.size == 0) {
                myMAC
            } else {
                clients[clients.size - 1]
            }

            var writer = BluetoothWriter(hostHandler, writerSoc)
            writer.getAsyncWriter().write(clientAssignment)
            clients.add(writerSoc.remoteDevice.address)
        }
    }

    fun startGame(){
        cancel = true;
    }




}