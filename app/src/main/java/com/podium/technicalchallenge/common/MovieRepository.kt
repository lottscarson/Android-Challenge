package com.podium.technicalchallenge.common

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.podium.technicalchallenge.graphql.GetGenresQuery
import com.podium.technicalchallenge.graphql.GetMovieDetailQuery
import com.podium.technicalchallenge.graphql.GetMoviesByGenreQuery
import com.podium.technicalchallenge.graphql.GetTop5MoviesQuery
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(private val apolloClient: ApolloClient) {

    suspend fun getTop5Movies(): List<Movie> =
        apolloClient.query(GetTop5MoviesQuery()).execute()
            .dataOrThrow().movies.map { it.toDomainSummary() }

    suspend fun getGenres(): List<String> =
        apolloClient.query(GetGenresQuery()).execute()
            .dataOrThrow().genres

    suspend fun getMoviesByGenre(genre: String?): List<Movie> =
        apolloClient.query(GetMoviesByGenreQuery(genre = Optional.presentIfNotNull(genre))).execute()
            .dataOrThrow().movies.map { it.toDomainSummary() }

    suspend fun getMovieDetail(id: Int): Movie =
        apolloClient.query(GetMovieDetailQuery(id = id)).execute()
            .dataOrThrow().movie.toDomainDetail()
}

private fun GetTop5MoviesQuery.Movie.toDomainSummary() = Movie(
    id = id,
    title = title,
    overview = overview,
    voteAverage = voteAverage,
    genres = genres,
    posterPath = posterPath
)

private fun GetMoviesByGenreQuery.Movie.toDomainSummary() = Movie(
    id = id,
    title = title,
    overview = overview,
    voteAverage = voteAverage,
    genres = genres,
    posterPath = posterPath
)

private fun GetMovieDetailQuery.Movie.toDomainDetail() = Movie(
    id = id,
    title = title,
    overview = overview,
    voteAverage = voteAverage,
    genres = genres,
    posterPath = posterPath,
    releaseDate = releaseDate,
    voteCount = voteCount,
    runtime = runtime,
    budget = budget,
    director = director?.let { Director(id = it.id, name = it.name) },
    cast = cast.map { CastMember(name = it.name, character = it.character, order = it.order, profilePath = it.profilePath) }
)
