package com.example.marsphotos.fake

import com.example.marsphotos.data.MarsPhotosRepository
import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.network.MarsApiService

object FakeDataSource {
    val photosList = listOf(MarsPhoto("1", "https://example.com/1.jpg"),
        MarsPhoto("2", "https://example.com/2.jpg"))
}

class FakeMarsApiService : MarsApiService {
    override suspend fun getPhotos() = FakeDataSource.photosList
}

class FakeNetworkMarsPhotosRepository : MarsPhotosRepository {
    override suspend fun getMarsPhotos() = FakeDataSource.photosList
}
