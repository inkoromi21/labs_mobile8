package com.example.rickandmorty.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rickandmorty.R

@Composable
fun CharacterScreen(state: CharacterUiState, retry: () -> Unit) {
    when (state) {
        CharacterUiState.Loading -> LoadingScreen()
        CharacterUiState.Error -> NetworkError(retry)
        is CharacterUiState.Success -> {
            val person = state.character
            LazyColumn(contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    AsyncImage(model = person.image, contentDescription = "Фото ${person.name}",
                        placeholder = painterResource(R.drawable.person_placeholder),
                        error = painterResource(R.drawable.person_placeholder),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth().height(250.dp))
                }
                item { Text(person.name, style = MaterialTheme.typography.headlineMedium) }
                item {
                    val status = when (person.status) {
                        "Alive" -> "Жив"
                        "Dead" -> "Мёртв"
                        else -> "Неизвестно"
                    }
                    val gender = when (person.gender) {
                        "Male" -> "Мужской"
                        "Female" -> "Женский"
                        "Genderless" -> "Бесполый"
                        else -> "Неизвестно"
                    }
                    Text("Вид: ${speciesName(person.species)}\nСтатус: $status\nПол: $gender" +
                        (if (person.type.isBlank()) "" else "\nТип: ${person.type}") +
                        "\nПроисхождение: ${person.origin.name}\nМестонахождение: ${person.location.name}")
                }
                item { Text("Эпизоды", style = MaterialTheme.typography.titleLarge) }
                if (state.episodes.isEmpty()) item { Text("Эпизоды не указаны") }
                items(state.episodes, key = { it.id }) { episode ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("${episode.episode} · ${episode.name}", style = MaterialTheme.typography.titleMedium)
                            Text(episode.airDate)
                        }
                    }
                }
            }
        }
    }
}
