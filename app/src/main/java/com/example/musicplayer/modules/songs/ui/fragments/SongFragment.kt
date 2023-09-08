package com.example.musicplayer.modules.songs.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentSongBinding
import com.google.android.material.tabs.TabLayout
import java.util.ArrayList


class SongFragment : Fragment() {

    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabLayout()
    }

    private fun initTabLayout() {
        val myTabPagerAdapter =
            TabPagerAdapter(
                childFragmentManager,
                lifecycle,
                arrayListOf(ForYouFragment.newInstance(), TopTracksFragment.newInstance())
            )
        binding.viewPager.apply {
            adapter = myTabPagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
                    super.onPageSelected(position)
                }
            })

        }
        binding.tabLayout.apply {
            addTab(newTab().setText(getString(R.string.for_you)))
            addTab(newTab().setText(getString(R.string.top_tracks)))
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let { binding.viewPager.setCurrentItem(it.position, true) }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    tab?.let { binding.viewPager.setCurrentItem(it.position, false) }
                }

            })
        }
    }

    inner class TabPagerAdapter(
        fa: FragmentManager,
        lc: Lifecycle,
        private var fragmentsList: ArrayList<Fragment>
    ) : FragmentStateAdapter(fa, lc) {
        override fun getItemCount(): Int = fragmentsList.size

        override fun createFragment(position: Int): Fragment {
            return fragmentsList[position]
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SongFragment()
    }
}