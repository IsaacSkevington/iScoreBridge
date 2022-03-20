package com.OS3.iscorebridge

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


var boardNumber = 0
var pairNS = 0
var pairEW = 0
var teams : Boolean = true
var round = 0



class ScoreEntryFragment() : Fragment(){

    lateinit var game : Game

    private var contractNumber: Int = 0
    var contractSuit: Char = ' '
    var doubled: Boolean = false
    var redoubled: Boolean = false
    lateinit var background : Drawable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun displayContract(){
        var text: String = ""
        if(contractSuit == ' ' || contractNumber == 0){
            text = "No contract selected"
        }
        else{
            var suit = if(contractSuit == 'N') "NT"
            else contractSuit.toString()
            text = contractNumber.toString() + suit
            if(doubled){
                text+= 'X'
            }
            else if(redoubled){
                text+="XX"
            }
        }
        val contractView: TextView = view!!.findViewById<TextView>(R.id.contractView)
        contractView.text = text
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setSuit(suit: Char, button: View, view: View){
        view.findViewById<ImageButton>(R.id.clubButton).background = background
        view.findViewById<ImageButton>(R.id.diamondButton).background = background
        view.findViewById<ImageButton>(R.id.heartButton).background = background
        view.findViewById<ImageButton>(R.id.spadeButton).background = background
        view.findViewById<Button>(R.id.noTrumpButton).background = background
        button.background = ColorDrawable(Color.CYAN)
        contractSuit = suit
        displayContract()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setNumber(number: Int, button : View, view: View){
        view.findViewById<Button>(R.id.button1).background = background
        view.findViewById<Button>(R.id.button2).background = background
        view.findViewById<Button>(R.id.button3).background = background
        view.findViewById<Button>(R.id.button4).background = background
        view.findViewById<Button>(R.id.button5).background = background
        view.findViewById<Button>(R.id.button6).background = background
        view.findViewById<Button>(R.id.button7).background = background
        button.background = ColorDrawable(Color.CYAN)
        contractNumber = number
        displayContract()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setDouble(button: View, view: View){
        view.findViewById<Button>(R.id.undoublebutton).background = background
        view.findViewById<Button>(R.id.reDoubleButton).background = background
        button.background = ColorDrawable(Color.CYAN)
        redoubled = false
        doubled = true
        displayContract()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setreDouble(button: View, view: View){
        view.findViewById<Button>(R.id.undoublebutton).background = background
        view.findViewById<Button>(R.id.DoubleButton).background = background
        button.background = ColorDrawable(Color.CYAN)
        redoubled = true
        doubled = false
        displayContract()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setunDouble(button: View, view: View){
        view.findViewById<Button>(R.id.DoubleButton).background = background
        view.findViewById<Button>(R.id.reDoubleButton).background = background
        button.background = ColorDrawable(Color.CYAN)
        redoubled = false
        doubled = false
        displayContract()
    }

    private fun isInt(text : String) : Boolean{
        return try{
            text.toInt()
            true
        } catch(e : Exception){
            false
        }
    }



    private fun boardCheck(view: View) : Boolean{
        var boardText = view.findViewById<TextInputEditText>(R.id.BoardNum).text.toString()
        if(boardText == ""){
            view.findViewById<TextInputLayout>(R.id.BoardNumLayout).error = "Board required"
            return false
        }
        if(!isInt(boardText)){
            view.findViewById<TextInputLayout>(R.id.BoardNumLayout).error = "Board must be number"
            return false
        }
        return true
    }

    private fun pairNSErrorCheck(view: View) : Boolean{
        var pairText = view.findViewById<TextInputEditText>(R.id.NorthSouth).text.toString()
        if(pairText == ""){
            view.findViewById<TextInputLayout>(R.id.NorthSouthLayout).error = "Pair required"
            return false
        }
        if(!isInt(pairText)){
            view.findViewById<TextInputLayout>(R.id.NorthSouthLayout).error = "Pair must be number"
            return false
        }
        return true
    }

    private fun pairEWErrorCheck(view: View) : Boolean{
        var pairText = view.findViewById<TextInputEditText>(R.id.EastWest).text.toString()
        if(pairText == ""){
            view.findViewById<TextInputLayout>(R.id.EastWestLayout).error = "Pair required"
            return false
        }
        if(!isInt(pairText)){
            view.findViewById<TextInputLayout>(R.id.EastWestLayout).error = "Pair must be number"
            return false
        }
        return true
    }

    private fun contractCheck(view: View) : Boolean{
        if(contractSuit == ' ' || contractNumber == 0){
            view!!.findViewById<TextView>(R.id.contractView).setTextColor(Color.parseColor("#DD2C00"))
            return false
        }
        return true
    }

    private fun declarerCheck(view: View) : Boolean{
        var declarerText = view.findViewById<TextInputEditText>(R.id.DeclarerEntry).text.toString()
        if(declarerText == ""){
            view.findViewById<TextInputLayout>(R.id.DeclarerEntryLayout).error = "Declarer required"
            return false
        }
        var d = declarerText[0]
        if(d != 'N' && d != 'E' && d != 'S' && d != 'W'){
            view.findViewById<TextInputLayout>(R.id.DeclarerEntryLayout).error = "Declarer must be N, E, S or W"
            return false
        }
        return true
    }

    private fun trickCheck(view: View) : Boolean{

        var trickText = view.findViewById<TextInputEditText>(R.id.TricksEntry).text.toString()
        if(trickText == ""){
            view.findViewById<TextInputLayout>(R.id.TricksEntryLayout).error = "Trick count required"
            return false
        }
        if(!isInt(trickText)){
            view.findViewById<TextInputLayout>(R.id.TricksEntryLayout).error = "Tricks must be a number"
            return false
        }
        var tricks = trickText.toInt()
        if(tricks < 0){
            view.findViewById<TextInputLayout>(R.id.TricksEntryLayout).error = "Tricks must be a positive number"
            return false
        }
        if(tricks > 13){
            view.findViewById<TextInputLayout>(R.id.TricksEntryLayout).error = "Tricks too large"
            return false
        }

        return true
    }
    private fun leadCheck(view: View) : Boolean{
        var leadText = view.findViewById<TextInputEditText>(R.id.LeadEntry).text.toString()
        if(leadText == ""){
            view.findViewById<TextInputLayout>(R.id.LeadEntryLayout).error = "Lead required"
            return false
        }
        if(!isCard(leadText)){
            view.findViewById<TextInputLayout>(R.id.LeadEntryLayout).error = "Incorrect. E.g. 3S or KC or TH"
            return false
        }
        return true
    }

    private fun errorCheck(view: View) : Boolean{
        view.findViewById<TextInputLayout>(R.id.BoardNumLayout).isErrorEnabled = false
        view.findViewById<TextInputLayout>(R.id.NorthSouthLayout).isErrorEnabled = false
        view.findViewById<TextInputLayout>(R.id.EastWestLayout).isErrorEnabled = false
        view.findViewById<TextInputLayout>(R.id.DeclarerEntryLayout).isErrorEnabled = false
        view.findViewById<TextInputLayout>(R.id.LeadEntryLayout).isErrorEnabled = false
        view.findViewById<TextInputLayout>(R.id.TricksEntryLayout).isErrorEnabled = false
        view!!.findViewById<TextView>(R.id.contractView).setTextColor(Color.parseColor("#000000"))
        var ret = true
        ret = boardCheck(view) && ret
        ret = pairNSErrorCheck(view) && ret
        ret = pairEWErrorCheck(view) && ret
        ret = contractCheck(view) && ret
        ret = declarerCheck(view) && ret
        ret = leadCheck(view) && ret
        ret = trickCheck(view) && ret
        return ret
    }

    private fun logicCheck(view : View) : Boolean{
        var pairNS = view!!.findViewById<TextView>(R.id.NorthSouth).text.toString().toInt()
        var pairEW = view!!.findViewById<TextView>(R.id.EastWest).text.toString().toInt()
        var boardNumber = view!!.findViewById<TextView>(R.id.BoardNum).text.toString().toInt()
        if(match.boards.containsKey(boardNumber)){
            if(match.boards[boardNumber]!!.hasGame(pairNS, pairEW)){
                view.findViewById<TextInputLayout>(R.id.BoardNumLayout).error = "Game already played"
                view.findViewById<TextInputLayout>(R.id.NorthSouthLayout).error = "Game already played"
                view.findViewById<TextInputLayout>(R.id.EastWestLayout).error = "Game already played"
                return false
            }
        }
        return true
    }

    private fun getGame(view : View) : Game{
        pairNS = view!!.findViewById<TextView>(R.id.NorthSouth).text.toString().toInt()
        pairEW = view!!.findViewById<TextView>(R.id.EastWest).text.toString().toInt()
        boardNumber = view!!.findViewById<TextView>(R.id.BoardNum).text.toString().toInt()
        return match.getGame(  boardNumber,
            pairNS,
            pairEW,
            contractSuit,
            contractNumber,
            view!!.findViewById<TextView>(R.id.TricksEntry).text.toString().toInt(),
            view!!.findViewById<TextView>(R.id.LeadEntry).text.toString(),
            view!!.findViewById<TextView>(R.id.DeclarerEntry).text[0],
            doubled,
            redoubled
        )
    }

    private fun submit(game : Game){

        match.addGame(game)
        wifiService.send(SENDGAME, game.toString())
        round++
        findNavController().navigate(R.id.scoreEntryToScoreView)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_score_entry, container, false)
    }





    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.undoublebutton).background = ColorDrawable(Color.CYAN)

        if(gameInfo.movementType == MOVEMENT_NONE){
            if(pairNS != 0 && pairEW != 0 && boardNumber != 0){
                view!!.findViewById<TextView>(R.id.NorthSouth).text = pairNS.toString()
                view!!.findViewById<TextView>(R.id.EastWest).text = pairEW.toString()
                view!!.findViewById<TextView>(R.id.BoardNum).text = (boardNumber + 1).toString()
            }
        }

        else{
            //Implement movement display here
        }

        background = view.findViewById<ImageButton>(R.id.clubButton).background

        view.findViewById<ImageButton>(R.id.clubButton).setOnClickListener{
            setSuit('C', it, view)
        }
        view.findViewById<ImageButton>(R.id.diamondButton).setOnClickListener{
            setSuit('D', it, view)
        }
        view.findViewById<ImageButton>(R.id.heartButton).setOnClickListener{
            setSuit('H', it, view)
        }
        view.findViewById<ImageButton>(R.id.spadeButton).setOnClickListener{
            setSuit('S', it, view)
        }
        view.findViewById<Button>(R.id.noTrumpButton).setOnClickListener{
            setSuit('N', it, view)
        }
        view.findViewById<Button>(R.id.button1).setOnClickListener{
            setNumber(1, it, view)
        }
        view.findViewById<Button>(R.id.button2).setOnClickListener{
            setNumber(2, it, view)
        }
        view.findViewById<Button>(R.id.button3).setOnClickListener{
            setNumber(3, it, view)
        }
        view.findViewById<Button>(R.id.button4).setOnClickListener{
            setNumber(4, it, view)
        }
        view.findViewById<Button>(R.id.button5).setOnClickListener{
            setNumber(5, it, view)
        }
        view.findViewById<Button>(R.id.button6).setOnClickListener{
            setNumber(6, it, view)
        }
        view.findViewById<Button>(R.id.button7).setOnClickListener{
            setNumber(7, it, view)
        }
        view.findViewById<Button>(R.id.DoubleButton).setOnClickListener{
            setDouble(it, view)
        }
        view.findViewById<Button>(R.id.reDoubleButton).setOnClickListener{
            setreDouble(it, view)
        }
        view.findViewById<Button>(R.id.undoublebutton).setOnClickListener{
            setunDouble(it, view)
        }
        view.findViewById<Button>(R.id.submitResult).setOnClickListener{
            if(errorCheck(view)) {
                if(logicCheck(view)){
                    game = getGame(view)
                    val builder = AlertDialog.Builder(view.context)
                    var resultString = when {
                        game.tricks - (game.contract.number + 6) == 0 -> {
                            "="
                        }
                        game.tricks - (game.contract.number + 6) < 0 -> {
                            (game.tricks - (game.contract.number + 6)).toString()
                        }
                        else -> {
                            "+" + (game.tricks - (game.contract.number + 6)).toString()
                        }
                    }

                    builder.setMessage("Confirmation required!" +
                            "\nBoard: " + game.boardNumber.toString() + " (NS " + game.pairNS.toString() + " vs EW " + game.pairEW.toString() + ")" +
                            "\nContract: " + game.contract.toDisplayString(false) + " by " + game.contract.declarer +
                            "\nTricks: " + game.tricks.toString() + " (" + resultString + ")" +
                            "\nScore: " + game.score.toString())
                        .setPositiveButton("Confirm",
                            DialogInterface.OnClickListener { dialog, id ->
                                submit(game)

                            })
                        .setNegativeButton("Reject",
                            DialogInterface.OnClickListener { dialog, id ->

                            })
                    builder.create()
                    builder.show()
                }
            }
        }
    }
}