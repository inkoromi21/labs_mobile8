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
package com.example.racetracker

import com.example.racetracker.ui.RaceParticipant
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class RaceParticipantTest {
    @Test
    fun runIncrementsAndFinishesAtMaximum() = runTest {
        val participant = RaceParticipant(name = "Тест", progressDelayMillis = 100L)
        val race = launch { participant.run() }

        advanceTimeBy(100L)
        runCurrent()
        assertEquals(1, participant.currentProgress)

        advanceUntilIdle()
        race.join()
        assertEquals(100, participant.currentProgress)
        assertEquals(1f, participant.progressFactor)
    }

    @Test
    fun cancellationStopsRunAndResetClearsProgress() = runTest {
        val participant = RaceParticipant(name = "Тест", progressDelayMillis = 100L)
        val race = launch { participant.run() }

        advanceTimeBy(100L)
        runCurrent()
        assertEquals(1, participant.currentProgress)

        race.cancelAndJoin()
        assertTrue(race.isCancelled)
        participant.reset()
        assertEquals(0, participant.currentProgress)
    }
}
