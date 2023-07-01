import com.example.movieverse.data.models.Movie
import com.example.movieverse.data.repositories.MovieRepository
import com.example.movieverse.data.viewmodels.MovieSearchViewModel
import com.example.movieverse.data.viewmodels.SearchResult
import com.example.movieverse.data.viewmodels.SearchResultMovie
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MovieSearchViewModelTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var viewModel: MovieSearchViewModel

    @Before
    fun setup() {
        movieRepository = mockk()
        val compositeDisposable = mockk<CompositeDisposable>(relaxed = true)
        viewModel = MovieSearchViewModel(movieRepository, compositeDisposable)
    }

    @Test
    fun searchMoviesShouldUpdateMoviesSearchListWithSearchResults() {
        val searchTerm = "Mo"

        val movies = listOf(
            Movie("Movie 1", "poster1"), Movie("Movie 2", "poster2"))

        val searchResultMovies = movies.map { SearchResultMovie(it.name, it.posterImage, "Mo") }
        val searchResult = SearchResult(searchResultMovies, searchTerm)
        every { movieRepository.getAllMovies() } returns Observable.just(movies)

        viewModel.searchMovies(searchTerm)

        //Assert
        assertEquals(searchResult, viewModel.moviesSearchList.value)
    }


    @Test
    fun searchMoviesShouldUpdateMoviesSearchListWithEmptyResultsWhenSearchResultIsEmpty() {

        val searchTerm = "Bird"
        val emptySearchResultList = emptyList<Movie>()
        val emptySearchResult = SearchResult(emptyList(), searchTerm)
        every { movieRepository.getAllMovies() } returns Observable.just(emptySearchResultList)

        viewModel.searchMovies(searchTerm)

        //Assert
        assertEquals(emptySearchResult, viewModel.moviesSearchList.value)
    }
}
