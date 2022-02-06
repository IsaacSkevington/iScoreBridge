package com.example.iscorebridge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController


/**
 * A simple [Fragment] subclass.
 * Use the [ScoreEntryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
var boardNumber = 0
var pairNS = 0
var pairEW = 0
var teams : Boolean = true
class ScoreEntryFragment() : Fragment() {

    private var contractNumber: Int = 0
    var contractSuit: Char = ' '
    var doubled: Boolean = false
    var redoubled: Boolean = false
    @Volatile
    lateinit var match : Match
    lateinit var bts : BluetoothService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun displayContract(){
        var text: String = ""
        if(contractSuit == ' ' || contractNumber == 0){
            text = "No contract selected"
        }
        else{
            text = contractNumber.toString() + contractSuit
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

    private fun setSuit(suit: Char){
        contractSuit = suit
        displayContract()
    }

    private fun setNumber(number: Int){
        contractNumber = number
        displayContract()
    }

    private fun setDouble(){
        redoubled = false
        doubled = true
        displayContract()
    }

    private fun setreDouble(){
        redoubled = true
        doubled = false
        displayContract()
    }



    private fun submit(){
        pairNS = view!!.findViewById<TextView>(R.id.NorthSouth).text.toString().toInt()
        pairEW = view!!.findViewById<TextView>(R.id.EastWest).text.toString().toInt()
        boardNumber = view!!.findViewById<TextView>(R.id.BoardNum).text.toString().toInt()
        match.addGame(  boardNumber,
                        pairNS,
                        pairEW,
                        contractSuit,
                        contractNumber,
                        view!!.findViewById<TextView>(R.id.TricksEntry).text.toString().toInt(),
                        view!!.findViewById<TextView>(R.id.LeadEntry).text.toString(),
                        view!!.findViewById<TextView>(R.id.ByEntry).text[0],
                        doubled,
                        redoubled
        )

        send(SENDMATCH, match.toString())
        findNavController().navigate(R.id.scoreEntryToScoreView)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageButton>(R.id.clubButton).setOnClickListener{
            setSuit('C')
        }
        view.findViewById<ImageButton>(R.id.diamondButton).setOnClickListener{
            setSuit('D')
        }
        view.findViewById<ImageButton>(R.id.heartButton).setOnClickListener{
            setSuit('H')
        }
        view.findViewById<ImageButton>(R.id.spadeButton).setOnClickListener{
            setSuit('S')
        }
        view.findViewById<Button>(R.id.noTrumpButton).setOnClickListener{
            setSuit('N')
        }
        view.findViewById<Button>(R.id.button1).setOnClickListener{
            setNumber(1)
        }
        view.findViewById<Button>(R.id.button2).setOnClickListener{
            setNumber(2)
        }
        view.findViewById<Button>(R.id.button3).setOnClickListener{
            setNumber(3)
        }
        view.findViewById<Button>(R.id.button4).setOnClickListener{
            setNumber(4)
        }
        view.findViewById<Button>(R.id.button5).setOnClickListener{
            setNumber(5)
        }
        view.findViewById<Button>(R.id.button6).setOnClickListener{
            setNumber(6)
        }
        view.findViewById<Button>(R.id.button7).setOnClickListener{
            setNumber(7)
        }
        view.findViewById<Button>(R.id.button7).setOnClickListener{
            setNumber(7)
        }
        view.findViewById<Button>(R.id.DoubleButton).setOnClickListener{
            setDouble()
        }
        view.findViewById<Button>(R.id.reDoubleButton).setOnClickListener{
            setreDouble()
        }
        view.findViewById<Button>(R.id.submitResult).setOnClickListener{
            submit()
        }
    }
}