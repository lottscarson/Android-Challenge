package com.podium.technicalchallenge.detail

import androidx.lifecycle.SavedStateHandle
import com.podium.technicalchallenge.MainDispatcherRule
import com.podium.technicalchallenge.common.Movie
import com.podium.technicalchallenge.common.MovieRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testMovie = Movie(
        id = 1,
        title = "Test Movie",
        overview = "An overview",
        voteAverage = 8.5,
        genres = listOf("Action", "Drama"),
        posterPath = null,
        releaseDate = "2023-01-01",
        voteCount = 1000,
        runtime = 120,
        budget = 1_000_000
    )

    @Test
    fun `successful load transitions to Success state`() = runTest {
        val repository: MovieRepository = mock()
        whenever(repository.getMovieDetail(1)).thenReturn(testMovie)

        val viewModel = MovieDetailViewModel(SavedStateHandle(mapOf("movieId" to 1)), repository)

        val state = viewModel.uiState.value as DetailUiState.Success
        assertEquals(testMovie, state.movie)
    }

    @Test
    fun `network error transitions to Error state`() = runTest {
        val repository: MovieRepository = mock()
        whenever(repository.getMovieDetail(1)).thenThrow(RuntimeException("Not found"))

        val viewModel = MovieDetailViewModel(SavedStateHandle(mapOf("movieId" to 1)), repository)

        assertTrue(viewModel.uiState.value is DetailUiState.Error)
    }

    @Test(expected = IllegalStateException::class)
    fun `missing movieId in SavedStateHandle throws immediately`() {
        MovieDetailViewModel(SavedStateHandle(emptyMap()), mock())
    }
}
