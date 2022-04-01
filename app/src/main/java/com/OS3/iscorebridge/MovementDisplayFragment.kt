package com.OS3.iscorebridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class MovementDisplayFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_movement_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.NSmovement).text = gameInfo.movement.getNSMovement(round, MYINFO.tableNumber)
        view.findViewById<TextView>(R.id.EWmovement).text = gameInfo.movement.getEWMovement(round, MYINFO.tableNumber)
        view.findViewById<TextView>(R.id.boardMovement).text = gameInfo.movement.getBoardMovement(round, MYINFO.tableNumber)

        view.findViewById<Button>(R.id.nextRoundButton).setOnClickListener {
            round++
            findNavController().navigate(R.id.movementDisplayToScoreEntry)
        }
    }

}