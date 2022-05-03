package com.OS3.iscorebridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private val TABS = arrayOf(
    "Scores",
    "Boards",
    "Stats"
)

abstract class RefreshableFragment : Fragment(){
    abstract fun refresh(view : View)
}

class SectionsPagerAdapter(var fragment: SpectatorViewFragment) :
    FragmentStateAdapter(fragment) {

    lateinit var childFragment : RefreshableFragment


    override fun createFragment(position: Int): Fragment {
        childFragment = when(position){
            0->OverallScoreViewFragment()
            1->BoardsViewFragment()
            2->StatsViewFragment()
            else->OverallScoreViewFragment()
        }
        return childFragment
    }

    fun refresh(view : View){
        childFragment.refresh(view)
    }

    override fun getItemCount(): Int {
        return TABS.size
    }
}

class SpectatorViewFragment : Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var adapter: SectionsPagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        return inflater.inflate(R.layout.fragment_spectator_view, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = SectionsPagerAdapter(this)
        val tabLayout = view.findViewById<TabLayout>(R.id.spectatorTabLayout)
        viewPager = view.findViewById(R.id.spectatorViewPager)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = TABS[position]
        }.attach()
        viewPager.currentItem = 0
        view.findViewById<FloatingActionButton>(R.id.refreshScoresButton).setOnClickListener {
            adapter.refresh(view)
        }

    }

}