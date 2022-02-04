package com.example.iscorebridge

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController


/**
 * A simple [Fragment] subclass.
 * Use the [WaitToStart.newInstance] factory method to
 * create an instance of this fragment.
 */

const val MESSAGESTART = 8
class WaitToStart : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wait_to_start, container, false)
    }

    private fun wait(view : View){
        Looper.prepare()
        var handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGESTART -> {
                        Looper.myLooper()!!.quit()
                        findNavController().navigate(R.id.waitToStartToScoreEntry)
                    }
                }

            }
        }
        bluetoothService.parentHandler = handler
        Looper.loop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WaitToStart.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WaitToStart().apply {
                arguments = Bundle().apply {
                }
            }
    }
}