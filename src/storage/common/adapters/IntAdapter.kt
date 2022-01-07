package com.kamilh.storage.common.adapters

import com.squareup.sqldelight.ColumnAdapter

class IntAdapter : ColumnAdapter<Int, Long> {

    override fun decode(databaseValue: Long): Int = databaseValue.toInt()

    override fun encode(value: Int): Long = value.toLong()
}