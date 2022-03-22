package com.OS3.iscorebridge

import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


@Volatile var startGame = false
class JoinGame : Fragment() {

    var joined = false
    lateinit var handler : Handler

    val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

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







    @RequiresApi(Build.VERSION_CODES.O)
    fun joinGame(view : View){

        if(!wifiClientInitialised) {
            var id = view.findViewById<TextView>(R.id.idEntry).text
            wifiClient = WifiClient(id.toString(), handler)
            wifiClient.start()
            wifiClientInitialised = true
        }
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

    override fun onPause(){
        super.onPause()
    }

    override fun onDestroy(){
        super.onDestroy()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGECONNECTEDHOST -> {
                        if(!joined) {
                            joined = true
                            Toast.makeText(context, "Joined successfully", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.joinGameToWaitToStart)
                        }
                    }
                    MESSAGE_CONNECTION_FAILED -> {
                        Toast.makeText(context, "Joining failed", Toast.LENGTH_LONG).show()
                        view.findViewById<Button>(R.id.joinButton).visibility = View.VISIBLE
                        view.findViewById<ProgressBar>(R.id.joiningProgress).visibility = View.INVISIBLE
                        view.findViewById<TextView>(R.id.joiningDisplay).text = ""
                    }
                }

            }
        }
        wifiService.parentHandler = handler
        view.findViewById<ProgressBar>(R.id.joiningProgress).visibility = View.INVISIBLE
        view.findViewById<Button>(R.id.joinButton).setOnClickListener {
            if(errorCheck(view)){
                view.findViewById<Button>(R.id.joinButton).visibility = View.INVISIBLE
                view.findViewById<ProgressBar>(R.id.joiningProgress).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.joiningDisplay).text = "Joining game"
                joinGame(view)
            }

        }
    }
}