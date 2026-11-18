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
import com.example.rickandmorty.data.Episode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface CharacterUiState {
    data object Loading : CharacterUiState
    data class Success(val character: Character, val episodes: List<Episode>) : CharacterUiState
    data object Error : CharacterUiState
}

class CharacterViewModel(
    private val repository: CharactersRepository,
    private val characterId: Int,
) : ViewModel() {
    var uiState: CharacterUiState by mutableStateOf(CharacterUiState.Loading)
        private set
    private var loadJob: Job? = null

    init { loadCharacter() }

    fun loadCharacter() {
        if (loadJob?.isActive == true) return
        uiState = CharacterUiState.Loading
        loadJob = viewModelScope.launch {
            uiState = try {
                val character = repository.getCharacter(characterId)
                CharacterUiState.Success(character, repository.getEpisodes(character))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                CharacterUiState.Error
            }
        }
    }

    companion object {
        fun factory(id: Int) = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as RickAndMortyApplication
                CharacterViewModel(app.container.charactersRepository, id)
            }
        }
    }
}
