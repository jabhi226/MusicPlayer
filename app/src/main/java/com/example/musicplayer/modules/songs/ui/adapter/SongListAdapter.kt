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
import com.example.musicplayer.modules.songs.data.models.network.SongDetails

class SongListAdapter(val onSongClicked: (SongDetails?) -> Unit) :
    ListAdapter<SongDetails, SongListAdapter.SongDetailsItemViewHolder>(object :
        DiffUtil.ItemCallback<SongDetails>() {
        override fun areItemsTheSame(oldItem: SongDetails, newItem: SongDetails): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SongDetails, newItem: SongDetails): Boolean {
            return oldItem.id == newItem.id
        }

    }) {

    inner class SongDetailsItemViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: SongDetails?) {
            binding.apply {
                tvSongName.text = item?.name
                tvArtist.text = item?.artist
                item?.cover?.let {
                    Glide.with(ivSongImg.context)
                        .load("https://cms.samespace.com/assets/$it")
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

    override fun onBindViewHolder(holder: SongDetailsItemViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }
}