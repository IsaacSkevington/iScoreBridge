package com.OS3.iscorebridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment


class BoardsViewFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_boards_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var linearLayout = view.findViewById<LinearLayout>(R.id.boardsDisplayView)

        val fragMan = parentFragmentManager
        val fragTransaction = fragMan.beginTransaction()

        match.boards.forEach {
            fragTransaction.add(linearLayout.id, BoardReviewDisplayFragment(it.value!!, MYINFO.myNumber))
        }
        fragTransaction.commit()
    }
}