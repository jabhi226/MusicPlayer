package com.example.musicplayer.modules.songs.viewModels

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

    val songList = MutableLiveData<Resource<List<SongDetails>>>()
    val currentSong: MutableLiveData<Int> = MutableLiveData()

    init {
        getSongList()
    }

    private fun getSongList() {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
                songList.postValue(repository.getSongList())
            }
        }
    }

    fun setCurrentSong(it: SongDetails) {
        currentSong.value = songList.value?.data?.indexOf(it)
    }

    fun changesSongNumber(i: Int) {
        val c = currentSong.value?.plus(i) ?: 0
        if ((songList.value?.data?.count() ?: 0) > c) {
            currentSong.value = c
        }
    }

    fun updateSongNumber(position: Int) {
        if (position <= (songList.value?.data?.count() ?: 0)){
            currentSong.value = position
        }
    }
}






