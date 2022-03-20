package com.OS3.iscorebridge

import android.os.Handler
import java.io.IOException
import java.io.InputStream
import java.net.Socket


class OneTimeWifiReader(val socket: Socket, @Volatile var handler : Handler, @Volatile var serviceHandler: Handler){
    @Volatile
    var data : Communication

    init {
        var reader = WifiReader(socket, handler, serviceHandler, true)
        do {
        } while (!reader.messageWaiting)
        data = reader.getMessage()
        reader.kill()
    }
}


class WifiReader (val socket: Socket, @Volatile var handler : Handler, @Volatile var serviceHandler: Handler, var oneTime : Boolean) : Thread(){

    constructor(socket: Socket, handler : Handler, serviceHandler: Handler) : this(socket, handler, serviceHandler, false)


    @Volatile var connected = false
    private lateinit var inputStream: InputStream
    private val buffer: ByteArray = ByteArray(10000)
    @Volatile lateinit var forwardHandler : Handler
    @Volatile var forwardHandlerSet : Boolean = false
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


    fun forwardHandlerSet(handler : Handler){
        this.forwardHandler = handler
        this.forwardHandlerSet = true
    }

    override fun run() {
        var numBytes: Int = 0
        this.inputStream = socket.inputStream
        this.connected = true
        while (!killFlag) {
            try {
                numBytes = inputStream.read(buffer)
            } catch (e: IOException) {
                handler.obtainMessage(MESSAGE_READER_DISCONNECTED).sendToTarget()
                kill()
                continue
            }

            var c: Communication
            try{
                var readMessage = String(buffer, 0, numBytes);
                c = Communication(readMessage)
            } catch (e: Exception) {
                continue
            }
            waitingMessage = c
            messageWaiting = true
            if(oneTime){
                break
            }
            if (c.purpose == SENDGAME) {
                if (c.deviceID != deviceID) {
                    var newGame = Game(c.msg)
                    match.addGame(newGame)
                    while (!forwardHandlerSet) {
                        var x = 0
                    }
                    forwardHandler.obtainMessage(MESSAGE_WRITE, BYTEARRAY, -1, buffer)
                        .sendToTarget()
                }
            } else if (c.purpose == SENDSTART) {
                var gameInfo = GameInfo(c.msg)
                if (!gameInfo.clientList.contains(socket.inetAddress.hostAddress)) {
                    gameInfo.clientList.add(0, socket.inetAddress.hostAddress)
                    c.msg = gameInfo.toString()
                    if (forwardHandlerSet && !amHost) {
                        forwardHandler.obtainMessage(MESSAGE_WRITE, STRING, -1, c.toString())
                            .sendToTarget()
                    }
                    handler.obtainMessage(MESSAGE_START, gameInfo).sendToTarget()
                    continue

                }
                while (!forwardHandlerSet) {
                    var x = 0
                }
                if (c.deviceID != deviceID) {
                    forwardHandler.obtainMessage(MESSAGE_WRITE, BYTEARRAY, -1, buffer)
                        .sendToTarget()
                }
                handler.obtainMessage(MESSAGE_START, gameInfo).sendToTarget()

            } else {
                handler.obtainMessage(MESSAGE_READ, c).sendToTarget()
            }
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