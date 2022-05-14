package com.OS3.iscorebridge

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

class FinalScoreFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameInfo.match.setupForFragment(this, {}, {})
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_final_score_screen, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createFile() {
        gameInfo.match.export()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myInfo.finishMatch()
        view.findViewById<Button>(R.id.exportPDFButton).setOnClickListener {
            createFile()
        }
    }
}

