package com.OS3.iscorebridge

import android.os.Handler
import java.io.IOException
import java.io.InputStream
import java.net.Socket


class OneTimeWifiReader(socket: Socket, @Volatile var handler : Handler){
    @Volatile
    var data : Communication

    init {
        val reader = WifiReader(socket, handler, true)
        do {
        } while (!reader.messageWaiting)
        data = reader.getMessage()
        reader.kill()
    }
}


class WifiReader (val socket: Socket, @Volatile var handler : Handler, private var oneTime : Boolean) : Thread(){

    constructor(socket: Socket, handler : Handler) : this(socket, handler, false)


    @Volatile var connected = false
    private lateinit var inputStream: InputStream
    private val buffer: ByteArray = ByteArray(10000)
    @Volatile var messageWaiting : Boolean = false
    @Volatile var killFlag = false
    @Volatile private lateinit var waitingMessage : Communication

    init{
        start()
    }


    fun getMessage() : Communication{
        messageWaiting = false
        return waitingMessage
    }



    override fun run() {
        var numBytes: Int
        this.inputStream = socket.inputStream
        this.connected = true
        while (!killFlag) {
            try {
                numBytes = inputStream.read(buffer)
            } catch (e: IOException) {
                handler.obtainMessage(MESSAGE_READER_DISCONNECTED, socket.inetAddress).sendToTarget()
                kill()
                continue
            }

            var c: Communication
            try{
                val readMessage = String(buffer, 0, numBytes)
                c = Communication(readMessage)
            } catch (e: Exception) {
                continue
            }
            waitingMessage = c
            messageWaiting = true
            if(oneTime){
                break
            }
            handler.obtainMessage(MESSAGE_READ, c).sendToTarget()
        }
    }

    fun kill() : Boolean{
        killFlag = true
        return try {
            socket.close()
            inputStream.close()
            true
        } catch(e : Exception){
            false
        }
    }

}