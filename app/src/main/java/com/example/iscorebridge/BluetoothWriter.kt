package com.example.iscorebridge

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.navigation.fragment.findNavController
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

val MESSAGE_WRITE = 2
val STRING = 0
val BYTEARRAY = 1


class BluetoothWriter(val socket : BluetoothSocket) : Thread() {

        @Volatile lateinit var sendHandler: Handler
        var firstMessage = "NONE"
        private val outputStream: OutputStream = socket.outputStream

        constructor(socket: BluetoothSocket, firstMessage: String) : this(socket) {
            this.firstMessage = firstMessage
        }


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
            //val writtenMsg = handler.obtainMessage(
            //    MESSAGE_WRITE, -1, -1, buffer
            //)
            //writtenMsg.sendToTarget()
        }

        override fun run() {
            Looper.prepare()
            sendHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_WRITE -> {
                            if (msg.arg1 == STRING) {
                                write(msg.obj as String)
                            }
                            if (msg.arg1 == BYTEARRAY) {
                                write(msg.obj as ByteArray)
                            }

                        }
                    }
                }
            }
            if(firstMessage != "NONE"){
                write(firstMessage)
            }

            Looper.loop()
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {

            } catch (e: IOException) {

            }
        }
    }