package com.example.iscorebridge

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.os.Message


val ME = "HOSTCONNECT"
class BluetoothHost(private val deviceAdapter : BluetoothAdapter, @Volatile var parentHandler: Handler) : Thread(){
    
    @Volatile lateinit var hostHandler : Handler
    @Volatile lateinit var myWriter: BluetoothWriter
    var clients = ArrayList<String>()
    @Volatile lateinit var myClient : BluetoothSocket
    @Volatile var cancel = false
    init{
        ClientHandler().start()
    }


    inner class ClientHandler() : Thread(){

        override fun run(){
            Looper.prepare()
            hostHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_READER_DISCONNECTED->{

                        }
                        MESSAGE_WRITER_DISCONNECTED->{

                        }
                    }

                }
            }
            Looper.loop()
        }

    }

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
            parentHandler.obtainMessage(MESSAGE_CLIENT_CONNECTED, clients.size + 1, -1, writerSoc.remoteDevice.name).sendToTarget()
            var c = Communication(bluetoothAdapter.name, MESSAGE_CONNECTDEVICE, clientAssignment)
            var writer = BluetoothWriter(writerSoc, hostHandler, c.toString())
            if(clients.size == 0){
                myWriter = writer
            }
            clients.add(writerSoc.remoteDevice.address)
        }
    }




}