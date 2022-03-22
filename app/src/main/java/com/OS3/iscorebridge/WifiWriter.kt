package com.OS3.iscorebridge

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.io.IOException
import java.io.OutputStream
import java.net.Socket


class OneTimeWifiWriter(private var socket : Socket, var handler: Handler, private var message : String){
    init{
        val writer = WifiWriter(socket, handler, firstMessage = message)
        do{}while(!writer.firstMessageWritten)
        writer.kill()
    }
}


class WifiWriter(private var socket: Socket, private var serviceHandler : Handler, var firstMessage: String = "NONE") : Thread() {



        @Volatile lateinit var sendHandler: Handler
        private var outputStream: OutputStream = this.socket.outputStream
        @Volatile var firstMessageWritten = false

        @Volatile
        var sendHandlerSet = false

        init {
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