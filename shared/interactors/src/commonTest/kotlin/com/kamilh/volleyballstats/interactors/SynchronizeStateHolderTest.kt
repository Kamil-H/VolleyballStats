package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.storage.MatchStorage
import com.kamilh.volleyballstats.storage.matchStorageOf
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SynchronizeStateHolderTest {

    private fun TestScope.stateHolder(
        matchStorage: MatchStorage = matchStorageOf(),
    ): SynchronizeStateHolder = SynchronizeStateHolder(
        coroutineScope = this,
        matchStorage = matchStorage,
        appDispatchers = testAppDispatchers,
    )

    @Test
    fun `SynchronizeStateHolder has Idle as init state`() = runTest {
        // GIVEN
        val stateHolder = stateHolder()

        // THEN
        assertEquals(SynchronizeState.Idle, stateHolder.receive().value)
    }

    @Test
    fun `SynchronizeStateHolder emits state correctly`() = runTest {
        // GIVEN
        val stateHolder = stateHolder()
        val state = SynchronizeState.Error(type = SynchronizeState.Error.Type.Unexpected)

        // WHEN
        stateHolder.send(state)

        // THEN
        assertEquals(state, stateHolder.receive().value)
    }
}
