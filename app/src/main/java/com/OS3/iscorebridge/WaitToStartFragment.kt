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


class WaitToStartFragment : Fragment() {

    var start = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onPause(){
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy(){
        super.onDestroy()
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
                        infoTag.mainActivity.matchHandler.obtainMessage(MESSAGE_START).sendToTarget()
                        myInfo.setup(requireContext())
                        findNavController().navigate(WaitToStartFragmentDirections.waitToStartToScoreEntry(START_BOARDNUMBER))
                    }
                }

            }
        }
        wifiClient.setHandler(handler)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wait(view)

    }

}