package com.example.iscorebridge

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.net.Socket

const val MESSAGE_CONNECT = 7


val ME = "HOSTCONNECT"
class BluetoothHost(private val deviceAdapter : BluetoothAdapter) : Thread(){
    
    lateinit var hostHandler : Handler
    lateinit var myWriter: BluetoothWriter
    var clients = ArrayList<String>()
    @Volatile lateinit var myClient : BluetoothSocket
    @Volatile var cancel = false


    override fun run(){
        var serverSoc = deviceAdapter.listenUsingRfcommWithServiceRecord(
            "iscorebridge",
            programUUID
        )
        while(!cancel) {

            val writerSoc = serverSoc.accept()
            var clientAssignment: String = if (clients.size == 0) {
                myClient = writerSoc
                ME

            } else {
                clients[clients.size - 1]
            }

            var writer = BluetoothWriter(writerSoc, clientAssignment)
            writer.start()
            if(clients.size == 0){
                myWriter = writer
            }
            clients.add(writerSoc.remoteDevice.address)
        }
    }

    fun startGame(){
        cancel = true;
    }




}