package com.example.musicplayer.modules.songs.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ItemSongBinding
import com.example.musicplayer.modules.songs.data.models.ui.LocalSong

class TopTrackAdapter(val onSongClicked: (LocalSong?) -> Unit) :
    ListAdapter<LocalSong, TopTrackAdapter.SongDetailsItemViewHolder>(object :
        DiffUtil.ItemCallback<LocalSong>() {
        override fun areItemsTheSame(oldItem: LocalSong, newItem: LocalSong): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LocalSong, newItem: LocalSong): Boolean {
            return oldItem.id == newItem.id
        }

    }) {

    inner class SongDetailsItemViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: LocalSong?) {
            binding.apply {
                tvSongName.text = item?.displayName
                tvArtist.text = item?.artist
                item?.let {
                    Glide.with(ivSongImg.context)
                        .load(item.imageUri)
                        .centerInside()
                        .placeholder(
                            ContextCompat.getDrawable(
                                this.root.context,
                                R.drawable.ic_launcher_foreground
                            )
                        )
                        .transform(CenterCrop())
                        .into(ivSongImg)
                }
                root.setOnClickListener {
                    onSongClicked(item)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongDetailsItemViewHolder {
        return SongDetailsItemViewHolder(
            ItemSongBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: TopTrackAdapter.SongDetailsItemViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }
}