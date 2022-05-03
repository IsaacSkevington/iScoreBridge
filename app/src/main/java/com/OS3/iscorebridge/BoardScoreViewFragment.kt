package com.OS3.iscorebridge

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton


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


        view.findViewById<FloatingActionButton>(R.id.nextScoreEntryButton).setOnClickListener {
            var action = BoardScoreViewFragmentDirections.scoreViewToScoreEntry(boardNumber, pairNS, pairEW)
            findNavController().navigate(action)
        }
        view.findViewById<FloatingActionButton>(R.id.finishButton).setOnClickListener {
            var builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Finish Match?")
                .setMessage("Are you sure you want to finish the match? You will not be able to score any more boards after this")
                .setPositiveButton("Yes"){_, _ ->
                    MYINFO.finishMatch()
                    findNavController().navigate(R.id.scoreViewToFinalScore)
                }
                .setNegativeButton("No"){_, _ ->
                }

        }

        this.board.displayScore(parentFragmentManager, pairNS, pairEW, view, layoutInflater)
    }

}

