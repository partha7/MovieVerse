package com.example.movieverse.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieverse.data.models.Movie
import com.example.movieverse.data.paging.MovieDataSource
import com.example.movieverse.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class for managing movie-related data and operations.
 */
@HiltViewModel
class MovieViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {

    //MutableLiveData to hold the movie list
    val moviesList: MutableLiveData<PagingData<Movie>> = MutableLiveData()

    //MutableLiveData to hold the page title
    var pageTitle: MutableLiveData<String> = MutableLiveData()


    //PagingConfig instance for the Pager
    private val pagingConfig = PagingConfig(
        pageSize = 20,
        prefetchDistance = 3,
        enablePlaceholders = false,
    )


    /**
     * Fetches the movies data using paging and populates the [moviesList] MutableLiveData.
     */
    fun fetchMovies() {
        viewModelScope.launch {
            getPagingMovieData()
                .cachedIn(viewModelScope)
                .collectLatest {
                    moviesList.value = it
                }
        }
    }


    /**
     * Retrieves the paging data for movies using the [Pager] and [MovieDataSource].
     * Sets the [pageTitle] when the page title is loaded.
     *
     * @return The flow of [PagingData] for movies.
     */
    private fun getPagingMovieData(): Flow<PagingData<Movie>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                MovieDataSource(repository) {
                    pageTitle.value = it
                }
            }
        ).flow
    }
}