package com.example.musicplayer.modules.songs.ui.fragments

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentForYouBinding
import com.example.musicplayer.modules.core.utils.Resource
import com.example.musicplayer.modules.core.utils.setRecyclerAnimation
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.modules.songs.service.SongPlayerService
import com.example.musicplayer.modules.songs.ui.activity.MainActivity
import com.example.musicplayer.modules.songs.ui.adapter.SongListAdapter
import com.example.musicplayer.modules.songs.viewModels.ForYouViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ForYouFragment : Fragment(), ServiceConnection {

    private var _binding: FragmentForYouBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<ForYouViewModel>()


    private val adapter = SongListAdapter {
        it?.let {
            viewModel.setCurrentSong(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForYouBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeData()
        startSongService()
    }

    private fun startSongService() {
        val i = Intent(requireContext(), SongPlayerService::class.java)
        requireContext().bindService(i, this, BIND_AUTO_CREATE)
    }

    private fun observeData() {
        viewModel.songList.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    adapter.submitList(it.data)
                    binding.rvSongs.setRecyclerAnimation((requireActivity() as MainActivity).isShowRecyclerViewAnimation)
                }

                Resource.Status.ERROR -> {
                    //show toast
                }

                else -> {

                }
            }
        }
        viewModel.currentSong.observe(viewLifecycleOwner) { currentSong ->
            viewModel.songList.value?.data?.let {
                showMinimizedPlayer(it[currentSong])
                songService?.startSong(it[currentSong])
            }
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

                    val bitmap = lifecycleScope.async(Dispatchers.IO) {
                        Glide.with(ivSongImg.context)
                            .asBitmap()
                            .centerInside()
                            .transform(CenterInside())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .load("https://cms.samespace.com/assets/${songDetails.cover}")
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
                }

            }
        }
    }

    private fun initRecyclerView() {
        binding.rvSongs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@ForYouFragment.adapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ForYouFragment()

        var songService: SongPlayerService? = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as SongPlayerService.SongPlayerBinder
        songService = binder.getSongPlayerService()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        songService = null
    }
}