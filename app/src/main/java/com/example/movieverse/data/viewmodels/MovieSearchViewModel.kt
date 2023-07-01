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
    private val logger = Logger(javaClass.simpleName)

    //MutableLiveData to hold the search results
    val moviesSearchList: MutableLiveData<SearchResult> = MutableLiveData()

    /**
     * Searches for movies based on the provided search text.
     * @param searchTerm The search text
     */
    fun searchMovies(searchTerm: String) {
        compositeDisposable.add(
            repository.getAllMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val searchResultList = it.filter { movie ->
                            //filtering based on search term
                            movie.name.contains(searchTerm, ignoreCase = true)
                        }.map { movie ->
                            //Converting Movie objects into a SearchResultMovie type as we need the search term to change the name colour in the search adapter.
                            //This is for AsyncListDiffer to consider viewholder updation in the cases of same list with different search terms.
                            SearchResultMovie(movie.name, movie.posterImage, searchTerm)
                        }
                        logger.info("Search results with size ${searchResultList.size}")
                        moviesSearchList.value = SearchResult(searchResultList, searchTerm)
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
 *
 * @param searchResultList The list of search results
 * @param searchTerm The search term used for the search
 */
data class SearchResult(val searchResultList: List<SearchResultMovie>, val searchTerm: String)

/**
 * Data class to hold the [Movie] items attached with the search term.
 * This is required for updating the movie name colour in the search adapter based on the search term.
 * As we are using [AsyncListDiffer] we need the search term for it to update same lists with different search terms.
 *
 * @param name The movie name
 * @param posterImage Movie poster image
 * @param searchTerm The search term used
 */
data class SearchResultMovie(val name: String, val posterImage: String, val searchTerm: String)