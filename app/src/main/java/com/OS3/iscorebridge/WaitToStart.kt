package com.OS3.iscorebridge

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class WaitToStart : Fragment() {

    var start = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wait_to_start, container, false)
    }

    private fun wait(view : View){

        var handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_START -> {
                        gameInfo = msg.obj as GameInfo
                        wifiService.clientList = gameInfo.clientList
                        round = 1

                        if(!start) {
                            start = true
                            findNavController().navigate(R.id.waitToStartToScoreEntry)
                        }

                    }
                    MESSAGECONNECTEDWRITER ->{

                    }
                }

            }
        }
        wifiService.setHandler(handler)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wait(view)

    }

}