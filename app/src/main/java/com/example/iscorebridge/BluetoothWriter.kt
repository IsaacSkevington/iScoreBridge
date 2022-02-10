package com.example.iscorebridge

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.navigation.fragment.findNavController
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*



class BluetoothWriter(var socket: BluetoothSocket, var serviceHandler : Handler, var firstMessage: String = "NONE") : Thread() {



        @Volatile lateinit var sendHandler: Handler
        private var outputStream: OutputStream

        @Volatile public var sendHandlerSet = false

        init {
            this.outputStream = this.socket.outputStream
            start()
        }



        // Call this from the main activity to send data to the remote device.
        fun write(s : String){
            write(s.toByteArray())
        }

        fun write(bytes: ByteArray) {
            try {
                outputStream.write(bytes)
            } catch (e: IOException) {
                throw e
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
                            else if (msg.arg1 == BYTEARRAY) {
                                write(msg.obj as ByteArray)
                            }

                        }
                    }
                }
            }
            sendHandlerSet = true
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