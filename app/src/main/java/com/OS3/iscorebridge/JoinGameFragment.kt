package com.OS3.iscorebridge

import android.annotation.SuppressLint
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class JoinGame : Fragment() {

    var joined = false
    lateinit var handler : Handler
    var joinMode : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args : JoinGameArgs by navArgs()
        this.joinMode = args.joinMode
        return inflater.inflate(R.layout.fragment_join_game, container, false)
    }







    @RequiresApi(Build.VERSION_CODES.O)
    fun joinGame(view : View){

        if(!wifiClientInitialised) {
            val id = view.findViewById<TextView>(R.id.idEntry).text
            wifiClient = WifiClient(handler)
            wifiClient.ConnectionThread(id.toString()).start()
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

    fun setVisibility(fab : FloatingActionButton, vis : Int){
        fab.visibility = vis
        fab.isClickable = (vis == VISIBLE)
    }

    fun switchButtons(view: View, back : Int, cancel : Int, forward : Int){
        setVisibility(view.findViewById(R.id.joinBackButton), back)
        setVisibility(view.findViewById(R.id.joinCancelButton), cancel)
        setVisibility(view.findViewById(R.id.joinForwardButton), forward)

    }

    fun onConnectionTry(view : View){
        switchButtons(view, INVISIBLE, VISIBLE, INVISIBLE)
        view.findViewById<ProgressBar>(R.id.joiningProgress).visibility = VISIBLE
        view.findViewById<ProgressBar>(R.id.joiningProgress).visibility = VISIBLE
    }

    fun onConnectionFail(view : View){
        Toast.makeText(context, "Joining failed", Toast.LENGTH_LONG).show()
        onConnectionCancel(view)
    }

    fun onBack(){
        findNavController().popBackStack()
    }

    fun onConnectionCancel(view : View){
        switchButtons(view, VISIBLE, INVISIBLE, VISIBLE)
        view.findViewById<ProgressBar>(R.id.joiningProgress).visibility = INVISIBLE
        //Handle cancel logic here

    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_CONNECTED_HOST -> {
                        if(!joined) {
                            joined = true
                            Toast.makeText(context, "Joined successfully", Toast.LENGTH_LONG).show()
                            if(joinMode == JOIN) findNavController().navigate(R.id.joinGameToEnterDetails)
                            else findNavController().navigate(R.id.joingGameToSpectatorInitialise)
                        }
                    }
                    MESSAGE_CONNECTION_FAILED -> {
                        onConnectionFail(view)
                    }
                }

            }
        }
        wifiService.parentHandler = handler
        onConnectionCancel(view)
        view.findViewById<FloatingActionButton>(R.id.joinForwardButton).setOnClickListener {
            if(errorCheck(view)){
                onConnectionTry(view)
                joinGame(view)
            }
        }
        view.findViewById<FloatingActionButton>(R.id.joinBackButton).setOnClickListener {
            onBack()
        }
        view.findViewById<FloatingActionButton>(R.id.joinCancelButton).setOnClickListener {
            onConnectionCancel(view)
        }
    }
}