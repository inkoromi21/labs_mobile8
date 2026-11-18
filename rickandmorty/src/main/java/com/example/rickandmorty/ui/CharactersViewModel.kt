package com.example.rickandmorty.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.rickandmorty.RickAndMortyApplication
import com.example.rickandmorty.data.Character
import com.example.rickandmorty.data.CharactersRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class CharactersUiState(
    val characters: List<Character> = emptyList(),
    val nextPage: Int? = 1,
    val isLoading: Boolean = false,
    val error: Boolean = false,
)

class CharactersViewModel(private val repository: CharactersRepository) : ViewModel() {
    var uiState by mutableStateOf(CharactersUiState())
        private set

    init { loadNextPage() }

    fun loadNextPage() {
        val page = uiState.nextPage ?: return
        if (uiState.isLoading || uiState.error) return
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val result = repository.getCharacters(page)
                uiState = uiState.copy(
                    characters = (uiState.characters + result.results).distinctBy { it.id },
                    nextPage = if (result.info.next == null) null else page + 1,
                    isLoading = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = true)
            }
        }
    }

    fun retry() {
        if (uiState.isLoading) return
        uiState = uiState.copy(error = false)
        loadNextPage()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as RickAndMortyApplication
                CharactersViewModel(app.container.charactersRepository)
            }
        }
    }
}
