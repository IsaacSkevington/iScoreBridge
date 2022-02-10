package com.example.iscorebridge

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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


    fun tablesCheck(view : View) : Boolean{
        var tables = view.findViewById<TextInputEditText>(R.id.numberTablesEntry).text.toString()
        try{
            var x = tables.toInt()
            if(x < 1){
                view.findViewById<TextInputLayout>(R.id.numberTablesLayout).error = "Tables must be greater than 0"
                return false
            }
        }
        catch (e : Exception){
            view.findViewById<TextInputLayout>(R.id.numberTablesLayout).error = "Tables must be a number"
            return false
        }
        return true
    }

    fun errorCheck(view:View): Boolean{
        view.findViewById<TextInputLayout>(R.id.numberTablesLayout).isErrorEnabled = false
        return tablesCheck(view) && bluetoothHost.clients.size > 0
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
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0)
        }
        startActivityForResult(discoverableIntent, requestCode)

        bluetoothHost = BluetoothHost(bluetoothAdapter)
        bluetoothHost.start()
        view.findViewById<Button>(R.id.StartPlayingButton).setOnClickListener {



            if(errorCheck(view)) {
                var tables = view.findViewById<TextInputEditText>(R.id.numberTablesEntry).text.toString().toInt()
                var movementTypeSelection = view.findViewById<RadioGroup>(R.id.movementGroup).checkedRadioButtonId
                var movementType = when(movementTypeSelection){
                    R.id.movementMitchell -> MOVEMENT_MITCHELL
                    R.id.movementHowell -> MOVEMENT_HOWELL
                    R.id.movementNone -> MOVEMENT_NONE
                    else -> MOVEMENT_NONE
                }

                var gameMode = if(view.findViewById<ToggleButton>(R.id.modeSwitch).isChecked){
                    GAMEMODE_PAIRS
                }
                else{
                    GAMEMODE_TEAMS
                }

                gameInfo = GameInfo(tables, gameMode, movementType, bluetoothHost.clients)

                var handler = object : Handler(Looper.myLooper()!!) {
                    override fun handleMessage(msg: Message) {
                        when (msg.what) {
                            MESSAGECONNECTEDREADER -> {
                                bluetoothService.send(
                                    SENDSTART,
                                    gameInfo.toString()
                                )
                            }
                            MESSAGE_START -> {
                                gameInfo = msg.obj as GameInfo
                                bluetoothService.clientList = gameInfo.clientList
                                findNavController().navigate(R.id.startGameToScore)
                            }
                        }
                    }
                }
                bluetoothService = BluetoothService(
                    bluetoothHost.myWriter,
                    bluetoothHost.myClient.remoteDevice.address,
                    handler
                )
                bluetoothService.clientList = bluetoothHost.clients
                bluetoothService.connect(activity!!)
            }


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