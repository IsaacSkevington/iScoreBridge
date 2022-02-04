package com.example.iscorebridge

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController

private const val REQUEST_ENABLE_BT = 1

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@Volatile lateinit var bluetoothAdapter : BluetoothAdapter
class HomePage : Fragment() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                findNavController().navigate(R.id.homeToStart)
            }
            else{
                Toast.makeText(context, "Bluetooth permissions incorrect", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun setupBluetooth(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            var btm = context!!.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = btm.adapter

        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if(bluetoothAdapter == null){
            return
        }
        if (!bluetoothAdapter?.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        else{
            findNavController().navigate(R.id.homeToStart)
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.playButton).setOnClickListener {
            setupBluetooth()
        }
        view.findViewById<Button>(R.id.joingamebutton).setOnClickListener {
            findNavController().navigate(R.id.homeToJoin)
        }
    }
}