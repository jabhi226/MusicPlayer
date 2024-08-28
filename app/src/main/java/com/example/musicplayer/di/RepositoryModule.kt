package com.example.musicplayer.di

import com.example.musicplayer.modules.songs.data.convertor.RemoteSongsResponseConverter
import com.example.musicplayer.modules.songs.data.convertor.RemoteSongsResponseConverterImpl
import com.example.musicplayer.modules.songs.data.repository.ForYouRepository
import com.example.musicplayer.modules.songs.data.repository.ForYouRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun bindRemoteSongResponseConverter(remoteSongsResponseConverter: RemoteSongsResponseConverterImpl): RemoteSongsResponseConverter

    @Binds
    @ViewModelScoped
    abstract fun bindForYouRepository(forYouRepository: ForYouRepositoryImpl): ForYouRepository

}