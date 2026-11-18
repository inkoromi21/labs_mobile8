/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.racetracker.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

/**
 * Хранит прогресс участника и двигает его с заданным интервалом.
 */
class RaceParticipant(
    val name: String,
    val maxProgress: Int = 100,
    val progressDelayMillis: Long = 500L,
    private val progressIncrement: Int = 1,
    initialProgress: Int = 0
) {
    init {
        require(maxProgress > 0) { "maxProgress=$maxProgress; must be > 0" }
        require(progressIncrement > 0) { "progressIncrement=$progressIncrement; must be > 0" }
        require(progressDelayMillis > 0) { "progressDelayMillis=$progressDelayMillis; must be > 0" }
        require(initialProgress in 0..maxProgress) {
            "initialProgress=$initialProgress; must be between 0 and maxProgress"
        }
    }

    var currentProgress by mutableStateOf(initialProgress)
        private set

    suspend fun run() {
        try {
            while (currentProgress < maxProgress) {
                delay(progressDelayMillis)
                currentProgress += minOf(progressIncrement, maxProgress - currentProgress)
            }
        } catch (exception: CancellationException) {
            Log.e("RaceParticipant", "$name was cancelled", exception)
            throw exception
        }
    }

    fun reset() {
        currentProgress = 0
    }

    val progressFactor: Float
        get() = currentProgress / maxProgress.toFloat()
}
