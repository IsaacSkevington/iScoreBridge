package com.example.iscorebridge

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [StartGameScreen.newInstance] factory method to
 * create an instance of this fragment.
 */

@Volatile lateinit var bluetoothHost: BluetoothHost
@Volatile var amHost : Boolean = false
@RequiresApi(Build.VERSION_CODES.O)
fun encodeID(ID : String) : String{
    var b64Encoder = Base64.getEncoder()
    var start= ID.length - 5
    if(start < 0){
        start = 0
    }
    var ID64 = b64Encoder.encodeToString(ID.substring(start, ID.length).toByteArray())
    return ID64.toString()
}

@Volatile lateinit var bth : BluetoothHost
class StartGameScreen : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start_game_screen, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        amHost = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amHost = true
        view.findViewById<TextView>(R.id.idTextView).text = encodeID(bluetoothAdapter.name)
        val requestCode = ENABLE_DISCOVERABLE;

        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivityForResult(discoverableIntent, requestCode)

        bluetoothHost = BluetoothHost(bluetoothAdapter)
        bluetoothHost.start()
        view.findViewById<Button>(R.id.StartPlayingButton).setOnClickListener {
            Looper.prepare()
            var handler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGECONNECTED ->{
                            send(MESSAGE_START, "")
                            findNavController().navigate(R.id.startGameToScore)
                        }
                    }
                }
            }
            bluetoothService = BluetoothService(bluetoothHost.myWriter, bluetoothHost.myClient.remoteDevice.address, handler)

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StartGameScreen.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            StartGameScreen().apply {
                arguments = Bundle().apply {
                }
            }
    }
}