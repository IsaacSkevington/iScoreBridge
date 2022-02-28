package com.OS3.iscorebridge

import android.os.Handler
import android.os.Looper
import android.os.Message


class WifiClient(var hostID : String, var parentHandler: Handler, service: WifiService){

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

    fun connect(){
        while(!connectionHandlerSet){
        }

        wifiService.connectReader(hostID)
    }










    private fun decodeID(hostID: String): String {
        var b64 = Base64Int(hostID)
        var b16 = Base16Int(b64)
        return b16.toMAC()
    }
}