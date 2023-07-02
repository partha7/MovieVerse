package com.example.movieverse

import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
import com.example.movieverse.data.models.Movie
import com.example.movieverse.data.repositories.MovieRepository
import com.example.movieverse.data.viewmodels.MovieSearchViewModel
import com.example.movieverse.data.viewmodels.SearchResult
import com.example.movieverse.data.viewmodels.SearchResultMovie
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class MovieSearchViewModelTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var viewModel: MovieSearchViewModel

    @Before
    fun setup() {
        //mocking and creating all required classes
        movieRepository = mockk()
        val compositeDisposable = mockk<CompositeDisposable>(relaxed = true)
        viewModel = MovieSearchViewModel(movieRepository, compositeDisposable)

        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        mockkStatic(Looper::class)
        val mainLooper = mockk<Looper>()
        every { mainLooper.thread } returns Thread.currentThread()
        every { Looper.getMainLooper() } returns mainLooper

        //Adding these to ensure that the RxJava operations run on the immediate scheduler, which executes tasks synchronously
        //and avoids issues with asynchronous behavior in tests.
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun searchMoviesShouldUpdateMoviesSearchListWithSearchResults() {
        val searchTerm = "Mo"

        val movies = listOf(
            Movie("Movie 1", "poster1"), Movie("Movie 2", "poster2")
        )

        val searchResultMovies = movies.map { SearchResultMovie(it.name, it.posterImage, "Mo") }
        val searchResult = SearchResult(searchResultMovies, searchTerm)

        every { movieRepository.getAllMovies() } returns Observable.just(movies)

        val testObserver = TestObserver<SearchResult>()
        val observer = Observer<SearchResult> {
            testObserver.onNext(it)
        }

        viewModel.moviesSearchList.observeForever(observer)
        viewModel.searchMovies(searchTerm)

        //wait for the value to be emitted
        testObserver.awaitCount(1)

        //assert
        testObserver.assertValue { it == searchResult }

        //clean up
        viewModel.moviesSearchList.removeObserver(observer)
    }


    @Test
    fun searchMoviesShouldUpdateMoviesSearchListWithEmptyResultsWhenSearchResultIsEmpty() {

        val searchTerm = "Bird"
        val emptySearchResultList = emptyList<Movie>()
        val emptySearchResult = SearchResult(emptyList(), searchTerm)
        every { movieRepository.getAllMovies() } returns Observable.just(emptySearchResultList)

        val testObserver = TestObserver<SearchResult>()
        val observer = Observer<SearchResult> {
            testObserver.onNext(it)
        }

        viewModel.moviesSearchList.observeForever(observer)
        viewModel.searchMovies(searchTerm)

        //wait for the value to be emitted
        testObserver.awaitCount(1)

        //assert
        testObserver.assertValue { it == emptySearchResult }

        //clean up
        viewModel.moviesSearchList.removeObserver(observer)
    }
}
