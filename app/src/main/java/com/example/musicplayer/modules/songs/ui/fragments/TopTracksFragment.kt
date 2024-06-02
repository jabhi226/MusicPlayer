package com.example.musicplayer.modules.songs.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.modules.core.utils.Utils
import com.example.musicplayer.modules.songs.data.models.ui.LocalSong
import com.example.musicplayer.modules.songs.ui.adapter.TopTrackAdapter


class TopTracksFragment : Fragment() {

    val adapter = TopTrackAdapter {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_tracks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            101
        )
        view.findViewById<RecyclerView>(R.id.rv_songs).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TopTracksFragment.adapter
        }
        adapter.submitList(getPlayList())
    }

    private fun getPlayList(): List<LocalSong>? {
        return try {
            val songs = ArrayList<LocalSong>()
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.AudioColumns.RELATIVE_PATH,
                MediaStore.Audio.AudioColumns.AUTHOR,
                MediaStore.Audio.AudioColumns.DURATION,
            )
            val selection = MediaStore.Audio.Media.IS_MUSIC

            val c = requireContext().contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )

            if (c != null) {
                while (c.moveToNext()) {
                    for (i in 0 until c.columnCount) {
                        print("${c.getString(i)}, ")
                    }
                    val id = c.getString(0)
                    val data = c.getString(1)
                    val displayName = c.getString(2)
                    val artist = c.getString(3)
                    val album = c.getString(4)
                    val duration = c.getString(5)
                    val albumId = c.getLong(6)


                    val imageUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        val adsflsf = requireContext().contentResolver.loadThumbnail(imageUri, Size(300, 300), null)
//                        adsflsf
                    }

                    val song = LocalSong(
                        id,
                        data,
                        displayName,
                        artist,
                        album,
                        duration,
                        imageUri.toString()
                    )
                    println("=>>> $song")
                    songs.add(song)
                }
                c.close()
            }
            Utils.showToast(requireContext(), songs.size.toString())
            songs
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TopTracksFragment().apply {
            }
    }
}