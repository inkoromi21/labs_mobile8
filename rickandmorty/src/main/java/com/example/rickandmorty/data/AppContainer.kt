package com.example.rickandmorty.data

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val charactersRepository: CharactersRepository
}

class DefaultAppContainer : AppContainer {
    private val json = Json { ignoreUnknownKeys = true }
    private val api: CharactersApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(CharactersApiService::class.java)
    }
    override val charactersRepository: CharactersRepository by lazy {
        NetworkCharactersRepository(api)
    }
}
