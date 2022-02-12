package com.example.iscorebridge

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.io.IOException
import java.io.OutputStream


class BluetoothWriter(var socket: BluetoothSocket, var serviceHandler : Handler, var firstMessage: String = "NONE") : Thread() {



        @Volatile lateinit var sendHandler: Handler
        private var outputStream: OutputStream

        @Volatile public var sendHandlerSet = false

        init {
            this.outputStream = this.socket.outputStream
            start()
        }


        fun write(s : String){
            write(s.toByteArray())
        }

        fun write(bytes: ByteArray) {
            try {
                outputStream.write(bytes)
            } catch (e: IOException) {
                throw e
            }
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
    }