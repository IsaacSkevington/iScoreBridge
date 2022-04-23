package com.OS3.iscorebridge

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs


class BoardScoreViewFragment : Fragment() {

    var boardNumber = 0
    var pairNS = 0
    var pairEW = 0
    lateinit var board : Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args : BoardScoreViewFragmentArgs by navArgs()
        this.boardNumber = args.boardNumber
        this.pairNS = args.pairNS
        this.pairEW = args.pairEW
        this.board = match.boards[boardNumber]!!

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board_score_view, container, false)
    }





    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<Button>(R.id.nextScoreEntryButton).setOnClickListener {
            var action = BoardScoreViewFragmentDirections.scoreViewToScoreEntry(boardNumber, pairNS, pairEW)
            findNavController().navigate(action)
        }
        view.findViewById<Button>(R.id.finishButton).setOnClickListener {
            findNavController().navigate(R.id.scoreViewToFinalScore)
        }

        this.board.displayScore(parentFragmentManager, pairNS, pairEW, view, layoutInflater)
    }

}

