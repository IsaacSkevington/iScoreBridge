package com.example.iscorebridge

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import org.w3c.dom.Text


/**
 * A simple [Fragment] subclass.
 * Use the [JoinGame.newInstance] factory method to
 * create an instance of this fragment.
 */

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
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_join_game, container, false)
    }








    @RequiresApi(Build.VERSION_CODES.O)
    fun joinGame(view : View){
        var id = view.findViewById<TextView>(R.id.idEntry).text
        var handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGECONNECTED -> {
                        findNavController().navigate(R.id.joinGameToWaitToStart)
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
                        // Discovery has found a device. Get the BluetoothDevice
                        // object and its info from the Intent.
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
                Looper.loop()
            }
        }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity!!.registerReceiver(pair, filter)
        bluetoothAdapter.startDiscovery()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.joinButton).setOnClickListener {
            joinGame(view)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment JoinGame.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            JoinGame().apply {
                arguments = Bundle().apply {
                }
            }
    }
}