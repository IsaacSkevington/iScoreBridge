package com.OS3.iscorebridge

import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


@Volatile var amHost : Boolean = false

@RequiresApi(Build.VERSION_CODES.O)
fun encodeID(ID : String) : String{
    var out = ""
    for(char in ID){
        out += char.toInt().toString()
    }
    return if(out.length > 7){
        out.substring(out.length - 7, out.length)
    }
    else{
        out
    }
}
class StartGameScreen : Fragment() {


    var start = false
    var playerTableRows = ArrayList<TableRow>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_start_game_screen, container, false)
    }


    private fun tablesCheck(view : View) : Boolean{
        val tables = view.findViewById<TextInputEditText>(R.id.numberTablesEntry).text.toString()
        if(tables == ""){
            view.findViewById<TextInputLayout>(R.id.numberTablesLayout).error = "Tables must be specified"
            return false
        }
        try{
            val x = tables.toInt()
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

    private fun boardsCheck(view : View) : Boolean{
        val boards = view.findViewById<TextInputEditText>(R.id.numberBoardsEntry).text.toString()
        if(boards == ""){
            view.findViewById<TextInputLayout>(R.id.numberBoardsLayout).error = "Boards must be specified"
            return false
        }
        try{
            val x = boards.toInt()
            if(x < 1){
                view.findViewById<TextInputLayout>(R.id.numberBoardsLayout).error = "Boards must be greater than 0"
                return false
            }
        }
        catch (e : Exception){
            view.findViewById<TextInputLayout>(R.id.numberBoardsLayout).error = "Boards must be a number"
            return false
        }
        return true
    }

    private fun errorCheck(view:View): Boolean{
        view.findViewById<TextInputLayout>(R.id.numberTablesLayout).isErrorEnabled = false
        view.findViewById<TextInputLayout>(R.id.numberBoardsLayout).isErrorEnabled = false
        var ret = boardsCheck(view)
        ret = tablesCheck(view) && ret
        return ret && wifiHost.clients.size > 0

    }
    


    override fun onPause(){
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy(){
        super.onDestroy()
        amHost = false
    }

    fun updateClientTable(view : View){
        var table = view!!.findViewById<TableLayout>(R.id.playersTable)
        playerTableRows.forEach {
            table.removeView(it)
        }
        var currentRowItems = 0
        var currentRow = TableRow(view.context)
        wifiHost.getTables().forEach {
            var textView = TextView(view.context)
            textView.text = it.toString()
            if(++currentRowItems == 3){
                table.addView(currentRow)
                currentRow = TableRow(view.context)
                currentRowItems = 0
            }
        }

    }

    fun pairsMode(view : View){
        view.findViewById<ConstraintLayout>(R.id.mitchellTypeLayout).visibility = View.VISIBLE
        view.findViewById<RadioButton>(R.id.movementHowell).visibility = View.VISIBLE
        view.findViewById<CheckBox>(R.id.arrowSwitchCheck).visibility = View.VISIBLE

    }
    fun teamsMode(view : View){
        view.findViewById<ConstraintLayout>(R.id.mitchellTypeLayout).visibility = View.GONE
        view.findViewById<RadioButton>(R.id.movementHowell).visibility = View.GONE
        view.findViewById<CheckBox>(R.id.arrowSwitchCheck).visibility = View.GONE
        if(view.findViewById<RadioGroup>(R.id.movementGroup).checkedRadioButtonId == R.id.movementHowell){
            view.findViewById<RadioGroup>(R.id.movementGroup).check(R.id.movementNone)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        amHost = true
        view.findViewById<TextView>(R.id.idTextView).text = encodeID(MYINFO.deviceName)
        val handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGECONNECTEDHOST -> {

                    }
                    MESSAGE_START -> {
                        gameInfo = msg.obj as GameInfo
                        wifiService.clientList = gameInfo.clientList

                        if(!start) {
                            start = true
                            findNavController().navigate(R.id.startGameToScore)
                        }

                    }
                    MESSAGE_CLIENT_CONNECTED ->{
                        var client = msg.obj as ClientInfo
                        Toast.makeText(view.context, client.deviceName + " joined", Toast.LENGTH_LONG).show()

                    }
                    MESSAGE_UPDATE_CLIENT ->{
                        updateClientTable(view)
                    }
                    MESSAGE_DEVICE_ID_CHANGED -> {
                        Toast.makeText(view.context, "Device ID Changed!", Toast.LENGTH_LONG).show()
                        view.findViewById<TextView>(R.id.idTextView).text = encodeID(MYINFO.deviceName)
                    }
                }
            }
        }
        wifiService.createGroup()
        wifiService.parentHandler = handler
        wifiHost = WifiHost(handler)
        wifiHostInitialised = true

        view.findViewById<Switch>(R.id.modeSwitch).setOnClickListener{
            if(view.findViewById<Switch>(R.id.modeSwitch).isChecked){
                teamsMode(view)
            }
            else{
                pairsMode(view)
            }
        }

        view.findViewById<Button>(R.id.StartPlayingButton).setOnClickListener {
            if(errorCheck(view)) {

                val tables =
                    view.findViewById<TextInputEditText>(R.id.numberTablesEntry).text.toString()
                        .toInt()
                val boards =
                    view.findViewById<TextInputEditText>(R.id.numberBoardsEntry).text.toString()
                        .toInt()
                when {
                    wifiHost.clients.size + 1 > tables -> {
                        view.findViewById<TextInputLayout>(R.id.numberTablesLayout).error =
                            "Too many people in game (${wifiHost.clients.size + 1})"

                    }
                    wifiHost.clients.size + 1 < tables -> {
                        view.findViewById<TextInputLayout>(R.id.numberTablesLayout).error =
                            "Too few people in game (${wifiHost.clients.size + 1})"
                    }
                    else -> {
                        val movementTypeSelection =
                            view.findViewById<RadioGroup>(R.id.movementGroup).checkedRadioButtonId
                        val movementType = when (movementTypeSelection) {
                            R.id.movementMitchell -> MOVEMENT_MITCHELL
                            R.id.movementHowell -> MOVEMENT_HOWELL
                            R.id.movementNone -> MOVEMENT_NONE
                            else -> MOVEMENT_NONE
                        }

                        val gameMode = if (view.findViewById<Switch>(R.id.modeSwitch).isChecked) {
                            GAMEMODE_PAIRS
                        } else {
                            GAMEMODE_TEAMS
                        }

                        val arrowSwitch = view.findViewById<CheckBox>(R.id.arrowSwitchCheck).isChecked
                        val shareAndRelay = view.findViewById<Switch>(R.id.mitchellTypeSwitch).isChecked

                        gameInfo =
                            GameInfo(tables, gameMode, boards, movementType, arrowSwitch, shareAndRelay, wifiHost.getClientAddresses())

                        wifiHost.startGame(handler)

                    }
                }
            }


        }

    }

}