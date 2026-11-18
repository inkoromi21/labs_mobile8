package com.example.rickandmorty

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.rickandmorty.data.Character
import com.example.rickandmorty.data.Episode
import com.example.rickandmorty.data.Place
import com.example.rickandmorty.ui.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ScreensTest {
    @get:Rule val compose = createComposeRule()
    private val character = Character(1, "Rick Sanchez", "Alive", "Human", "", "Male",
        Place("Earth"), Place("Citadel"), "", emptyList())

    @Test fun characterCardOpensSelectedId() {
        var selected = 0
        compose.setContent { MaterialTheme {
            CharactersScreen(CharactersUiState(listOf(character), nextPage = null), {}, {}, { selected = it })
        } }
        compose.onNodeWithText("Rick Sanchez").performClick()
        compose.runOnIdle { assertEquals(1, selected) }
    }

    @Test fun errorRetryCallsAction() {
        var retries = 0
        compose.setContent { MaterialTheme {
            CharactersScreen(CharactersUiState(error = true), {}, { retries++ }, {})
        } }
        compose.onNodeWithText("Повторить").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun scrollingNearEndRequestsNextPage() {
        var requests = 0
        val people = (1..20).map { character.copy(id = it, name = "Character $it") }
        compose.setContent { MaterialTheme {
            CharactersScreen(CharactersUiState(people, nextPage = 2), { requests++ }, {}, {})
        } }
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(20)
        compose.waitUntil { requests > 0 }
    }

    @Test fun detailShowsInformationAndEpisode() {
        compose.setContent { MaterialTheme {
            CharacterScreen(CharacterUiState.Success(character,
                listOf(Episode(1, "Pilot", "S01E01", "December 2, 2013"))), {})
        } }
        compose.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        compose.onNodeWithText("S01E01 · Pilot").performScrollTo().assertIsDisplayed()
    }
}
