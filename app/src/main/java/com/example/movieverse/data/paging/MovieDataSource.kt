package com.example.movieverse.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieverse.data.models.Movie
import com.example.movieverse.data.repositories.MovieRepository
import com.example.movieverse.utils.Constants
import com.example.movieverse.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [MovieDataSource] is responsible for loading paginated data of type [Movie].
 * It uses [PagingSource] from the Paging 3 library to provide the data.
 *
 * @param repository The repository class responsible for fetching the movie data.
 * @param pageTitleLoaded Callback function to notify the loaded page title.
 */
class MovieDataSource(
    private val repository: MovieRepository,
    val pageTitleLoaded: (String) -> Unit
) : PagingSource<Int, Movie>() {

    /**
     * Instance of the [Logger] class used for logging data.
     * */
    private val logger = Logger(javaClass.simpleName)


    /**
     * Function to get the refresh key for pagination.
     * It determines the key for next page based on the current anchor position.
     *
     * @param state The current [PagingState] of the paging data.
     * @return The refresh key for pagination.
     */
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    /**
     * Function to load the data for a specific page.
     * It retrieves the page contents from the repository and notifies the loaded page title.
     *
     * @param params The [LoadParams] containing information about the page to load.
     * @return The [LoadResult] containing the loaded data and pagination keys.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val pageNum = params.key ?: 1

        val prevKey = if (pageNum > 1) pageNum - 1 else null
        val nextKey = if (pageNum < Constants.TOTAL_PAGES) pageNum + 1 else null

        return try {
            //Loading the page contents from the json file
            val pageContents = withContext(Dispatchers.IO) {
                repository.readJsonFile("${Constants.FILENAME_PREFIX}${pageNum}.json")?.page
            }

            var movies = listOf<Movie>()

            if (pageContents != null) {
                //Notifying the loaded page title through the callback function
                pageTitleLoaded(pageContents.title)
                movies = pageContents.movies.content

                logger.info("Movies loaded for Page $pageNum")
            } else
                logger.warn("Page contents for page num $pageNum is null")

            LoadResult.Page(
                data = movies,
                prevKey = prevKey,
                nextKey = nextKey
            )

        } catch (e: Exception) {
            logger.error("Error while loading page $pageNum", e)
            LoadResult.Error(e)
        }
    }
}