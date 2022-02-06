package com.example.iscorebridge

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*


val programUUID: UUID = UUID.fromString("096e8f5d-2b31-410a-9cc3-c577003bbfdd")

@Volatile lateinit var bluetoothService : BluetoothService




const val MESSAGECONNECTED = 3
const val MESSAGE_START = 13
const val MESSAGE_SEND = 6
const val MESSAGE_SEND_MATCH = 11
const val MESSAGE_READ_MATCH = 12
const val ENABLE_DISCOVERABLE = 4
const val MESSAGE_MATCHUPDATE = 5

fun send(purpose : Int, msg : String){
    var c = Communication(bluetoothAdapter.name, purpose, msg)
    bluetoothService.childHandler.obtainMessage(MESSAGE_SEND, -1, -1, c).sendToTarget()
}

class BluetoothService(): Activity() {

    lateinit var writer : BluetoothWriter
    lateinit var reader : BluetoothReader
    var writerSet : Boolean = false
    var readerSet : Boolean = false
    lateinit var childHandler : Handler
    lateinit var bluetoothDevice : BluetoothDevice
    lateinit var deviceID : String
    lateinit var readerID : String
    @Volatile lateinit var parentHandler : Handler

    constructor(readerID : String, parentHandler : Handler) : this() {
        this.readerID = readerID
        this.parentHandler = parentHandler
        deviceID = bluetoothAdapter.name
        enableDiscoverable()
    }
    constructor(bluetoothDevice : BluetoothDevice, parentHandler: Handler) : this() {
        deviceID = bluetoothAdapter.name
        this.parentHandler = parentHandler
        this.bluetoothDevice = bluetoothDevice
        enableDiscoverable()

    }

    constructor(bluetoothWriter: BluetoothWriter, readerID: String, parentHandler: Handler) : this(){
        deviceID = bluetoothAdapter.name
        this.writer = bluetoothWriter
        writerSet = true
        this.readerID = readerID
        this.parentHandler = parentHandler
        connect()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            ENABLE_DISCOVERABLE ->{
                if(resultCode != RESULT_CANCELED){
                    DataHandler().start()
                    connect()
                }

            }
        }
    }


    private fun enableDiscoverable(){
        val requestCode = ENABLE_DISCOVERABLE
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivityForResult(discoverableIntent, requestCode)

    }




    private inner class DataHandler() : Thread() {
        override fun run() {
            Looper.prepare()
            childHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_READ -> {

                            var readBuf: ByteArray = msg.obj as ByteArray;
                            // construct a string from the valid bytes in the buffer
                            var readMessage = String(readBuf, 0, msg.arg1);
                            var c = Communication(readMessage)
                            if(c.deviceID != deviceID) {
                                writer.sendHandler.obtainMessage(MESSAGE_WRITE,  BYTEARRAY, -1, readBuf).sendToTarget()
                            }
                            if(c.purpose == SENDMATCH){
                                var newMatch = Match(c.msg)
                                match.merge(newMatch)
                            }
                            else if(c.purpose == SENDSTART){
                                parentHandler.obtainMessage(MESSAGE_START, -1, -1, null).sendToTarget()
                            }

                        }
                        MESSAGE_SEND ->{
                            var c = msg.obj as Communication
                            writer.sendHandler.obtainMessage(MESSAGE_WRITE,  STRING, -1, c.toString()).sendToTarget()
                        }
                    }

                }
            }
            Looper.loop()
        }
    }



    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(programUUID)
        }



        public override fun run() {
            mmSocket?.let { socket ->
                socket.connect()
                onconnect(socket)


            }
        }

        public fun initialiseWriter(){
            var serverSoc = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                "iscorebridge",
                programUUID
            )
            val writerSoc = serverSoc.accept()
            serverSoc.close()
            writer = BluetoothWriter(writerSoc)

        }

        public fun onconnect(socket:BluetoothSocket){
            reader = BluetoothReader(childHandler, socket)
            if(!writerSet) {
                initialiseWriter()
            }
            val connectedMsg = parentHandler.obtainMessage(
                MESSAGECONNECTED
            )
            connectedMsg.sendToTarget()
            reader.start()
        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val pair = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    bluetoothAdapter.cancelDiscovery()
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if(device.address == readerID){
                        ConnectThread(device).start()
                    }

                }
            }
        }
    }


    private fun connect(){
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter!!.bondedDevices
        pairedDevices?.forEach { device ->
            if(device.address == this.readerID){
                ConnectThread(device).start()
                return
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(pair, filter)
        bluetoothAdapter.startDiscovery()



    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pair)
    }



}