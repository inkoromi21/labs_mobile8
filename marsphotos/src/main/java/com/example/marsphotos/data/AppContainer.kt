package com.example.marsphotos.data

import com.example.marsphotos.network.MarsApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {
    val marsPhotosRepository: MarsPhotosRepository
}

class DefaultAppContainer : AppContainer {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://android-kotlin-fun-mars-server.appspot.com/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
    private val service: MarsApiService by lazy { retrofit.create(MarsApiService::class.java) }
    override val marsPhotosRepository: MarsPhotosRepository by lazy {
        NetworkMarsPhotosRepository(service)
    }
}
