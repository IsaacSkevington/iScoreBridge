package com.OS3.iscorebridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


open class EnterTableDetailsFragment : Fragment() {


    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    private fun tableCheck(view:View) : Boolean{
        val tableView = view.findViewById<TextInputEditText>(R.id.tableInput)
        val table = tableView.text.toString()
        if(table == ""){
            tableView.error = "Table must be specified"
            return false
        }
        try{
            val x = table.toInt()
            if(x < 1){
                tableView.error = "Table must be greater than 0"
                return false
            }
        }
        catch (e : Exception){
            view.findViewById<TextInputLayout>(R.id.tableInputLayout).error = "Table must be a number"
            return false
        }
        return true
    }

    fun idCheck(view : View, boxId : Int, layoutID : Int) : Boolean{
        val personID = view.findViewById<TextInputEditText>(boxId).text.toString()
        if(personID == ""){
            view.findViewById<TextInputLayout>(layoutID).error = "ID Number must be specified or 0"
            return false
        }
        try{
            val x = personID.toInt()
            if(x < 0){
                view.findViewById<TextInputLayout>(layoutID).error = "ID must be a positive number"
                return false
            }
        }
        catch (e : Exception){
            view.findViewById<TextInputLayout>(layoutID).error = "ID must be a number"
            return false
        }
        return true
    }

     fun validate(view : View) : Boolean{
        var ret = tableCheck(view)
        ret = ret && idCheck(view, R.id.northInput, R.id.northInputLayout)
        ret = ret && idCheck(view, R.id.eastInput, R.id.eastInputLayout)
        ret = ret && idCheck(view, R.id.southInput, R.id.southInputLayout)
        ret = ret && idCheck(view, R.id.westInput, R.id.westInputLayout)
        return ret

    }

    fun completeJoin(){
        wifiClient.send(CHANGEINFO, myInfo.toString())
        next()
    }

    fun detailsIncorrect(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Some players failed to resolve!!\n" +
                "Select confirm to proceed with the wrong names" +
                "\nNorth: " + myInfo.currentTable.pairNS.p1.name +
                "\nEast: " + myInfo.currentTable.pairEW.p1.name +
                "\nSouth: " + myInfo.currentTable.pairNS.p2.name +
                "\nWest: " + myInfo.currentTable.pairEW.p2.name)

            .setPositiveButton("Confirm"
            ) { _, _ ->
                completeJoin()
            }
            .setNegativeButton("Reject"
            ) { _, _ ->
                Toast.makeText(context, "Names rejected", Toast.LENGTH_LONG).show()
            }
        builder.create()
        builder.show()
    }

    fun tableNumberIncorrect(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Someone with the table number " + myInfo.currentTable.tableNumber + " has already joined")
            .setPositiveButton("Ok"
            ) { _, _ ->
            }
        builder.create()
        builder.show()
    }

    fun confirmDetails(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Players resolved!\n" +
                "\nNorth: " + myInfo.currentTable.pairNS.p1.name +
                "\nEast: " + myInfo.currentTable.pairEW.p1.name +
                "\nSouth: " + myInfo.currentTable.pairNS.p2.name +
                "\nWest: " + myInfo.currentTable.pairEW.p2.name)

            .setPositiveButton("Confirm"
            ) { _, _ ->
                completeJoin()
            }
            .setNegativeButton("Reject"
            ) { _, _ ->
                Toast.makeText(context, "Names rejected", Toast.LENGTH_LONG).show()
            }
        builder.create()
        builder.show()
    }

    open fun next(){
        Toast.makeText(context, "Game entered successfully", Toast.LENGTH_LONG).show()
        wifiClient.send(MESSAGE_JOIN_COMPLETE, myInfo.toString())
        if(amHost){

        }
        if(gameStarted){
            findNavController().navigate(WaitToStartFragmentDirections.waitToStartToScoreEntry(START_BOARDNUMBER))
        }
        else{
            findNavController().navigate(R.id.enterDetailsToWaitToStart)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_enter_table_details, container, false)
    }

    fun handleCommunication(c : Communication){
        var ci = ClientInfo(c.msg)
        myInfo.currentTable.pairNS.p1 = ci.currentTable.pairNS.p1
        myInfo.currentTable.pairEW.p1 = ci.currentTable.pairEW.p1
        myInfo.currentTable.pairNS.p2 = ci.currentTable.pairNS.p2
        myInfo.currentTable.pairEW.p2 = ci.currentTable.pairEW.p2
        if(ci.currentTable.tableNumber == 0){
            tableNumberIncorrect()
        }
        else if(myInfo.currentTable.pairNS.p1.name == PLAYERNOTFOUND ||
            myInfo.currentTable.pairEW.p1.name == PLAYERNOTFOUND ||
            myInfo.currentTable.pairNS.p2.name == PLAYERNOTFOUND ||
            myInfo.currentTable.pairEW.p2.name == PLAYERNOTFOUND){
            detailsIncorrect()
        }
        else{
            confirmDetails()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<FloatingActionButton>(R.id.submitTableDataButton).setOnClickListener {
            if(validate(view)){
                myInfo.currentTable.tableNumber = requireView().findViewById<TextInputEditText>(R.id.tableInput).text.toString().toInt()
                myInfo.currentTable.pairNS.p1 = Player("", requireView().findViewById<TextInputEditText>(R.id.northInput).text.toString().toInt())
                myInfo.currentTable.pairEW.p1 = Player("", requireView().findViewById<TextInputEditText>(R.id.eastInput).text.toString().toInt())
                myInfo.currentTable.pairNS.p2 = Player("", requireView().findViewById<TextInputEditText>(R.id.southInput).text.toString().toInt())
                myInfo.currentTable.pairEW.p2 = Player("", requireView().findViewById<TextInputEditText>(R.id.westInput).text.toString().toInt())
                wifiClient.sendForResponse(CHECKCLIENTDETAILS, myInfo.toString()){
                    handleCommunication(it)
                }
            }
        }

    }


}