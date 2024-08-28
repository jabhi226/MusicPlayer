package com.example.musicplayer.modules.songs.ui.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentSongBinding
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.modules.songs.helper.SongType
import com.example.musicplayer.modules.songs.service.SongEventListener
import com.example.musicplayer.modules.songs.service.SongPlayerService
import com.example.musicplayer.modules.songs.ui.activity.MainActivity
import com.example.musicplayer.modules.songs.viewModels.SongsViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.ArrayList


class SongFragment : Fragment(), ServiceConnection, SongEventListener {

    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SongsViewModel>()

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
        observeData()
        startSongService()
    }

    private fun observeData() {
        viewModel.currentSong.observe(viewLifecycleOwner) { currentSong ->
            val ind = currentSong.second
            if (currentSong.first == SongType.REMOTE_SONG) {
                viewModel.remoteSongList.value?.data?.let {
                    showMinimizedPlayer(it[ind])
                    songService?.startSong(it[ind])
                }
            } else {
                viewModel.localSongList.value?.let {
                    showMinimizedPlayer(it[ind].getSongDetailsModel())
                    songService?.startSong(it[ind].getSongDetailsModel())
                }
            }
        }

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

    private fun showMinimizedPlayer(songDetails: SongDetails) {
        binding.apply {
            itemMinimizedPlayer.root.setOnClickListener {
                (requireActivity() as MainActivity).addFragment(SongPlayerFragment.newInstance())
            }
            itemMinimizedPlayer.ivPauseStart.setOnClickListener {
                if (songService?.isSongPlaying() == true) {
                    songService?.pauseSong()
                    itemMinimizedPlayer.ivPauseStart.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_pause
                        )
                    )
                } else {
                    songService?.resumeSong()
                    itemMinimizedPlayer.ivPauseStart.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_resume
                        )
                    )
                }
            }
            itemMinimizedPlayer.root.visibility = View.VISIBLE
            itemMinimizedPlayer.apply item@{
                tvSongName.text = songDetails.name
                lifecycleScope.launch {
                    try {
                        val bitmap = lifecycleScope.async(Dispatchers.IO) {
                            Glide.with(ivSongImg.context)
                                .asBitmap()
                                .centerInside()
                                .transform(CenterInside())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .load(
                                    if (songDetails.uri != null) {
                                        songDetails.uri
                                    } else {
                                        "https://cms.samespace.com/assets/${songDetails.cover}"
                                    }
                                )
                                .submit()
                                .get()
                        }
                        bitmap.await()?.let { bitmap1 ->
                            ivSongImg.setImageBitmap(bitmap1)
                            Palette.from(bitmap1).generate { palette ->
                                val swatch = palette?.darkMutedSwatch ?: palette?.darkVibrantSwatch
                                swatch?.let {
                                    tvSongName.setTextColor(it.titleTextColor)
                                    this@item.root.setBackgroundColor(it.rgb)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        }
    }

    private fun startSongService() {
        val i = Intent(requireContext(), SongPlayerService::class.java)
        requireContext().bindService(i, this, Context.BIND_AUTO_CREATE)
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

        var songService: SongPlayerService? = null //todo update to song fragmnet

        fun startStopPlayer() {
            if (songService?.isSongPlaying() == true) {
                songService?.pauseSong()
            } else {
                songService?.resumeSong()
            }
        }

        fun playPrevious(songType: SongType) {
            songService?.previousSong(songType)
        }

        fun playNext(songType: SongType) {
            songService?.nextSong(songType)
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as SongPlayerService.SongPlayerBinder
        songService = binder.getSongPlayerService()
        songService?.setEventListeners(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        songService = null
    }

    override fun playPreviousSong(songType: SongType) {
        viewModel.changesSongNumber(Pair(songType, -1))
    }

    override fun playNextSong(songType: SongType) {
        viewModel.changesSongNumber(Pair(songType, 1))
    }
}