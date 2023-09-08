package com.example.musicplayer.di

import android.content.Context
import com.example.musicplayer.modules.songs.data.convertor.ResponseConvertor
import com.example.musicplayer.modules.songs.data.repository.ForYouRepository
import com.example.musicplayer.modules.songs.data.repository.ForYouRepositoryImpl
import com.example.musicplayer.network.services.NetworkApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
class RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideForYouRepository(
        networkApiService: NetworkApiService,
        @ApplicationContext context: Context
    ): ForYouRepository {
        return ForYouRepositoryImpl(networkApiService, ResponseConvertor(context))
    }
}