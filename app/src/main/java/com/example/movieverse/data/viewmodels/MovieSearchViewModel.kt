package com.example.movieverse.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movieverse.data.models.Movie
import com.example.movieverse.data.repositories.MovieRepository
import com.example.movieverse.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

/**
 * ViewModel class responsible for handling movie search functionality.
 * @param repository The MovieRepository instance for data retrieval.
 * @param compositeDisposable The CompositeDisposable for managing RxJava subscriptions.
 */
@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    private val repository: MovieRepository, private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    /**
     * Instance of the [Logger] class used for logging data.
     * */
    val logger = Logger(javaClass.simpleName)

    //MutableLiveData to hold the search results
    val moviesSearchList: MutableLiveData<SearchResult> = MutableLiveData()

    /**
     * Searches for movies based on the provided search text.
     * @param text The search text
     */
    fun searchMovies(text: String) {
        compositeDisposable.add(
            repository.getAllMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val searchResultList = it.filter { movie ->
                            movie.name.contains(text, ignoreCase = true)
                        }
                        logger.info("Search results with size ${searchResultList.size}")
                        moviesSearchList.value = SearchResult(searchResultList, text)
                    },
                    {
                        logger.error("Error while searching movies", it)
                    })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}

/**
 * Data class to hold the search results and search term.
 * @param searchResultList The list of search results
 * @param searchTerm The search term used for the search
 */
data class SearchResult(val searchResultList: List<Movie>, val searchTerm: String)