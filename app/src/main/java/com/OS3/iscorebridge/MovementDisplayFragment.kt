package com.OS3.iscorebridge

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class MovementDisplayFragment : Fragment() {

    var boardNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args : MovementDisplayFragmentArgs by navArgs()
        this.boardNumber = args.boardNumber
        return inflater.inflate(R.layout.fragment_movement_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        infoTag.clear()
        view.findViewById<TextView>(R.id.NSmovement).text = myInfo.getNSMovement()
        view.findViewById<TextView>(R.id.EWmovement).text = myInfo.getEWMovement()
        view.findViewById<TextView>(R.id.boardMovement).text = myInfo.getBoardMovement()

        view.findViewById<Button>(R.id.nextRoundButton).setOnClickListener {
            myInfo.nextRound()
            infoTag.setMessage(myInfo.getRoundInfo())
            infoTag.setOnClickListener {
                var timer = infoTag.mainActivity.roundTimer
                var layout = LinearLayout(requireContext())
                layout.orientation = VERTICAL
                var timerview = TextView(requireContext())
                timer.displayIn(timerview)
                layout.addView(timerview)
                AlertDialog.Builder(requireContext())
                    .setTitle("Round details")
                    .setView(layout)
                    .setPositiveButton("Ok"){_, _ ->}
                    .create().show()

            }
            var action = MovementDisplayFragmentDirections.movementDisplayToScoreEntry(0)
            findNavController().navigate(action)
        }
    }

}