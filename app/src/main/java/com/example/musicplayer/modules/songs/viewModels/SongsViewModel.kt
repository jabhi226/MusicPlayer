package com.example.musicplayer.modules.songs.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.modules.core.utils.Resource
import com.example.musicplayer.modules.songs.data.models.network.SongDetails
import com.example.musicplayer.modules.songs.data.models.ui.LocalSong
import com.example.musicplayer.modules.songs.data.repository.ForYouRepository
import com.example.musicplayer.modules.songs.helper.SongType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val repository: ForYouRepository,
    private val exceptionHandler: CoroutineExceptionHandler
): ViewModel() {

    private val _remoteSongList = MutableLiveData<Resource<List<SongDetails>>>()
    val remoteSongList: LiveData<Resource<List<SongDetails>>> get() = _remoteSongList

    private val _localSongList = MutableLiveData<List<LocalSong>>()
    val localSongList: LiveData<List<LocalSong>> get() = _localSongList

    private val _currentSong: MutableLiveData<Pair<SongType, Int>> = MutableLiveData()
    val currentSong: LiveData<Pair<SongType, Int>> get() = _currentSong

    init {
        getSongList()
    }

    private fun getSongList() {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
                _remoteSongList.postValue(repository.getSongList())
            }
        }
    }

    fun setCurrentSong(it: Pair<SongType, Int?>) {
        if (it.second != null)
            _currentSong.value = Pair(it.first, it.second!!)
    }

    fun changesSongNumber(i: Pair<SongType, Int>) {
        val songNumber = currentSong.value?.second?.plus(i.second) ?: 0
        if (i.first == SongType.REMOTE_SONG) {
            if ((remoteSongList.value?.data?.count() ?: 0) > songNumber) {
                _currentSong.value = Pair(SongType.REMOTE_SONG, songNumber)
            }
        } else {
            if ((localSongList.value?.count() ?: 0) > songNumber) {
                _currentSong.value = Pair(SongType.LOCAL_SONG, songNumber)
            }
        }
    }

    fun setLocalSongList(playList: List<LocalSong>) {
        _localSongList.value = playList
    }

}