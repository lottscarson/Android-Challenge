package com.podium.technicalchallenge.dashboard

import com.podium.technicalchallenge.MainDispatcherRule
import com.podium.technicalchallenge.common.Movie
import com.podium.technicalchallenge.common.MovieRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testMovies = listOf(Movie(1, "Test Movie", "Overview", 8.5, listOf("Action"), null))
    private val testGenres = listOf("Action", "Drama")

    private lateinit var repository: MovieRepository

    @Before
    fun setup() {
        repository = mock()
    }

    @Test
    fun `successful load transitions to Success state`() = runTest {
        whenever(repository.getTop5Movies()).thenReturn(testMovies)
        whenever(repository.getGenres()).thenReturn(testGenres)
        whenever(repository.getMoviesByGenre(anyOrNull(), anyOrNull())).thenReturn(testMovies)

        val viewModel = DashboardViewModel(repository)

        val state = viewModel.uiState.value as DashboardUiState.Success
        assertEquals(testMovies, state.top5)
        assertEquals(testGenres, state.genres)
        assertEquals(testMovies, state.browseMovies)
    }

    @Test
    fun `network error transitions to Error state`() = runTest {
        whenever(repository.getTop5Movies()).thenThrow(RuntimeException("Network error"))

        val viewModel = DashboardViewModel(repository)

        val state = viewModel.uiState.value
        assertTrue(state is DashboardUiState.Error)
        assertEquals("Network error", (state as DashboardUiState.Error).message)
    }

    @Test
    fun `onGenreSelected updates browse movies`() = runTest {
        whenever(repository.getTop5Movies()).thenReturn(testMovies)
        whenever(repository.getGenres()).thenReturn(testGenres)
        whenever(repository.getMoviesByGenre(anyOrNull(), anyOrNull())).thenReturn(testMovies)

        val viewModel = DashboardViewModel(repository)

        val filteredMovies = listOf(Movie(2, "Action Movie", "Overview", 9.0, listOf("Action"), null))
        whenever(repository.getMoviesByGenre("Action", null)).thenReturn(filteredMovies)

        viewModel.onGenreSelected("Action")

        val state = viewModel.uiState.value as DashboardUiState.Success
        assertEquals(filteredMovies, state.browseMovies)
        assertEquals("Action", viewModel.selectedGenre.value)
    }

    @Test
    fun `onRatingSelected filters movies by minimum rating`() = runTest {
        whenever(repository.getTop5Movies()).thenReturn(testMovies)
        whenever(repository.getGenres()).thenReturn(testGenres)
        whenever(repository.getMoviesByGenre(anyOrNull(), anyOrNull())).thenReturn(testMovies)

        val viewModel = DashboardViewModel(repository)

        val highRatedMovies = listOf(Movie(3, "High Rated", "Overview", 9.5, listOf("Drama"), null))
        whenever(repository.getMoviesByGenre(null, 8.0)).thenReturn(highRatedMovies)

        viewModel.onRatingSelected(8)

        val state = viewModel.uiState.value as DashboardUiState.Success
        assertEquals(highRatedMovies, state.browseMovies)
        assertEquals(8, viewModel.selectedRating.value)
        verify(repository).getMoviesByGenre(null, 8.0)
    }
}
