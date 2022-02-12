package com.example.iscorebridge

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


@Volatile var startGame = false
class JoinGame : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_join_game, container, false)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            ENABLE_DISCOVERABLE ->{
                if(resultCode != Activity.RESULT_CANCELED){
                    bluetoothService.ConnectWriterThread().start()
                    findNavController().navigate(R.id.joinGameToWaitToStart)

                }

            }
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    fun joinGame(view : View){
        var id = view.findViewById<TextView>(R.id.idEntry).text
        var handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGECONNECTEDREADER -> {


                        val requestCode = ENABLE_DISCOVERABLE
                        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600)
                        }
                        startActivityForResult(discoverableIntent, requestCode)


                    }
                    MESSAGECONNECTED ->{
                        bluetoothService.connect(activity!!)
                    }
                }

            }
        }
        var btc = BluetoothClient(id.toString(), handler)

        lateinit var pair : BroadcastReceiver
        pair = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onReceive(context: Context, intent: Intent) {
                when(intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        if(device != null){
                            if(device.name != null) {
                                if (encodeID(device.name) == id.toString()) {
                                    activity!!.unregisterReceiver(pair)
                                    bluetoothAdapter.cancelDiscovery()

                                    btc.connect(device)
                                    Looper.loop()
                                }
                            }
                        }

                    }
                }
            }
        }

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            if(encodeID(device.name) == id.toString()){
                btc.connect(device)
                return
            }
        }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity!!.registerReceiver(pair, filter)
        bluetoothAdapter.startDiscovery()
    }

    fun tableCheck(view:View) : Boolean{
        var table = view.findViewById<TextInputEditText>(R.id.tableEntry).text.toString()
        if(table == ""){
            view.findViewById<TextInputLayout>(R.id.tableEntryLayout).error = "Table must be specified"
            return false
        }
        try{
            var x = table.toInt()
            if(x < 1){
                view.findViewById<TextInputLayout>(R.id.tableEntryLayout).error = "Table must be greater than 0"
                return false
            }
        }
        catch (e : Exception){
            view.findViewById<TextInputLayout>(R.id.tableEntryLayout).error = "Table must be a number"
            return false
        }
        return true
    }

    fun idCheck(view:View) : Boolean{
        var id = view.findViewById<TextInputEditText>(R.id.idEntry).text.toString()
        if(id == ""){
            view.findViewById<TextInputLayout>(R.id.tableEntryLayout).error = "ID must be specified"
            return false
        }
        return true
    }


    fun errorCheck(view : View) : Boolean{
        view.findViewById<TextInputLayout>(R.id.tableEntryLayout).isErrorEnabled = false
        view.findViewById<TextInputLayout>(R.id.idEntryLayout).isErrorEnabled = false
        var ret = tableCheck(view)
        return idCheck(view) && ret
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.joinButton).setOnClickListener {
            if(errorCheck(view)){
                joinGame(view)
            }

        }
    }
}