package com.example.rickandmorty.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CharactersApiService {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): CharacterPage

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): Character

    @GET("episode/{id}")
    suspend fun getEpisode(@Path("id") id: Int): Episode

    @GET("episode/{ids}")
    suspend fun getEpisodes(@Path("ids") ids: String): List<Episode>
}

interface CharactersRepository {
    suspend fun getCharacters(page: Int): CharacterPage
    suspend fun getCharacter(id: Int): Character
    suspend fun getEpisodes(character: Character): List<Episode>
}

class NetworkCharactersRepository(private val api: CharactersApiService) : CharactersRepository {
    override suspend fun getCharacters(page: Int) = api.getCharacters(page)
    override suspend fun getCharacter(id: Int) = api.getCharacter(id)

    override suspend fun getEpisodes(character: Character): List<Episode> {
        val ids = character.episode.map { it.substringAfterLast('/').toInt() }
        return when (ids.size) {
            0 -> emptyList()
            1 -> listOf(api.getEpisode(ids.first()))
            else -> api.getEpisodes(ids.joinToString(","))
        }
    }
}
