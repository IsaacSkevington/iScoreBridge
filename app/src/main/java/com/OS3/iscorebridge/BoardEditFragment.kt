package com.OS3.iscorebridge

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BoardEditFragment : DialogFragment() {

    var diag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_board_edit, container, false)
    }

    fun initDiag(){
        diag = true
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(diag){
            view.findViewById<FloatingActionButton>(R.id.closeEditBoardButton).also {
                it.visibility = VISIBLE
                it.isClickable = true
                it.setOnClickListener {
                    dismiss()
                }
            }
        }
        view.findViewById<FloatingActionButton>(R.id.directorBoardEditButton).setOnClickListener {
            var board = view.findViewById<TextView>(R.id.boardNumberEditBox).text.toString().toInt()
            var pairNSNumber = view.findViewById<TextView>(R.id.northSouthPairEditBox).text.toString().toInt()
            var pairNS = PlayerPair(pairNSNumber)
            var game = gameInfo.match.boards[board]?.getGame(pairNS)
            if(game != null) {
                var fragman = childFragmentManager
                var transaction = fragman.beginTransaction()
                transaction.add(R.id.scoreEntryContainer, ScoreEntryFragment.newInstance(game!!))
                transaction.commit()
            }
            else{
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage("No such board has been played")
                    .setPositiveButton("Ok"){_, _ ->}
                    .create()
                    .show()
            }
        }

    }
}


fun showBoardEditDiag(fragmentManager: FragmentManager) {
    BoardEditFragment().also{
        it.show(fragmentManager, "dialog")
        it.initDiag()
    }
}