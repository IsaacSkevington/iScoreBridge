package com.OS3.iscorebridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.floatingactionbutton.FloatingActionButton


class SettingsFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    fun validateSettings(view : View) : Boolean{
        return true
    }
    fun setSettings(view : View){
        try {
            var pin = view.findViewById<TextView>(R.id.pinChangeBox).text.toString().toInt()
            SETTINGS.userPin = pin
            SETTINGS.save(requireContext())
        }
        catch(e : NumberFormatException){}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.settingsEditCompleteButton).setOnClickListener {
            if(validateSettings(view)){
                setSettings(view)
                Toast.makeText(requireContext(), "Settings Saved!", Toast.LENGTH_LONG)
                dismiss()
            }
        }
        view.findViewById<TextView>(R.id.pinChangeBox).text = if(SETTINGS.pinSet()){
            SETTINGS.userPin.toString()
        }
        else{
            ""
        }
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}

fun showSettingsDiag(fragmentManager: FragmentManager){
    SettingsFragment().also{
        it.show(fragmentManager, "Settings Diag")


    }
}