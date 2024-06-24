package com.example.musicplayer.modules.songs.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.modules.core.utils.Resource
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.modules.songs.data.repository.ForYouRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForYouViewModel @Inject constructor(
    private val repository: ForYouRepository,
    private val exceptionHandler: CoroutineExceptionHandler
) : ViewModel() {

    private val _songList = MutableLiveData<Resource<List<SongDetails>>>()
    val songList: LiveData<Resource<List<SongDetails>>> get() = _songList

    private val _currentSong: MutableLiveData<Int> = MutableLiveData()
    val currentSong: LiveData<Int> get() = _currentSong

    init {
        getSongList()
    }

    private fun getSongList() {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
                _songList.postValue(repository.getSongList())
            }
        }
    }

    fun setCurrentSong(it: SongDetails) {
        _currentSong.value = songList.value?.data?.indexOf(it)
    }

    fun changesSongNumber(i: Int) {
        val c = currentSong.value?.plus(i) ?: 0
        if ((songList.value?.data?.count() ?: 0) > c) {
            _currentSong.value = c
        }
    }

    fun updateSongNumber(position: Int) {
        if (position <= (songList.value?.data?.count() ?: 0)){
            _currentSong.value = position
        }
    }
}






