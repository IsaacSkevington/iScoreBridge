package com.OS3.iscorebridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BiddingConstructor() : DialogFragment() {

    lateinit var bidding : Bidding
    var boardNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_bidding_constructor, container, false)
    }

    fun bidPressed(bid : Bid, view : View){
        bidding.add(bid)
        bidding.display(view)
    }
    fun bidPressed(button : View, view:View){
        bidding.add(getBid(button as Button))

        bidding.display(view)
    }

    fun undo(view : View){
        bidding.pop()
        bidding.display(view)
    }

    var done = {view:View ->
        bidding.allPass()
        bidding.display(view)
    }

    var close = {view:View ->

    }


    fun updateBoardNumber(num : Int){
        this.boardNumber = num
        try{
            bidding.display(requireView())
        }
        catch(e : UninitializedPropertyAccessException){

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        bidding = if(boardNumber == 0){
            Bidding(NORTH)
        }
        else{
            Bidding(getDealer(boardNumber))
        }
        bidding.display(view)

        view.findViewById<Button>(R.id.bidding_1_club).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_2_club).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_3_club).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_4_club).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_5_club).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_6_club).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_7_club).setOnClickListener {
            bidPressed(it, view)
        }

        view.findViewById<Button>(R.id.bidding_1_diamond).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_2_diamond).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_3_diamond).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_4_diamond).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_5_diamond).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_6_diamond).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_7_diamond).setOnClickListener {
            bidPressed(it, view)
        }


        view.findViewById<Button>(R.id.bidding_1_heart).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_2_heart).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_3_heart).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_4_heart).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_5_heart).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_6_heart).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_7_heart).setOnClickListener {
            bidPressed(it, view)
        }


        view.findViewById<Button>(R.id.bidding_1_spade).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_2_spade).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_3_spade).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_4_spade).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_5_spade).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_6_spade).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_7_spade).setOnClickListener {
            bidPressed(it, view)
        }


        view.findViewById<Button>(R.id.bidding_1_NT).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_2_NT).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_3_NT).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_4_NT).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_5_NT).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_6_NT).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_7_NT).setOnClickListener {
            bidPressed(it, view)
        }
        view.findViewById<Button>(R.id.bidding_pass).setOnClickListener {
            bidPressed(PASS, view)
        }
        view.findViewById<Button>(R.id.bidding_double).setOnClickListener {
            bidPressed(DOUBLE, view)
        }
        view.findViewById<Button>(R.id.bidding_redouble).setOnClickListener {
            bidPressed(REDOUBLE, view)
        }

        view.findViewById<FloatingActionButton>(R.id.undoButton).setOnClickListener{
            undo(view)
        }

        view.findViewById<FloatingActionButton>(R.id.doneButton).setOnClickListener {
            done(view)
        }
        view.findViewById<FloatingActionButton>(R.id.closeBiddingButton).setOnClickListener{
            close(view)
        }
    }

    /*
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle("Creating bidding for board $boardNumber")
            .create()
    }

     */

}

fun showBiddingConstructorDiag(fragmentManager: FragmentManager, boardNumber: Int, onSuccess : (bidding : Bidding) -> Unit) {
    BiddingConstructor().also{

        it.updateBoardNumber(boardNumber)
        it.show(fragmentManager, "dialog")

        it.close = {_->
            it.dismiss()
        }
        it.done = {_->
            it.bidding.allPass()
            onSuccess(it.bidding)
            it.dismiss()
            Toast.makeText(it.context, "Bidding created successfully", Toast.LENGTH_LONG).show()

        }
    }
}