package com.example.rickandmorty

import com.example.rickandmorty.data.*
import com.example.rickandmorty.ui.*
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun pagesAppendAndStopAtLastPage() = runTest(dispatcher) {
        val repository = FakeRepository()
        val model = CharactersViewModel(repository)
        assertTrue(model.uiState.isLoading)
        advanceUntilIdle()
        assertEquals(listOf(1), model.uiState.characters.map { it.id })
        model.loadNextPage()
        model.loadNextPage()
        advanceUntilIdle()
        model.loadNextPage()
        assertEquals(listOf(1, 2), model.uiState.characters.map { it.id })
        assertNull(model.uiState.nextPage)
        assertEquals(listOf(1, 2), repository.pages)
    }

    @Test fun failedPageKeepsListAndRetryRequestsSamePage() = runTest(dispatcher) {
        val repository = FakeRepository()
        val model = CharactersViewModel(repository)
        advanceUntilIdle()
        repository.fail = true
        model.loadNextPage()
        advanceUntilIdle()
        assertTrue(model.uiState.error)
        assertEquals(1, model.uiState.characters.size)
        model.loadNextPage()
        advanceUntilIdle()
        assertEquals(listOf(1, 2), repository.pages)
        repository.fail = false
        model.retry()
        advanceUntilIdle()
        assertFalse(model.uiState.error)
        assertEquals(listOf(1, 2, 2), repository.pages)
        assertEquals(listOf(1, 2), model.uiState.characters.map { it.id })
    }

    @Test fun firstPageFailureCanBeRetried() = runTest(dispatcher) {
        val repository = FakeRepository().apply { fail = true }
        val model = CharactersViewModel(repository)
        advanceUntilIdle()
        assertTrue(model.uiState.error)
        assertTrue(model.uiState.characters.isEmpty())
        repository.fail = false
        model.retry()
        advanceUntilIdle()
        assertEquals(1, model.uiState.characters.single().id)
    }

    @Test fun detailLoadsSelectedCharacterAndEpisodes() = runTest(dispatcher) {
        val model = CharacterViewModel(FakeRepository(), 42)
        assertEquals(CharacterUiState.Loading, model.uiState)
        advanceUntilIdle()
        val result = model.uiState as CharacterUiState.Success
        assertEquals(42, result.character.id)
        assertEquals("S01E01", result.episodes.single().episode)
    }

    @Test fun detailFailureAndRetry() = runTest(dispatcher) {
        val repository = FakeRepository().apply { fail = true }
        val model = CharacterViewModel(repository, 42)
        advanceUntilIdle()
        assertEquals(CharacterUiState.Error, model.uiState)
        repository.fail = false
        model.loadCharacter()
        advanceUntilIdle()
        assertTrue(model.uiState is CharacterUiState.Success)
    }

    @Test fun episodeFailureShowsErrorAndRetryRestoresDetail() = runTest(dispatcher) {
        val repository = FakeRepository().apply { failEpisodes = true }
        val model = CharacterViewModel(repository, 1)
        advanceUntilIdle()
        assertEquals(CharacterUiState.Error, model.uiState)
        repository.failEpisodes = false
        model.loadCharacter()
        advanceUntilIdle()
        assertEquals(1, (model.uiState as CharacterUiState.Success).episodes.size)
    }
}

private class FakeRepository : CharactersRepository {
    val pages = mutableListOf<Int>()
    var fail = false
    var failEpisodes = false
    override suspend fun getCharacters(page: Int): CharacterPage {
        pages += page
        delay(10)
        if (fail) throw IOException()
        return CharacterPage(PageInfo(if (page == 1) "page2" else null), listOf(person(page)))
    }
    override suspend fun getCharacter(id: Int): Character {
        if (fail) throw IOException()
        return person(id)
    }
    override suspend fun getEpisodes(character: Character): List<Episode> {
        if (failEpisodes) throw IOException()
        return listOf(Episode(1, "Pilot", "S01E01", "December 2, 2013"))
    }
}

internal fun person(id: Int) = Character(id, "Rick $id", "Alive", "Human", "", "Male",
    Place("Earth"), Place("Earth"), "", listOf("https://rickandmortyapi.com/api/episode/1"))
