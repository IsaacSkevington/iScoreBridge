package com.OS3.iscorebridge

import android.os.Handler
import java.net.ServerSocket

class Client(private var port : Int, var number:Int, var handler : Handler){

    private lateinit var reader : WifiReader
    lateinit var writer: WifiWriter

    fun connect(){
        val serverSoc = ServerSocket(port)
        val connectedSoc = serverSoc.accept()
        serverSoc.close()
        reader = WifiReader(connectedSoc, handler)
        writer = WifiWriter(connectedSoc, handler)

    }

    fun getAddress() : String{
        return reader.socket.inetAddress.hostAddress!!
    }

    fun kill(){
        writer.kill()
        reader.kill()
    }


}