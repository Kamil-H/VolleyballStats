package com.kamilh.volleyballstats.interactors

import kotlin.test.Test
import kotlin.test.assertEquals

class SynchronizeStateHolderTest {

    @Test
    fun `SynchronizeStateHolder has Idle as init state`() {
        // GIVEN
        val stateHolder = SynchronizeStateHolder()

        // THEN
        assertEquals(SynchronizeState.Idle, stateHolder.receive().value)
    }

    @Test
    fun `SynchronizeStateHolder emits state correctly`() {
        // GIVEN
        val stateHolder = SynchronizeStateHolder()
        val state = SynchronizeState.Error

        // WHEN
        stateHolder.send(state)

        // THEN
        assertEquals(state, stateHolder.receive().value)
    }
}
