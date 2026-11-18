package com.example.marsphotos

import com.example.marsphotos.data.MarsPhotosRepository
import com.example.marsphotos.fake.FakeDataSource
import com.example.marsphotos.fake.FakeNetworkMarsPhotosRepository
import com.example.marsphotos.rules.TestDispatcherRule
import com.example.marsphotos.ui.screens.MarsUiState
import com.example.marsphotos.ui.screens.MarsViewModel
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarsViewModelTest {
    @get:Rule val dispatcherRule = TestDispatcherRule()

    @Test fun loadingChangesToSuccess() = runTest {
        val model = MarsViewModel(FakeNetworkMarsPhotosRepository())
        assertEquals(MarsUiState.Loading, model.marsUiState)
        advanceUntilIdle()
        assertEquals(MarsUiState.Success(FakeDataSource.photosList), model.marsUiState)
    }

    @Test fun failedRequestCanBeRetried() = runTest {
        var fail = true
        val repository = object : MarsPhotosRepository {
            override suspend fun getMarsPhotos() = if (fail) throw IOException() else FakeDataSource.photosList
        }
        val model = MarsViewModel(repository)
        advanceUntilIdle()
        assertEquals(MarsUiState.Error, model.marsUiState)
        fail = false
        model.getMarsPhotos()
        advanceUntilIdle()
        assertEquals(MarsUiState.Success(FakeDataSource.photosList), model.marsUiState)
    }

    @Test fun repeatedClicksDoNotDuplicateActiveRequest() = runTest {
        var requests = 0
        val repository = object : MarsPhotosRepository {
            override suspend fun getMarsPhotos(): List<com.example.marsphotos.model.MarsPhoto> {
                requests++
                delay(100)
                return FakeDataSource.photosList
            }
        }
        val model = MarsViewModel(repository)
        model.getMarsPhotos()
        model.getMarsPhotos()
        advanceUntilIdle()
        assertEquals(1, requests)
    }
}
