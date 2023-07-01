package com.example.movieverse.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.movieverse.data.paging.MovieDataSource
import com.example.movieverse.data.repositories.MovieRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MovieModule {

    @Provides
    @Singleton
    fun provideContext(context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideMovieRepository(context: Application, gson: Gson): MovieRepository {
        return MovieRepository(context, gson)
    }

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}