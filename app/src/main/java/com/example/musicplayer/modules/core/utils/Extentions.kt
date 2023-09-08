package com.example.musicplayer.modules.core.utils

import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R


fun RecyclerView.setRecyclerAnimation(showRecyclerViewAnimation: Boolean) {
    if (!showRecyclerViewAnimation) return
    val anime = AnimationUtils.loadLayoutAnimation(
        this.context, R.anim.layout_animation_fade_in
    )
    anime.delay = 0.2F
    anime.interpolator = LinearInterpolator()
    this.layoutAnimation = anime
}