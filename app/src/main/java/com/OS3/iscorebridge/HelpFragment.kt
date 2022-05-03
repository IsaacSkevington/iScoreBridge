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


class HelpPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {


    override fun createFragment(position: Int): Fragment {
        return HelpDisplayFragment.newInstance(HELPINFOLIST[position].second)
    }

    override fun getItemCount(): Int {
        return HELPINFOLIST.size
    }
}

class HelpDisplayFragment : Fragment(){

    lateinit var helpItem : HelpItem;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.help_subpage, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helpItem.display(view.findViewById(R.id.helpSubpageLinearLayout), resources)

    }
    companion object {
        fun newInstance(helpItem: HelpItem): HelpDisplayFragment {
            var hdf = HelpDisplayFragment()
            hdf.helpItem = helpItem
            return hdf;
        }
    }
}

class HelpFragment : Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var onClose = {}

    companion object{
        fun newInstance(onClose : ()->Unit): HelpFragment {
            var frag = HelpFragment()
            frag.onClose = onClose
            return frag
        }
    }

    private lateinit var adapter: HelpPagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_help, container, false)
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<FloatingActionButton>(R.id.closeHelpButton).setOnClickListener {
            onClose()
        }
        adapter = HelpPagerAdapter(this)
        val tabLayout = view.findViewById<TabLayout>(R.id.helpTabLayout)
        viewPager = view.findViewById(R.id.helpViewPager)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = HELPINFOLIST[position].first
        }.attach()
        viewPager.currentItem = 0

    }




}
