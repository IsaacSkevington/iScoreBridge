package com.example.iscorebridge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import org.w3c.dom.Text


/**
 * A simple [Fragment] subclass.
 * Use the [ScoreEntryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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



    private fun submit(){
        match.addGame(view!!.findViewById<TextView>(R.id.BoardNum).text.toString().toInt(),
                        view!!.findViewById<TextView>(R.id.NorthSouth).text.toString().toInt(),
                        view!!.findViewById<TextView>(R.id.EastWest).text.toString().toInt(),
                        contractSuit,
                        contractNumber,
                        view!!.findViewById<TextView>(R.id.TricksEntry).text.toString().toInt(),
                        view!!.findViewById<TextView>(R.id.LeadEntry).text.toString(),
                        view!!.findViewById<TextView>(R.id.ByEntry).text[0],
                        doubled,
                        redoubled
        )
        val matchUpdateMessage = bts.childHandler.obtainMessage(
            MESSAGE_SEND, 0, 0,
            match
        )
        matchUpdateMessage.sendToTarget()

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
        view.findViewById<Button>(R.id.submitResult).setOnClickListener{
            submit()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScoreEntryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(match : SharedMatch) =
            ScoreEntryFragment().apply {
                arguments = Bundle().apply {
                    match
                }
            }
    }
}