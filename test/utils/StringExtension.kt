package com.kamilh.utils

import org.junit.Test

class StringExtensionTest {

    @Test
    fun `findSimilarity works correctly`() {
        // GIVEN
        val strings = listOf(
            "AABB",
            "AAABBB",
            "A",
            "Maskymilian Granieczny",
            "Krzysztof Granieczny",
            "Marcin Wika",
        )
        val similarStrings = listOf(
            "AAABB",
            "AABBB",
            "AABBB",
            "Maksymilian Granieczny",
            "Maksymilian Granieczny",
            "Mateusz Mika",
        )
        val similarityAboveThreshold = listOf(true, true, false, true, false, false)
        val threshold = 0.7

        // WHEN
        val results = strings.mapIndexed { index, s -> similarStrings[index].findSimilarity(s) }

        // THEN
        results.forEachIndexed { index, d ->
            if (similarityAboveThreshold[index]) {
                assert(d >= threshold)
            } else {
                assert(d < threshold)
            }
        }
    }
}