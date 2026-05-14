package com.podium.technicalchallenge.dashboard

import androidx.lifecycle.ViewModel
import com.podium.technicalchallenge.common.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel()
