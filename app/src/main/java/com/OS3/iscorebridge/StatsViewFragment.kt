package com.OS3.iscorebridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class StatsViewFragment : RefreshableFragment() {

    lateinit var stats : Stats

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_stats_view, container, false)
    }

    override fun refresh(view: View) {
        stats.refresh()
        stats.display(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stats = Stats(MYINFO.myNumber, match)
        stats.display(view)
    }
}