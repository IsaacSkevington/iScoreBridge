package com.OS3.iscorebridge

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.io.IOException
import java.io.OutputStream
import java.net.Socket


class OneTimeWifiWriter(var socket : Socket, var serviceHandler: Handler, var message : String){
    init{
        var writer = WifiWriter(socket, serviceHandler, firstMessage = message)
        while(!writer.firstMessageWritten){

        }
        writer.kill()
    }
}


class WifiWriter(var socket: Socket, var serviceHandler : Handler, var firstMessage: String = "NONE") : Thread() {



        @Volatile lateinit var sendHandler: Handler
        private var outputStream: OutputStream
        @Volatile var firstMessageWritten = false

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
                serviceHandler.obtainMessage(MESSAGE_WRITER_DISCONNECTED).sendToTarget()
                kill()
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
                firstMessageWritten = true
            }

            Looper.loop()
        }

        fun kill(){
            try {
                Looper.myLooper()!!.quit()
                socket.close()
                outputStream.close()
            }
            catch(e:Exception){

            }
        }
    }