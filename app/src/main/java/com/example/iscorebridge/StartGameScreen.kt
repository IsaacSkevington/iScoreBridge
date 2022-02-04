package com.example.iscorebridge

import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.os.Bundle
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

const val IDSTART = 0
const val IDEND = 5

@RequiresApi(Build.VERSION_CODES.O)
fun encodeID(ID : String) : String{
    var b64Encoder = Base64.getEncoder()
    var ID64 = b64Encoder.encodeToString(ID.substring(IDSTART, IDEND).toByteArray())
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.idTextView).text = encodeID(bluetoothAdapter.name)
        bth = BluetoothHost(bluetoothAdapter, bluetoothAdapter.address)
        view.findViewById<Button>(R.id.StartPlayingButton).setOnClickListener {
            bth.cancel = true
            findNavController().navigate(R.id.startGameToScore)
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