package com.example.rickandmorty.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RickAndMortyApp() {
    val controller = rememberNavController()
    val entry by controller.currentBackStackEntryAsState()
    val isDetail = entry?.destination?.route == "character/{id}"
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (isDetail) "Персонаж" else "Rick and Morty") },
            navigationIcon = {
                if (isDetail) IconButton(onClick = { controller.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
            },
        )
    }) { padding ->
        NavHost(controller, startDestination = "characters", modifier = Modifier.padding(padding)) {
            composable("characters") {
                val model: CharactersViewModel = viewModel(factory = CharactersViewModel.Factory)
                CharactersScreen(model.uiState, model::loadNextPage, model::retry,
                    onCharacterClick = { controller.navigate("character/$it") })
            }
            composable("character/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStack ->
                val id = requireNotNull(backStack.arguments).getInt("id")
                val model: CharacterViewModel = viewModel(factory = CharacterViewModel.factory(id))
                CharacterScreen(model.uiState, model::loadCharacter)
            }
        }
    }
}
