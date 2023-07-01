package com.example.movieverse.di

import com.example.movieverse.data.repositories.MovieRepository
import com.example.movieverse.data.viewmodels.MovieSearchViewModel
import com.example.movieverse.data.viewmodels.MovieViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.disposables.CompositeDisposable

@Module
@InstallIn(ActivityRetainedComponent::class)
class ViewModelModule {

    @Provides
    @ActivityRetainedScoped
    fun provideMovieViewModel(repository: MovieRepository): MovieViewModel {
        return MovieViewModel(repository)
    }

    @Provides
    fun provideMovieSearchViewModel(
        repository: MovieRepository,
        compositeDisposable: CompositeDisposable
    ): MovieSearchViewModel {
        return MovieSearchViewModel(repository, compositeDisposable)
    }
}
