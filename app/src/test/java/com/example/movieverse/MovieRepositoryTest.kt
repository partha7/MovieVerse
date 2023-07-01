package com.example.movieverse

import android.content.Context
import com.example.movieverse.data.models.Data
import com.example.movieverse.data.models.Movie
import com.example.movieverse.data.models.Movies
import com.example.movieverse.data.models.Page
import com.example.movieverse.data.repositories.MovieRepository
import com.example.movieverse.utils.Constants
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MovieRepositoryTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var context: Context
    private lateinit var gson: Gson

    @Before
    fun setup() {
        context = mockk()
        gson = Gson()
        movieRepository = MovieRepository(context, gson)
    }

    @Test
    fun testReadJsonFileReturnsDataObjectWhenFileExists() {

        val fileName = "test_file.json"
        val jsonData = "{\"page\": {\"title\": \"Romantic Comedy\",\"total-content-items\" : \"54\",\"page-num\" : \"1\",\"page-size\" : \"20\",\"content-items\": {\"content\": [{\"name\": \"The Birds\",\"poster-image\": \"poster1.jpg\"},{\"name\": \"Rear Window\",\"poster-image\": \"poster2.jpg\"}]}}}"

        val inputStream = jsonData.byteInputStream()

        // Mock the context.assets.open method to return the test JSON data as an InputStream
        every { context.assets.open(fileName) } returns inputStream

        val result = movieRepository.readJsonFile(fileName)

        assertEquals("Romantic Comedy", result?.page?.title)
        assertEquals(2, result?.page?.movies?.content?.size)
    }

    @Test
    fun testReadJsonFileReturnsNullWhenFileDoesNotExist() {
        val fileName = "non_existent_file.json"

        // Mock the context.assets.open method to throw an exception
        every { context.assets.open(fileName) } throws Exception()

        val result = movieRepository.readJsonFile(fileName)

        // Assert
        assertEquals(null, result)
    }

    @Test
    fun getAllMovieShouldReturnCombinedListOfMoviesFromAllJSONFiles() {
        val movie1 = Movie("The Birds", "poster1.jpg")
        val movie2 = Movie("Rear Window", "poster2.jpg")
        val movie3 = Movie("Rear Window", "poster1.jpg")
        val data1 = Data(Page("", "", "", "", Movies(listOf(movie1, movie2))))
        val data2 = Data(Page("", "", "", "", Movies(listOf(movie3))))

        val json1 = "{\"page\": {\"title\": \"Romantic Comedy\",\"total-content-items\" : \"54\",\"page-num\" : \"1\",\"page-size\" : \"20\",\"content-items\": {\"content\": [{\"name\": \"The Birds\",\"poster-image\": \"poster1.jpg\"},{\"name\": \"Rear Window\",\"poster-image\": \"poster2.jpg\"}]}}}"
        val json2 = "{\"page\": {\"title\": \"Romantic Comedy\",\"total-content-items\" : \"54\",\"page-num\" : \"1\",\"page-size\" : \"20\",\"content-items\": {\"content\": [{\"name\": \"Rear Window\",\"poster-image\": \"poster1.jpg\"}]}}}"

        every { context.assets.open("${Constants.FILENAME_PREFIX}1.json").bufferedReader().use { it.readText() } } returns json1
        every { context.assets.open("${Constants.FILENAME_PREFIX}2.json").bufferedReader().use { it.readText() } } returns json2
        every { gson.fromJson(json1, Data::class.java) } returns data1
        every { gson.fromJson(json2, Data::class.java) } returns data2

        val result = movieRepository.getAllMovies()

        val expectedList = listOf(movie1, movie2, movie3)
        assertEquals(expectedList, result)
    }
}
