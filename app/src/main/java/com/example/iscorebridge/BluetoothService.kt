package com.example.iscorebridge

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.util.*




@Volatile lateinit var bluetoothService : BluetoothService




const val MESSAGECONNECTED = 3
const val  MESSAGECONNECTEDWRITER = 14
const val MESSAGECONNECTEDREADER = 15
const val MESSAGE_CLIENT_CHANGE = 19
const val MESSAGE_START = 13
const val MESSAGE_SEND = 6
const val MESSAGE_SEND_MATCH = 11
const val MESSAGE_READ_MATCH = 12
const val ENABLE_DISCOVERABLE = 4
const val MESSAGE_MATCHUPDATE = 5



class BluetoothService() {

    @Volatile lateinit var clientList : ArrayList<String>
    @Volatile lateinit public var activity : Activity
    @Volatile lateinit var writer : BluetoothWriter
    @Volatile lateinit var reader : BluetoothReader
    @Volatile var writerSet : Boolean = false
    @Volatile var readerSet : Boolean = false
    @Volatile lateinit var childHandler : Handler
    lateinit var deviceID : String
    lateinit var readerID : String
    @Volatile lateinit var parentHandler : Handler
    @Volatile lateinit var serviceHandler : Handler


    constructor(readerID : String, parentHandler : Handler) : this() {
        this.readerID = readerID
        this.parentHandler = parentHandler
        deviceID = bluetoothAdapter.name
        RunningThread().start()
    }
    constructor(reader : BluetoothReader, parentHandler: Handler) : this() {
        deviceID = bluetoothAdapter.name
        this.parentHandler = parentHandler
        this.reader = reader
        readerSet = true
        RunningThread().start()
        parentHandler.obtainMessage(MESSAGECONNECTEDREADER).sendToTarget()

    }

    constructor(bluetoothWriter: BluetoothWriter, readerID: String, parentHandler: Handler) : this(){
        deviceID = bluetoothAdapter.name
        this.writer = bluetoothWriter
        writerSet = true
        this.readerID = readerID
        this.parentHandler = parentHandler
        RunningThread().start()
    }






    fun send(purpose : Int, msg : String){
        var c = Communication(bluetoothAdapter.name, purpose, msg)
        writer.sendHandler.obtainMessage(MESSAGE_WRITE, STRING, -1, c.toString()).sendToTarget()
    }


    inner class RunningThread() : Thread(){
        public override fun run(){
            Looper.prepare()
            serviceHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_READER_DISCONNECTED -> {

                            var index = clientList.indexOf(reader.socket.remoteDevice.address)
                            if(index == 0){
                                index = clientList.size
                            }
                            var
                                    connectionAddress = clientList[index - 1]
                            // Create a BroadcastReceiver for ACTION_FOUND.
                            val pair = object : BroadcastReceiver() {
                                override fun onReceive(context: Context, intent: Intent) {
                                    when(intent.action) {
                                        BluetoothDevice.ACTION_FOUND -> {
                                            bluetoothAdapter.cancelDiscovery()
                                            // Discovery has found a device. Get the BluetoothDevice
                                            // object and its info from the Intent.
                                            val device: BluetoothDevice =
                                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                                            if(device.address == connectionAddress){
                                                var socket = device.createRfcommSocketToServiceRecord(programUUID)
                                                socket.connect()
                                                var handler = reader.handler
                                                reader.stop()
                                                reader = BluetoothReader(handler, socket, serviceHandler)
                                                reader.forwardHandler = writer.sendHandler
                                            }

                                        }
                                    }
                                }
                            }
                            val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter!!.bondedDevices
                            for(device in pairedDevices){
                                if(device.address == connectionAddress){
                                    var socket = device.createRfcommSocketToServiceRecord(programUUID)
                                    socket.connect()
                                    var handler = reader.handler
                                    reader.stop()
                                    reader = BluetoothReader(handler, socket, serviceHandler)
                                    reader.forwardHandler = writer.sendHandler
                                }
                            }

                            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                            activity.registerReceiver(pair, filter)
                            bluetoothAdapter.startDiscovery()
                        }
                        MESSAGE_WRITER_DISCONNECTED ->{
                            var serverSoc = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                                "iscorebridge",
                                programUUID
                            )
                            val writerSoc = serverSoc.accept()
                            clientList.remove(writer.socket.remoteDevice.address)
                            writer.stop()
                            var c = Communication(bluetoothAdapter.name, MESSAGE_CLIENT_CHANGE, clientList.toString().substring(1, clientList.toString().length - 1))
                            writer = BluetoothWriter(writerSoc, serviceHandler, c.toString())
                        }
                    }

                }
            }
            if(readerSet) {
                reader.serviceHandler = serviceHandler
            }
            if(writerSet) {
                writer.serviceHandler = serviceHandler
            }
            Looper.loop()
        }
    }

    public fun setHandler(handler: Handler){
        this.parentHandler = handler
        if(readerSet) {
            this.reader.handler = parentHandler
        }
    }

    inner class ConnectWriterThread() : Thread(){
        public override fun run(){
            initialiseWriter()
        }
        public fun initialiseWriter(){
            var serverSoc = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                "iscorebridge",
                programUUID
            )
            val writerSoc = serverSoc.accept()
            serverSoc.close()
            writer = BluetoothWriter(writerSoc, serviceHandler)
            while(!writer.sendHandlerSet){

            }
            reader.forwardHandlerSet(writer.sendHandler)
            parentHandler.obtainMessage(MESSAGECONNECTEDWRITER).sendToTarget()

        }
    }





    private inner class ConnectReaderThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(programUUID)
        }



        public override fun run() {
            mmSocket?.let { socket ->
                socket.connect()
                onconnect(socket)


            }
        }



        public fun onconnect(socket:BluetoothSocket){
            reader = BluetoothReader(parentHandler, socket, serviceHandler)
            readerSet = true
            if(writerSet){
                reader.forwardHandlerSet(writer.sendHandler)
            }

            parentHandler.obtainMessage(MESSAGECONNECTEDREADER).sendToTarget()
        }
    }





    fun connect(activity : Activity){

        // Create a BroadcastReceiver for ACTION_FOUND.
        val pair = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when(intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        bluetoothAdapter.cancelDiscovery()
                        // Discovery has found a device. Get the BluetoothDevice
                        // object and its info from the Intent.
                        val device: BluetoothDevice =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                        if(device.address == readerID){
                            ConnectReaderThread(device).start()
                        }

                    }
                }
            }
        }
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter!!.bondedDevices
        pairedDevices?.forEach { device ->
            if(device.address == this.readerID){
                ConnectReaderThread(device).start()
                return
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity.registerReceiver(pair, filter)
        bluetoothAdapter.startDiscovery()



    }



}