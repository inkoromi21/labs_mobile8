package com.example.rickandmorty.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rickandmorty.R
import com.example.rickandmorty.data.Character
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun CharactersScreen(
    state: CharactersUiState,
    loadNextPage: () -> Unit,
    retry: () -> Unit,
    onCharacterClick: (Int) -> Unit,
) {
    if (state.characters.isEmpty()) {
        if (state.error) NetworkError(retry)
        else LoadingScreen()
        return
    }
    val listState = rememberLazyListState()
    LaunchedEffect(listState, state.characters.size) {
        snapshotFlow {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            last >= state.characters.size - 4
        }.distinctUntilChanged().collect { nearEnd -> if (nearEnd) loadNextPage() }
    }
    LazyColumn(state = listState, contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Персонажи", style = MaterialTheme.typography.headlineMedium) }
        items(state.characters, key = { it.id }) { character ->
            CharacterRow(character) { onCharacterClick(character.id) }
        }
        item {
            when {
                state.isLoading -> Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.error -> NetworkError(retry, Modifier.fillMaxWidth())
                state.nextPage == null -> Text("Все персонажи загружены", Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun CharacterRow(character: Character, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = character.image, contentDescription = "Фото ${character.name}",
                placeholder = painterResource(R.drawable.person_placeholder),
                error = painterResource(R.drawable.person_placeholder),
                contentScale = ContentScale.Crop, modifier = Modifier.size(96.dp))
            Column(Modifier.padding(16.dp)) {
                Text(character.name, style = MaterialTheme.typography.titleMedium)
                Text(speciesName(character.species), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

fun speciesName(value: String) = when (value) {
    "Human" -> "Человек"
    "Alien" -> "Пришелец"
    "Humanoid" -> "Гуманоид"
    "Robot" -> "Робот"
    "Animal" -> "Животное"
    "Mythological Creature" -> "Мифическое существо"
    "Poopybutthole" -> "Пупибатхол"
    "Cronenberg" -> "Кроненберг"
    "Disease" -> "Болезнь"
    "unknown" -> "Неизвестно"
    else -> value
}

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
}

@Composable
fun NetworkError(retry: () -> Unit, modifier: Modifier = Modifier.fillMaxSize()) {
    Column(modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text("Не удалось загрузить данные. Проверьте подключение к интернету.")
        Button(onClick = retry, modifier = Modifier.padding(top = 12.dp)) { Text("Повторить") }
    }
}
