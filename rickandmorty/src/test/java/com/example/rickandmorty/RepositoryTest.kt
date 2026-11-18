package com.example.rickandmorty

import com.example.rickandmorty.data.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RepositoryTest {
    private val api = FakeApi()
    private val repository = NetworkCharactersRepository(api)

    @Test fun requestedPageIsPassedToApi() = runTest {
        val result = repository.getCharacters(4)
        assertEquals(4, api.requestedPage)
        assertEquals(4, result.results.single().id)
    }
    @Test fun oneEpisodeUsesObjectEndpoint() = runTest {
        assertEquals(listOf(api.episode), repository.getEpisodes(person(1)))
        assertEquals(1, api.singleId)
        assertNull(api.batchIds)
    }
    @Test fun manyEpisodesUseOneBatchRequest() = runTest {
        val character = person(1).copy(episode = listOf("https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/2"))
        assertEquals(2, repository.getEpisodes(character).size)
        assertEquals("1,2", api.batchIds)
        assertNull(api.singleId)
    }
    @Test fun noEpisodesMakesNoRequest() = runTest {
        assertTrue(repository.getEpisodes(person(1).copy(episode = emptyList())).isEmpty())
        assertNull(api.singleId)
        assertNull(api.batchIds)
    }
}

private class FakeApi : CharactersApiService {
    var requestedPage: Int? = null
    var singleId: Int? = null
    var batchIds: String? = null
    val episode = Episode(1, "Pilot", "S01E01", "December 2, 2013")
    override suspend fun getCharacters(page: Int): CharacterPage {
        requestedPage = page
        return CharacterPage(PageInfo(), listOf(person(page)))
    }
    override suspend fun getCharacter(id: Int) = person(id)
    override suspend fun getEpisode(id: Int): Episode {
        singleId = id
        return episode
    }
    override suspend fun getEpisodes(ids: String): List<Episode> {
        batchIds = ids
        return listOf(episode, episode.copy(id = 2))
    }
}
