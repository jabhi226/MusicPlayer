package com.example.musicplayer.modules.songs.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.modules.songs.data.models.ui.LocalSong
import com.example.musicplayer.modules.songs.data.repository.ForYouRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject

@HiltViewModel
class TopTrackViewModel @Inject constructor(
    private val repository: ForYouRepository,
    private val exceptionHandler: CoroutineExceptionHandler
) : ViewModel() {

    private val _songList = MutableLiveData<List<LocalSong>>()
    val songList: LiveData<List<LocalSong>> get() = _songList

    private val _currentSong: MutableLiveData<Int> = MutableLiveData()
    val currentSong: LiveData<Int> get() = _currentSong

    fun setCurrentSong(it: LocalSong) {
        _currentSong.value = songList.value?.indexOf(it)
    }

    fun changesSongNumber(i: Int) {
        val c = currentSong.value?.plus(i) ?: 0
        if ((songList.value?.count() ?: 0) > c) {
            _currentSong.value = c
        }
    }

    fun updateSongNumber(position: Int) {
        if (position <= (songList.value?.count() ?: 0)) {
            _currentSong.value = position
        }
    }

    fun setCurrentSeekBarPosition(toInt: Int) {

    }

    fun setSongList(playList: List<LocalSong>) {
        _songList.value = playList
    }
}






