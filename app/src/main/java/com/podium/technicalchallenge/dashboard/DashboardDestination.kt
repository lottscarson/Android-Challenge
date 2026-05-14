package com.podium.technicalchallenge.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardDestination(onMovieClick: (Int) -> Unit) {
    val viewModel = hiltViewModel<DashboardViewModel>()
    DashboardScreen(onMovieClick = onMovieClick)
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onMovieClick: (Int) -> Unit = {}
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            Text("Dashboard — coming soon")
        }
    }
}
