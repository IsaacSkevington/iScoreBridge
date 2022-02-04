package com.example.iscorebridge

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import org.w3c.dom.Text


/**
 * A simple [Fragment] subclass.
 * Use the [JoinGame.newInstance] factory method to
 * create an instance of this fragment.
 */

@Volatile var startGame = false
class JoinGame : Fragment() {

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

        return inflater.inflate(R.layout.fragment_join_game, container, false)
    }

    private fun joinGame(view : View){
        var id = view.findViewById<TextView>(R.id.idEntry).text
        Looper.prepare()
        var handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGECONNECTED -> {
                        Looper.myLooper()!!.quit()
                        findNavController().navigate(R.id.joinGameToWaitToStart)
                    }
                }

            }
        }
        var btc = BluetoothClient(id.toString(), handler)
        btc.start()
        Looper.loop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.joinButton).setOnClickListener {
            joinGame(view)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment JoinGame.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            JoinGame().apply {
                arguments = Bundle().apply {
                }
            }
    }
}