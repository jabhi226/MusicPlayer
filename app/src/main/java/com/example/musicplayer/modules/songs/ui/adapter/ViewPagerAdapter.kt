package com.example.musicplayer.modules.songs.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.musicplayer.databinding.ItemCorousalBinding
import com.example.musicplayer.modules.songs.data.models.network.SongDetails

class ViewPagerAdapter :
    ListAdapter<SongDetails, ViewPagerAdapter.ViewPagerViewHolder>(object :
        DiffUtil.ItemCallback<SongDetails>() {
        override fun areItemsTheSame(oldItem: SongDetails, newItem: SongDetails): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SongDetails, newItem: SongDetails): Boolean {
            return oldItem.id == newItem.id
        }
    }) {

    inner class ViewPagerViewHolder(private val binding: ItemCorousalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: SongDetails?) {
            item?.cover?.let { s ->
                Glide.with(binding.ivCover.context)
                    .load("https://cms.samespace.com/assets/$s")
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(CenterCrop())
                    .into(binding.ivCover)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        return ViewPagerViewHolder(
            ItemCorousalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

}