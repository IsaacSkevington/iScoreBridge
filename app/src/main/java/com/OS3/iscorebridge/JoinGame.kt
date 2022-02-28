package com.OS3.iscorebridge

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







    @RequiresApi(Build.VERSION_CODES.O)
    fun joinGame(view : View){
        var id = view.findViewById<TextView>(R.id.idEntry).text
        var handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGECONNECTEDREADER -> {
                        wifiService.connectWriter()
                        findNavController().navigate(R.id.joinGameToWaitToStart)
                    }
                }

            }
        }
        wifiService.setHandler(handler)
        var wifiClient = WifiClient(id.toString(), handler, wifiService)
        wifiClient.connect()
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