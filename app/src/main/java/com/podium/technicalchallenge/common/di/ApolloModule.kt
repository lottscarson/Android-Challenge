package com.podium.technicalchallenge.common.di

import com.apollographql.apollo.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApolloModule {
    private const val BASE_URL = "https://podium-fe-challenge-2021.netlify.app/.netlify/functions/graphql"

    // This should be in a secrets file, but for the purposes of this exercise, putting it here
    private const val API_KEY = "da2-7dph3xgybbffjn3mik4jii4equ"

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient =
        ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .addHttpHeader("x-api-key", API_KEY)
            .build()
}
