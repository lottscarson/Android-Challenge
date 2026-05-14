package com.podium.technicalchallenge.common

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val voteAverage: Double,
    val genres: List<String>,
    val posterPath: String?,
    val releaseDate: String = "",
    val voteCount: Int = 0,
    val runtime: Int = 0,
    val budget: Int = 0,
    val director: Director? = null,
    val cast: List<CastMember> = emptyList()
)

data class Director(val id: Int, val name: String)

data class CastMember(
    val name: String,
    val character: String,
    val order: Int,
    val profilePath: String?
)
