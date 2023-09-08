package com.example.musicplayer.di

import android.content.Context
import com.example.funnymemesapp.network.NetworkResponseAdapterFactory
import com.example.musicplayer.modules.core.utils.Endpoints
import com.example.musicplayer.modules.songs.data.convertor.ResponseConvertor
import com.example.musicplayer.modules.songs.data.repository.ForYouRepository
import com.example.musicplayer.modules.songs.data.repository.ForYouRepositoryImpl
import com.example.musicplayer.network.services.NetworkApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
//            .connectTimeout(60, TimeUnit.SECONDS)
//            .writeTimeout(60, TimeUnit.SECONDS)
//            .readTimeout(60 * 5, TimeUnit.SECONDS)
//            .followRedirects(false)
//            .followSslRedirects(false)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .baseUrl(Endpoints.BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkApiService(retrofit: Retrofit): NetworkApiService {
        return retrofit.create(NetworkApiService::class.java)
    }


}