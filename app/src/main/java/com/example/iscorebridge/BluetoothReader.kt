package com.example.iscorebridge

import android.bluetooth.BluetoothSocket
import android.os.Handler
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

val MESSAGE_READ = 0

class BluetoothReader (val handler : Handler, var socket : BluetoothSocket) : Thread(){
    private var inputStream: InputStream = socket.inputStream
    private val buffer: ByteArray = ByteArray(10000) // mmBuffer store for the stream

    override fun run() {
        socket.connect()
        inputStream = socket.inputStream
        var numBytes: Int = 0 // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            // Read from the InputStream.
            try {
                numBytes = inputStream.read(buffer)
            } catch (e: IOException) {
                throw e
            }

            // Send the obtained bytes to the UI activity.
            val readMsg = handler.obtainMessage(
                MESSAGE_READ, numBytes, -1,
                buffer
            )
            readMsg.sendToTarget()
        }
    }


    // Call this method from the main activity to shut down the connection.
    fun cancel() {
        try {
        } catch (e: IOException) {

        }
    }
}