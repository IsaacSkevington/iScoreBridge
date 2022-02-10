package com.example.iscorebridge

import android.bluetooth.BluetoothSocket
import android.os.Handler
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


class BluetoothReader (@Volatile var handler : Handler, var socket: BluetoothSocket, @Volatile var serviceHandler: Handler) : Thread(){



    private var inputStream: InputStream
    private val buffer: ByteArray = ByteArray(10000) // mmBuffer store for the stream
    @Volatile lateinit var forwardHandler : Handler
    @Volatile var forwardHandlerSet : Boolean = false

    init{
        this.inputStream = socket.inputStream
        start()
    }

    fun forwardHandlerSet(handler : Handler){
        this.forwardHandler = handler
        this.forwardHandlerSet = true
    }

    override fun run() {
        inputStream = socket.inputStream
        var numBytes: Int = 0 // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            // Read from the InputStream.
            try {
                numBytes = inputStream.read(buffer)
            } catch (e: IOException) {
                handler.obtainMessage(MESSAGE_READER_DISCONNECTED).sendToTarget()
                continue
            }

            var readMessage = String(buffer, 0, numBytes);
            var c : Communication
            try {
                c = Communication(readMessage)
            }
            catch (e : Exception){
                continue
            }

            if(c.purpose == SENDGAME){
                if (c.deviceID != bluetoothAdapter.name) {
                    var newGame = Game(c.msg)
                    match.addGame(newGame)
                    while(!forwardHandlerSet){var x = 0}
                    forwardHandler.obtainMessage(MESSAGE_WRITE, BYTEARRAY, -1, buffer).sendToTarget()
                }
            }
            else if(c.purpose == SENDSTART){
                var gameInfo = GameInfo(c.msg)
                if(!gameInfo.clientList.contains(socket.remoteDevice.address)){
                    gameInfo.clientList.add(0, socket.remoteDevice.address)
                    c.msg = gameInfo.toString()
                    while(!forwardHandlerSet){var x = 0}
                    forwardHandler.obtainMessage(MESSAGE_WRITE, STRING, -1, c.toString()).sendToTarget()
                    handler.obtainMessage(MESSAGE_START, gameInfo).sendToTarget()
                    continue

                }
                while(!forwardHandlerSet){var x = 0}
                if (c.deviceID != bluetoothAdapter.name) {
                    forwardHandler.obtainMessage(MESSAGE_WRITE, BYTEARRAY, -1, buffer).sendToTarget()
                }
                handler.obtainMessage(MESSAGE_START, gameInfo).sendToTarget()

            }
            else{
                handler.obtainMessage(MESSAGE_READ, c).sendToTarget()
            }

            // construct a string from the valid bytes in the buffer


        }
    }


    // Call this method from the main activity to shut down the connection.
    fun cancel() {
        try {
        } catch (e: IOException) {

        }
    }
}