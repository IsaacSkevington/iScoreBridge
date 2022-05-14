package com.OS3.iscorebridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout


class BoardsViewFragment : RefreshableFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_boards_view, container, false)
    }

    override fun refresh(view: View) {
        var linearLayout = view.findViewById<LinearLayout>(R.id.boardsDisplayView)

        val fragMan = parentFragmentManager
        val fragTransaction = fragMan.beginTransaction()

        gameInfo.match.boards.forEach {
            if(it.value!!.playedBy(myInfo.myPair) || myInfo.finished) {
                fragTransaction.add(
                    linearLayout.id,
                    BoardReviewDisplayFragment.newInstance(it.value!!, myInfo.myPair)
                )
            }
        }
        fragTransaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh(view)
    }
}