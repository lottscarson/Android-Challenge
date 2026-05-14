package com.podium.technicalchallenge.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podium.technicalchallenge.common.Movie
import com.podium.technicalchallenge.common.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val top5: List<Movie>,
        val genres: List<String>,
        val browseMovies: List<Movie>
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    private val _selectedRating = MutableStateFlow<Int?>(null)
    val selectedRating: StateFlow<Int?> = _selectedRating.asStateFlow()

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var reloadJob: Job? = null

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                val top5 = movieRepository.getTop5Movies()
                val genres = movieRepository.getGenres()
                val browse = movieRepository.getMoviesByGenre(null)
                _uiState.value = DashboardUiState.Success(top5, genres, browse)
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onGenreSelected(genre: String?) {
        val prev = _selectedGenre.value
        _selectedGenre.value = genre
        reloadBrowse(onError = { _selectedGenre.value = prev })
    }

    fun onRatingSelected(rating: Int?) {
        val prev = _selectedRating.value
        _selectedRating.value = rating
        reloadBrowse(onError = { _selectedRating.value = prev })
    }

    private fun reloadBrowse(onError: () -> Unit = {}) {
        reloadJob?.cancel()
        reloadJob = viewModelScope.launch {
            val current = (_uiState.value as? DashboardUiState.Success) ?: return@launch
            try {
                val browse = movieRepository.getMoviesByGenre(
                    genre = _selectedGenre.value,
                    minVoteAverage = _selectedRating.value?.toDouble()
                )
                _uiState.value = current.copy(browseMovies = browse)
            } catch (e: Exception) {
                onError()
                _uiState.value = current
            }
        }
    }
}
