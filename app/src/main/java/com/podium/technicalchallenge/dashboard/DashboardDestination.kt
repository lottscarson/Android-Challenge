package com.podium.technicalchallenge.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.podium.technicalchallenge.common.Movie

@Composable
fun DashboardDestination(onMovieClick: (Int) -> Unit) {
    val viewModel = hiltViewModel<DashboardViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val selectedRating by viewModel.selectedRating.collectAsState()
    DashboardScreen(
        uiState = uiState,
        selectedGenre = selectedGenre,
        selectedRating = selectedRating,
        onGenreSelected = viewModel::onGenreSelected,
        onRatingSelected = viewModel::onRatingSelected,
        onMovieClick = onMovieClick
    )
}

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    selectedGenre: String?,
    selectedRating: Int?,
    onGenreSelected: (String?) -> Unit,
    onRatingSelected: (Int?) -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("Top 5", "Browse")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Movie Explorer") })
        },
        modifier = modifier
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (uiState) {
                is DashboardUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DashboardUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.message)
                    }
                }
                is DashboardUiState.Success -> {
                    when (selectedTabIndex) {
                        0 -> Top5Tab(movies = uiState.top5, onMovieClick = onMovieClick)
                        1 -> BrowseTab(
                            genres = uiState.genres,
                            movies = uiState.browseMovies,
                            selectedGenre = selectedGenre,
                            selectedRating = selectedRating,
                            onGenreSelected = onGenreSelected,
                            onRatingSelected = onRatingSelected,
                            onMovieClick = onMovieClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Top5Tab(movies: List<Movie>, onMovieClick: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies) { movie ->
            MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
        }
    }
}

@Composable
private fun BrowseTab(
    genres: List<String>,
    movies: List<Movie>,
    selectedGenre: String?,
    selectedRating: Int?,
    onGenreSelected: (String?) -> Unit,
    onRatingSelected: (Int?) -> Unit,
    onMovieClick: (Int) -> Unit
) {
    Column {
        GenreChipRow(
            genres = genres,
            selectedGenre = selectedGenre,
            onGenreSelected = onGenreSelected
        )
        RatingChipRow(
            selectedRating = selectedRating,
            onRatingSelected = onRatingSelected
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GenreChipRow(
    genres: List<String>,
    selectedGenre: String?,
    onGenreSelected: (String?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            GenreChip(
                label = "All",
                isSelected = selectedGenre == null,
                onClick = { onGenreSelected(null) }
            )
        }
        items(genres) { genre ->
            GenreChip(
                label = genre,
                isSelected = genre == selectedGenre,
                onClick = { onGenreSelected(if (genre == selectedGenre) null else genre) }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RatingChipRow(
    selectedRating: Int?,
    onRatingSelected: (Int?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            GenreChip(
                label = "Any Rating",
                isSelected = selectedRating == null,
                onClick = { onRatingSelected(null) }
            )
        }
        items((9 downTo 1).toList()) { stars ->
            GenreChip(
                label = "$stars Stars +",
                isSelected = stars == selectedRating,
                onClick = { onRatingSelected(if (stars == selectedRating) null else stars) }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GenreChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Chip(
        onClick = onClick,
        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colors.primary) else null,
        colors = ChipDefaults.chipColors(
            backgroundColor = if (isSelected)
                MaterialTheme.colors.primary.copy(alpha = 0.1f)
            else
                MaterialTheme.colors.surface
        )
    ) {
        Text(label)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
            ) {
                AsyncImage(
                    model = movie.posterPath?.let { "https://image.tmdb.org/t/p/w185$it" },
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⭐ ${"%.1f".format(movie.voteAverage)}",
                    style = MaterialTheme.typography.body2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.genres.take(3).joinToString(" • "),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.body2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
