package com.example.musicplayer.modules.songs.ui.fragments

import android.annotation.SuppressLint
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentSongPlayerBinding
import com.example.musicplayer.modules.core.utils.Resource
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.modules.songs.data.models.ui.LocalSong
import com.example.musicplayer.modules.songs.helper.SongType
import com.example.musicplayer.modules.songs.ui.adapter.ViewPagerAdapter
import com.example.musicplayer.modules.songs.ui.fragments.SongFragment.Companion.songService
import com.example.musicplayer.modules.songs.viewModels.SongsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class SongPlayerFragment : Fragment() {

    private var _binding: FragmentSongPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<SongsViewModel>()
    private var viewPagerAdapter = ViewPagerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        initView()
        initCarousel()
    }

    private fun initCarousel() {
        binding.viewPagerCarousel.apply {
            adapter = viewPagerAdapter

            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3

            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(40))
            setPageTransformer(MarginPageTransformer(40))


            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.setCurrentSong(Pair(null, position))
                }

            })
        }
    }

    private fun initView() {
        binding.apply {
            ivNext.setOnClickListener {
                viewModel.changesSongNumber(Pair(null, 1))
            }
            ivPrevious.setOnClickListener {
                viewModel.changesSongNumber(Pair(null, -1))
            }
            ivPauseStart.setOnClickListener {
                if (songService?.isSongPlaying() == true) {
                    songService?.pauseSong()
                    ivPauseStart.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_pause
                        )
                    )
                } else {
                    songService?.resumeSong()
                    ivPauseStart.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_resume
                        )
                    )
                }
            }
            seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser){
                        val duration = songService?.getDurations()
                        songService?.seekTo((duration ?: 0) * progress / 100)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeData() {
        /*
        viewModel.songList.observe(viewLifecycleOwner) { songList ->
            when (songList.status) {
                Resource.Status.SUCCESS -> {
                    songList.data?.let {
                        viewPagerAdapter.submitList(it)
                    }
                }

                else -> {}
            }
        }
         */

        viewModel.currentSong.observe(viewLifecycleOwner) { currentSong ->
            binding.apply {
                val l = if (currentSong.first == SongType.REMOTE_SONG) {
                    val i = viewModel.remoteSongList.value?.data
                    viewPagerAdapter.submitList(i)
                    i
                } else {
                    val i = viewModel.localSongList.value
                    viewPagerAdapter.submitList(i?.map { it.getSongDetailsModel() })
                    i
                }
                l?.let {
                    var song = it[currentSong.second]
                    song = when (song) {
                        is SongDetails -> {
                            song
                        }
                        is LocalSong ->  {
                            song.getSongDetailsModel()
                        }
                        else -> {
                            return@observe
                        }
                    } as SongDetails
                    tvSongName.text = song.name
                    tvArtist.text = song.artist
                    viewPagerCarousel.setCurrentItem(currentSong.second, false)

                    setBackground(song)
                    songService?.startSong(song)

                    ivPauseStart.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_resume
                        )
                    )

                    lifecycleScope.launch {
                        var duration: Int? = null
                        var currentPosition: Int? = null
                        do {
                            duration = songService?.getDurations()
                            currentPosition = songService?.getCurrentPosition()

                            // todo format
                            val minutes = TimeUnit.MILLISECONDS.toMinutes((duration ?: 0).toLong())
                            val seconds = TimeUnit.MILLISECONDS.toSeconds((duration ?: 0).toLong())
                            val minutes1 = TimeUnit.MILLISECONDS.toMinutes((currentPosition ?: 0).toLong())
                            val seconds1 = TimeUnit.MILLISECONDS.toSeconds((currentPosition ?: 0).toLong())

                            val f: NumberFormat = DecimalFormat("00")
                            binding.tvTimeEnd.text = "${f.format(minutes)}:${f.format(seconds%60)}"
                            binding.tvTimeCurrent.text = "${f.format(minutes1)}:${f.format(seconds1%60)}"
                            if (duration != 0){
                                val p = 100 - (abs(((currentPosition?.toDouble() ?: 0.0) - (duration?.toDouble() ?: 0.0)) / (duration?.toDouble() ?: 0.0)) * 100)
                                binding.seekbar.setProgress(p.toInt(), true)
                            }
                            delay(500)
                        } while ((duration ?: 0) >= (currentPosition ?: 0))
                    }
                }
            }

        }
    }

    private fun setBackground(songDetails: SongDetails) {
        lifecycleScope.launch {
            try {
                val b = withContext(Dispatchers.IO) {//todo @Abhi add extension function for glide bitmap
                    Glide.with(requireContext())
                        .asBitmap()
                        .transform(CenterCrop())
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
                Palette.from(b).generate { palette ->
                    val swatch = palette?.darkMutedSwatch ?: palette?.darkVibrantSwatch
                    swatch?.let {
                        val h: Int = binding.root.height ?: 0
                        val mDrawable = ShapeDrawable(RectShape())
                        mDrawable.paint.shader = LinearGradient(
                            0f,
                            0f,
                            0f,
                            h.toFloat(),
                            it.rgb,
//                        it.bodyTextColor,
                            requireContext().resources.getColor(R.color.black, null),
                            Shader.TileMode.REPEAT
                        )
                        binding.root.background = mDrawable
                    }
                    palette?.darkVibrantSwatch?.let {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SongPlayerFragment()
    }
}