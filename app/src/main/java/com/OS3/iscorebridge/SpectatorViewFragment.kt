package com.OS3.iscorebridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private val TABS = arrayOf(
    "Scores",
    "Boards",
    "Stats"
)

class SectionsPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {


    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->OverallScoreViewFragment()
            1->BoardsViewFragment()
            2->StatsViewFragment()
            else->OverallScoreViewFragment()
        }
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

    }

}