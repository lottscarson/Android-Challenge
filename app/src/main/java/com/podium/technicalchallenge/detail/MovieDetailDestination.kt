package com.podium.technicalchallenge.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage

@Composable
fun MovieDetailDestination(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val viewModel = hiltViewModel<MovieDetailViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    MovieDetailScreen(uiState = uiState, onBack = onBack)
}

@Composable
fun MovieDetailScreen(
    uiState: DetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState is DetailUiState.Success) {
                        Text(
                            text = uiState.movie.title,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        when (uiState) {
            is DetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.message)
                }
            }
            is DetailUiState.Success -> {
                MovieDetailContent(movie = uiState.movie, modifier = Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun MovieDetailContent(
    movie: com.podium.technicalchallenge.common.Movie,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
            ) {
                AsyncImage(
                    model = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⭐ ${"%.1f".format(movie.voteAverage)}  •  ${movie.voteCount} votes",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    movie.genres.forEach { genre ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, MaterialTheme.colors.primary.copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = genre,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                val meta = buildList {
                    if (movie.runtime > 0) add("${movie.runtime} min")
                    if (movie.releaseDate.isNotEmpty()) add(movie.releaseDate.take(4))
                    if (movie.budget > 0) add("$${"%,d".format(movie.budget)}")
                }
                if (meta.isNotEmpty()) {
                    Text(
                        text = meta.joinToString("  •  "),
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.body1
                )
                movie.director?.let { director ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Directed by ${director.name}",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        if (movie.cast.isNotEmpty()) {
            item {
                Text(
                    text = "Cast",
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            items(movie.cast.sortedBy { it.order }.take(5)) { member ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = member.name,
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = member.character,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
