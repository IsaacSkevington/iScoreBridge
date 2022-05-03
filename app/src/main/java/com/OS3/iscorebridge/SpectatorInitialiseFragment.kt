package com.OS3.iscorebridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

class SpectatorInitialiseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spectator_initialise, container, false)
    }

    fun checkSpectatorDetails(view : View) : Boolean{
        var tableNumber = view.findViewById<EditText>(R.id.spectatorTableNumberEntry).text.toString().toInt()
        var playerNumber = view.findViewById<EditText>(R.id.spectatorPlayerNumberEntry).text.toString().toInt()
        var cardinality = if(view.findViewById<Switch>(R.id.spectatorCardinalitySwitch).isChecked) EASTWEST
                            else NORTHSOUTH
        var info = SpectatorInfo(tableNumber, playerNumber, cardinality)
        var response = wifiClient.sendForResponse(CHECKSPECTATORDETAILS, info.toString())
        var responseInfo = SpectatorInfo(response.msg)
        return if(responseInfo.confirmation){
            MYINFO.tableNumber = tableNumber
            MYINFO.myNumber = responseInfo.tableNumber
            true
        }
        else{
            false
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.beginSpectatingButton).setOnClickListener {
            if(checkSpectatorDetails(view)){
                findNavController().navigate(R.id.SpectatorInitialiseToSpectatorView)
            }
            else{
                view.findViewById<TextInputLayout>(R.id.spectatorTableNumberEntryLayout).error = "Details don't match"
                view.findViewById<TextInputLayout>(R.id.spectatorPlayerNumberEntryLayout).error = "Details don't match"
            }
        }
    }
}