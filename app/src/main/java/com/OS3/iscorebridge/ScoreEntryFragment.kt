package com.OS3.iscorebridge

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


var teams : Boolean = true



class ScoreEntryFragment : Fragment(){

    var suitIdMap = mapOf<Suit, Int>(
        CLUBS to R.id.clubButton,
        DIAMONDS to R.id.diamondButton,
        HEARTS to R.id.heartButton,
        SPADES to R.id.spadeButton,
        NOTRUMPS to R.id.noTrumpButton
    )

    var numberIdMap = mapOf<Int, Int>(
        1 to R.id.button1,
        2 to R.id.button2,
        3 to R.id.button3,
        4 to R.id.button4,
        5 to R.id.button5,
        6 to R.id.button6,
        7 to R.id.button7
    )

    companion object{
        fun newInstance(game : Game): ScoreEntryFragment{
            var ret = ScoreEntryFragment()
            ret.game = game
            return ret
        }
    }


    var game : Game? = null

    private var contractNumber: Int = 0
    private var contractSuit: Suit? = null
    private var doubled: Boolean = false
    private var redoubled: Boolean = false
    private lateinit var background : Drawable
    var boardNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args : ScoreEntryFragmentArgs by navArgs()
        boardNumber = args.boardNumber
    }

    private fun displayContract(view : View){
        var text: String
        if(contractSuit == null || contractNumber == 0){
            text = "No contract selected"
        }
        else{
            text = contractNumber.toString() + contractSuit.toString()
            if(doubled){
                text+= 'X'
            }
            else if(redoubled){
                text+="XX"
            }
        }
        val contractView: TextView = view.findViewById(R.id.contractView)
        contractView.text = text
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setSuit(suit: Suit, view: View){
        view.findViewById<ImageButton>(R.id.clubButton).background = background
        view.findViewById<ImageButton>(R.id.diamondButton).background = background
        view.findViewById<ImageButton>(R.id.heartButton).background = background
        view.findViewById<ImageButton>(R.id.spadeButton).background = background
        view.findViewById<Button>(R.id.noTrumpButton).background = background
        view.findViewById<Button>(suitIdMap[suit]!!).background = ColorDrawable(Color.CYAN)
        contractSuit = Suit(suit)
        displayContract(view)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setNumber(number: Int, view: View){
        view.findViewById<Button>(R.id.button1).background = background
        view.findViewById<Button>(R.id.button2).background = background
        view.findViewById<Button>(R.id.button3).background = background
        view.findViewById<Button>(R.id.button4).background = background
        view.findViewById<Button>(R.id.button5).background = background
        view.findViewById<Button>(R.id.button6).background = background
        view.findViewById<Button>(R.id.button7).background = background
        view.findViewById<Button>(numberIdMap[number]!!).background = ColorDrawable(Color.CYAN)
        contractNumber = number
        displayContract(view)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setDouble(view: View){
        view.findViewById<Button>(R.id.undoublebutton).background = background
        view.findViewById<Button>(R.id.reDoubleButton).background = background
        view.findViewById<Button>(R.id.DoubleButton).background = ColorDrawable(Color.CYAN)
        redoubled = false
        doubled = true
        displayContract(view)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setreDouble(view: View){
        view.findViewById<Button>(R.id.undoublebutton).background = background
        view.findViewById<Button>(R.id.DoubleButton).background = background
        view.findViewById<Button>(R.id.reDoubleButton).background = ColorDrawable(Color.CYAN)
        redoubled = true
        doubled = false
        displayContract(view)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setunDouble(view: View){
        view.findViewById<Button>(R.id.DoubleButton).background = background
        view.findViewById<Button>(R.id.reDoubleButton).background = background
        view.findViewById<Button>(R.id.undoublebutton).background = ColorDrawable(Color.CYAN)
        redoubled = false
        doubled = false
        displayContract(view)
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
        val boardText = view.findViewById<TextInputEditText>(R.id.BoardNum).text.toString()
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
        val pairText = view.findViewById<TextInputEditText>(R.id.NorthSouth).text.toString()
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
        val pairText = view.findViewById<TextInputEditText>(R.id.EastWest).text.toString()
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
        if(contractSuit == null || contractNumber == 0){
            view.findViewById<TextView>(R.id.contractView).setTextColor(Color.parseColor("#DD2C00"))
            return false
        }
        return true
    }

    private fun declarerCheck(view: View) : Boolean{
        val declarerText = view.findViewById<TextInputEditText>(R.id.DeclarerEntry).text.toString()
        if(declarerText == ""){
            view.findViewById<TextInputLayout>(R.id.DeclarerEntryLayout).error = "Declarer required"
            return false
        }
        val d = declarerText[0]
        if(d != 'N' && d != 'E' && d != 'S' && d != 'W'){
            view.findViewById<TextInputLayout>(R.id.DeclarerEntryLayout).error = "Declarer must be N, E, S or W"
            return false
        }
        return true
    }

    private fun trickCheck(view: View) : Boolean{

        val trickText = view.findViewById<TextInputEditText>(R.id.TricksEntry).text.toString()
        if(trickText == ""){
            view.findViewById<TextInputLayout>(R.id.TricksEntryLayout).error = "Trick count required"
            return false
        }
        if(!isInt(trickText)){
            view.findViewById<TextInputLayout>(R.id.TricksEntryLayout).error = "Tricks must be a number"
            return false
        }
        val tricks = trickText.toInt()
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
        val leadText = view.findViewById<TextInputEditText>(R.id.LeadEntry).text.toString()
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
        view.findViewById<TextView>(R.id.contractView).setTextColor(Color.parseColor("#000000"))
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
        val pairNS = view.findViewById<TextView>(R.id.NorthSouth).text.toString().toInt()
        val pairEW = view.findViewById<TextView>(R.id.EastWest).text.toString().toInt()
        val boardNumber = view.findViewById<TextView>(R.id.BoardNum).text.toString().toInt()
        if(gameInfo.match.boards.containsKey(boardNumber)){
            if(gameInfo.match.boards[boardNumber]!!.hasGame(myInfo.currentTable.pairNS, myInfo.currentTable.pairEW)){
                view.findViewById<TextInputLayout>(R.id.BoardNumLayout).error = "Game already played"
                view.findViewById<TextInputLayout>(R.id.NorthSouthLayout).error = "Game already played"
                view.findViewById<TextInputLayout>(R.id.EastWestLayout).error = "Game already played"
                return false
            }
        }
        return true
    }

    private fun getGame(view : View) : Game{
        var pairNS = gameInfo.getPlayerPair(view.findViewById<TextView>(R.id.NorthSouth).text.toString().toInt())!!
        var pairEW = gameInfo.getPlayerPair(view.findViewById<TextView>(R.id.EastWest).text.toString().toInt())!!
        boardNumber = view.findViewById<TextView>(R.id.BoardNum).text.toString().toInt()
        return gameInfo.match.getGame(  boardNumber,
            pairNS,
            pairEW,
            contractSuit!!,
            contractNumber,
            view.findViewById<TextView>(R.id.TricksEntry).text.toString().toInt(),
            view.findViewById<TextView>(R.id.LeadEntry).text.toString(),
            Cardinality(view.findViewById<TextView>(R.id.DeclarerEntry).text.toString()[0]),
            doubled,
            redoubled
        )
    }

    private fun submit(game : Game){

        gameInfo.match.addGame(game)
        wifiService.send(MESSAGE_SEND_GAME, game.toString())
        var action = ScoreEntryFragmentDirections.scoreEntryToScoreView(boardNumber)
        findNavController().navigate(action)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val args : ScoreEntryFragmentArgs by navArgs()
        this.boardNumber = args.boardNumber

        return inflater.inflate(R.layout.fragment_score_entry, container, false)
    }

    fun nextRound(){
        if(myInfo.hasNextRound()) {
            var action = ScoreEntryFragmentDirections.scoreEntryToMovementDisplay(boardNumber)
            findNavController().navigate(action)
        }
        else{
            infoTag.clear()
            AlertDialog.Builder(requireContext())
                .setTitle("Match Finished")
                .setMessage("You have completed all the boards, time to take a look at the scores!")
                .setPositiveButton("Ok"){_, _ ->}
                .create().show()
            findNavController().navigate(R.id.scoreEntryToFinalScore)
        }
    }


    fun loadGame(view: View){
        view.findViewById<TextView>(R.id.NorthSouth).text = game!!.pairNS.displayNumber.toString()
        view.findViewById<TextView>(R.id.EastWest).text = game!!.pairEW.displayNumber.toString()
        view.findViewById<TextView>(R.id.BoardNum).text = game!!.boardNumber.toString()
        game!!.contract.also {
            setSuit(it.suit, view)
            setNumber(it.number, view)
            when {
                it.doubled -> {
                    setDouble(view)
                }
                it.redoubled -> {
                    setreDouble(view)
                }
                else -> {
                    setunDouble(view)
                }
            }
            view.findViewById<TextView>(R.id.DeclarerEntry).text = it.declarer.toDisplayString()



        }
        view.findViewById<TextView>(R.id.TricksEntry).text = game!!.tricks.toString()
        view.findViewById<TextView>(R.id.LeadEntry).text = game!!.lead.toString()
    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.undoublebutton).background = ColorDrawable(Color.CYAN)
        gameStarted = true
        if(game != null){
            loadGame(view)
        }
        else {
            boardNumber = gameInfo.match.getNextUnplayedBoard(boardNumber)
            if (boardNumber == 0) {
                nextRound()
                return
            }
            if(finishRoundImmediately){
                finishRoundImmediately = false
                gameInfo.match.zeroRemainingBoards(boardNumber)
                nextRound()
                return
            }
            view.findViewById<TextView>(R.id.NorthSouth).text =
                myInfo.currentTable.pairNS.toString()
            view.findViewById<TextView>(R.id.EastWest).text = myInfo.currentTable.pairEW.toString()
            view.findViewById<TextView>(R.id.BoardNum).text = boardNumber.toString()
        }

        background = view.findViewById<ImageButton>(R.id.clubButton).background

        view.findViewById<ImageButton>(R.id.clubButton).setOnClickListener{
            setSuit(CLUBS, view)
        }
        view.findViewById<ImageButton>(R.id.diamondButton).setOnClickListener{
            setSuit(DIAMONDS, view)
        }
        view.findViewById<ImageButton>(R.id.heartButton).setOnClickListener{
            setSuit(HEARTS, view)
        }
        view.findViewById<ImageButton>(R.id.spadeButton).setOnClickListener{
            setSuit(SPADES, view)
        }
        view.findViewById<Button>(R.id.noTrumpButton).setOnClickListener{
            setSuit(NOTRUMPS, view)
        }
        view.findViewById<Button>(R.id.button1).setOnClickListener{
            setNumber(1, view)
        }
        view.findViewById<Button>(R.id.button2).setOnClickListener{
            setNumber(2, view)
        }
        view.findViewById<Button>(R.id.button3).setOnClickListener{
            setNumber(3, view)
        }
        view.findViewById<Button>(R.id.button4).setOnClickListener{
            setNumber(4, view)
        }
        view.findViewById<Button>(R.id.button5).setOnClickListener{
            setNumber(5, view)
        }
        view.findViewById<Button>(R.id.button6).setOnClickListener{
            setNumber(6, view)
        }
        view.findViewById<Button>(R.id.button7).setOnClickListener{
            setNumber(7, view)
        }
        view.findViewById<Button>(R.id.DoubleButton).setOnClickListener{
            setDouble(view)
        }
        view.findViewById<Button>(R.id.reDoubleButton).setOnClickListener{
            setreDouble(view)
        }
        view.findViewById<Button>(R.id.undoublebutton).setOnClickListener{
            setunDouble(view)
        }
        view.findViewById<FloatingActionButton>(R.id.submitResult).setOnClickListener{
            if(errorCheck(view)) {
                if (logicCheck(view)) {
                    getGame(view).also {game ->
                        this.game = game
                        val resultString = when {
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
                        val builder = AlertDialog.Builder(view.context)
                        builder.setMessage(
                            "Confirmation required!" +
                                    "\nBoard: " + game.boardNumber.toString() + " (NS " + game.pairNS.toString() + " vs EW " + game.pairEW.toString() + ")" +
                                    "\nContract: " + game.contract.toDisplayString(false) + " by " + game.contract.declarer +
                                    "\nTricks: " + game.tricks.toString() + " (" + resultString + ")" +
                                    "\nScore: " + game.score.toString()
                        )
                            .setPositiveButton(
                                "Confirm"
                            ) { _, _ ->
                                submit(game)
                            }
                            .setNegativeButton(
                                "Reject"
                            ) { _, _ ->

                            }
                        builder.create()
                        builder.show()
                    }
                }
            }
        }
    }
}