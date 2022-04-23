package com.OS3.iscorebridge

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BoardReviewDisplayFragment(var board : Board, var myPair : Int) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board_review_display, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.boardNumberText).text = "Board ${board.boardNumber}"
        view.findViewById<TextView>(R.id.scoreSummaryText).text = board.getDisplayScore(myPair)
        view.findViewById<FloatingActionButton>(R.id.boardMoreDetailsButton).setOnClickListener {
            var boardDetailsView = layoutInflater.inflate(R.layout.board_detail_display, null)
            board.displayScore(parentFragmentManager, myPair, boardDetailsView, layoutInflater)
            var builder = AlertDialog.Builder(view.context)
            builder.setMessage("Board " + board.boardNumber)
                .setPositiveButton("Ok") { _, _ ->

                }
                .setView(boardDetailsView)
            .create().show()
        }
        board.setStarListener(view.findViewById(R.id.reviewStarButton))

    }


}