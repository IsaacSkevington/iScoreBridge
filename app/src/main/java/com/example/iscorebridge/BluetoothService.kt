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

fun getMac(context: Context) : String{
    val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val info = manager.connectionInfo
    return if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return ""
    }
    else {
        info.macAddress.toUpperCase()
    }
}

class SharedBluetoothData : ViewModel() {
    private val room = MutableLiveData<BluetoothService>()

    fun setBluetoothRoom(service : BluetoothService){
        this.room.value = service
    }
}

class SharedMatch : ViewModel(){
    val match = MutableLiveData<Match>()
}

const val MESSAGECONNECTED = 3
const val MESSAGE_SEND = 6
const val ENABLE_DISCOVERABLE = 4
const val MESSAGE_MATCHUPDATE = 5

class BluetoothService(var readerID : String, var bluetoothAdapter: BluetoothAdapter, var deviceID:String, @Volatile public var parentHandler: Handler) : Activity() {

    lateinit var writer : BluetoothWriter
    lateinit var reader : BluetoothReader
    lateinit var childHandler : Handler


    public fun start(){
        enableDiscoverable()
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

    private fun decodeID(id: String) : String{
        return id
    }

    public fun updateMatch(newMatch : Match){
        writer.getAsyncWriter().write(deviceID + newMatch.toString())
    }

    private fun parseDeviceID(msg: String) : String{
        return msg[0].toString() + msg[1].toString()
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
                            if(parseDeviceID(readMessage) == deviceID) {
                                writer.getAsyncWriter().write(readBuf)
                            }
                            var newMatch = Match(readMessage.substring(2))
                            match = newMatch
                            reader.getAsyncReader().start()
                        }
                        MESSAGE_SEND ->{
                            writer.getAsyncWriter().write(deviceID + (msg.obj as Match).toString())
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

        public fun onconnect(socket:BluetoothSocket){
            reader = BluetoothReader(childHandler, socket)
            var serverSoc = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                "iscorebridge",
                programUUID
            )
            val writerSoc = serverSoc.accept()
            serverSoc.close()
            writer = BluetoothWriter(childHandler, writerSoc)
            val writtenMsg = parentHandler.obtainMessage(
                MESSAGECONNECTED
            )
            writtenMsg.sendToTarget()
            reader.getAsyncReader().start()
        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val pair = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if(device.address == decodeID(readerID)){
                        ConnectThread(device).start()
                    }

                }
            }
        }
    }


    private fun connect(){
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            if(device.address == decodeID(this.readerID)){
                ConnectThread(device).start()
                return
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(pair, filter)



    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pair)
    }



}