package com.kamilh.extensions

import org.junit.Test

class DividedListTest {

    @Test
    fun `test that list is getting divided properly when index is in the middle`() {
        // GIVEN
        val pivot = 3
        val first = listOf(1, 2) + pivot
        val second = listOf(4, 5, 6)
        val listToDivide = first + second

        // WHEN
        val divided = listToDivide.divideExcluding(pivotIndex = first.indexOf(pivot))

        // THEN
        assert(divided.before == first - pivot)
        assert(divided.after == second)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `test that exception is thrown when index is below 0`() {
        // GIVEN
        val listToDivide = listOf<Int>()

        // WHEN
        listToDivide.divideExcluding(pivotIndex = -1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `test that exception is thrown when index is above list size`() {
        // GIVEN
        val listToDivide = listOf<Int>()

        // WHEN
        listToDivide.divideExcluding(pivotIndex = 1)
    }

    @Test
    fun `test that before and after lists are empty when list to divide is also empty`() {
        // GIVEN
        val listToDivide = listOf<Int>()

        // WHEN
        val divided = listToDivide.divideExcluding(pivotIndex = 0)

        // THEN
        assert(divided.before.isEmpty())
        assert(divided.after.isEmpty())
    }

    @Test
    fun `test that before lists is empty when pivot is equal to 0`() {
        // GIVEN
        val listToDivide = listOf(0, 1, 2)

        // WHEN
        val divided = listToDivide.divideExcluding(pivotIndex = 0)

        // THEN
        assert(divided.before.isEmpty())
        assert(divided.after == listToDivide.drop(1))
    }

    @Test
    fun `test that after lists is empty when pivot is equal to list size`() {
        // GIVEN
        val listToDivide = listOf(0, 1, 2)

        // WHEN
        val divided = listToDivide.divideExcluding(pivotIndex = listToDivide.lastIndex)

        // THEN
        assert(divided.before == listToDivide.dropLast(1))
        assert(divided.after.isEmpty())
    }
}