package com.example.rickandmorty.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterPage(val info: PageInfo, val results: List<Character>)

@Serializable
data class PageInfo(val next: String? = null)

@Serializable
data class Place(val name: String)

@Serializable
data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String = "",
    val gender: String,
    val origin: Place,
    val location: Place,
    val image: String,
    val episode: List<String>,
)

@Serializable
data class Episode(
    val id: Int,
    val name: String,
    val episode: String,
    @SerialName("air_date") val airDate: String,
)
