package com.example.musicplayer.di

import android.nfc.Tag
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineExceptionHandler

@InstallIn(ViewModelComponent::class)
@Module
class CoroutineModule {

    @ViewModelScoped
    @Provides
    fun provideCoroutineModule(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }
    }
}