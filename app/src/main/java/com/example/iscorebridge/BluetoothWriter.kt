package com.example.iscorebridge

import android.bluetooth.BluetoothSocket
import android.os.Handler
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

val MESSAGE_WRITE = 2

class BluetoothWriter(val handler : Handler, val socket : BluetoothSocket) {

    inner class getAsyncWriter : Thread() {

        private val outputStream: OutputStream = socket.outputStream
        private val buffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        // Call this from the main activity to send data to the remote device.
        fun write(s : String){
            write(s.toByteArray())
        }

        fun write(bytes: ByteArray) {
            try {
                outputStream.write(bytes)
            } catch (e: IOException) {
            }

            // Share the sent message with the UI activity.
            val writtenMsg = handler.obtainMessage(
                MESSAGE_WRITE, -1, -1, buffer
            )
            writtenMsg.sendToTarget()
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {

            } catch (e: IOException) {

            }
        }
    }
}