package com.OS3.iscorebridge

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class EnterTableDetailsFragment : Fragment() {


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

    fun detailsIncorrect(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Some players failed to resolve!!\n" +
                "Select confirm to proceed with the wrong names" +
                "\nNorth: " + MYINFO.north.name +
                "\nEast: " + MYINFO.east.name +
                "\nSouth: " + MYINFO.south.name +
                "\nWest: " + MYINFO.west.name)

            .setPositiveButton("Confirm"
            ) { _, _ ->
                next()
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
        builder.setMessage("Someone with the table number " + MYINFO.tableNumber + " has already joined")
            .setPositiveButton("Ok"
            ) { _, _ ->
            }
        builder.create()
        builder.show()
    }

    fun confirmDetails(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Players resolved!\n" +
                "North: " + MYINFO.north.name +
                "\nEast: " + MYINFO.east.name +
                "\nSouth: " + MYINFO.south.name +
                "\nWest: " + MYINFO.west.name)

            .setPositiveButton("Confirm"
            ) { _, _ ->
                next()
            }
            .setNegativeButton("Reject"
            ) { _, _ ->
                Toast.makeText(context, "Names rejected", Toast.LENGTH_LONG).show()
            }
        builder.create()
        builder.show()
    }

    fun next(){
        Toast.makeText(context, "Game entered successfully", Toast.LENGTH_LONG).show()
        wifiClient.send(SENDCLIENTDETAILS, MYINFO.toString())
        findNavController().navigate(R.id.enterDetailsToWaitToString)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_enter_table_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_CLIENT_DETAILS_OBTAINED -> {
                        var ci = msg.obj as ClientInfo
                        MYINFO.north = ci.north
                        MYINFO.east = ci.east
                        MYINFO.south = ci.south
                        MYINFO.west = ci.west
                        if(ci.tableNumber == 0){
                            tableNumberIncorrect()
                        }
                        else if(ci.north.name == PLAYERNOTFOUND ||
                            ci.east.name == PLAYERNOTFOUND ||
                            ci.south.name == PLAYERNOTFOUND ||
                            ci.west.name == PLAYERNOTFOUND){
                            detailsIncorrect()
                        }
                        else{
                            confirmDetails()
                        }
                    }
                }

            }
        }

        wifiClient.setHandler(handler)

        view.findViewById<Button>(R.id.submitTableDataButton).setOnClickListener {
            if(validate(view)){
                MYINFO.tableNumber = requireView().findViewById<TextInputEditText>(R.id.tableInput).text.toString().toInt()
                MYINFO.north = Player("", requireView().findViewById<TextInputEditText>(R.id.northInput).text.toString().toInt())
                MYINFO.east = Player("", requireView().findViewById<TextInputEditText>(R.id.eastInput).text.toString().toInt())
                MYINFO.south = Player("", requireView().findViewById<TextInputEditText>(R.id.southInput).text.toString().toInt())
                MYINFO.west = Player("", requireView().findViewById<TextInputEditText>(R.id.westInput).text.toString().toInt())
                wifiClient.send(CHECKCLIENTDETAILS, MYINFO.toString())
            }
        }

    }


}