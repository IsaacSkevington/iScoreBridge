package com.OS3.iscorebridge

import android.annotation.SuppressLint
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


class JoinGame : Fragment() {

    var joined = false
    lateinit var handler : Handler

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
            val id = view.findViewById<TextView>(R.id.idEntry).text
            wifiClient = WifiClient(id.toString(), handler)
            wifiClient.start()
            wifiClientInitialised = true
        }
    }


    private fun idCheck(view:View) : Boolean{
        view.findViewById<TextInputEditText>(R.id.idEntry).also{
            var id = it.text.toString()
            if(id == ""){
                it.error = "ID must be specified"
                return false
            }
            return true
        }
    }


    private fun errorCheck(view : View) : Boolean{
        view.findViewById<TextInputLayout>(R.id.idEntryLayout).isErrorEnabled = false
        return idCheck(view)
    }

    override fun onPause(){
        super.onPause()
    }

    override fun onDestroy(){
        super.onDestroy()
    }


    @SuppressLint("SetTextI18n")
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
                            findNavController().navigate(R.id.joinGameToEnterDetails)
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