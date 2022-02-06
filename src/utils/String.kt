package com.kamilh.utils

import kotlin.math.max
import kotlin.math.min

private fun getLevenshteinDistance(first: String, second: String): Int {
    val m = first.length
    val n = second.length
    val array = Array(m + 1) { IntArray(n + 1) }
    for (i in 1..m) {
        array[i][0] = i
    }
    for (j in 1..n) {
        array[0][j] = j
    }
    var cost: Int
    for (i in 1..m) {
        for (j in 1..n) {
            cost = if (first[i - 1] == second[j - 1]) 0 else 1
            array[i][j] = min(min(array[i - 1][j] + 1, array[i][j - 1] + 1), array[i - 1][j - 1] + cost)
        }
    }
    return array[m][n]
}

fun String.findSimilarity(other: String): Double {
    val maxLength = max(this.length, other.length)
    return if (maxLength > 0) {
        (maxLength * 1.0 - getLevenshteinDistance(this, other)) / maxLength * 1.0
    } else 1.0
}