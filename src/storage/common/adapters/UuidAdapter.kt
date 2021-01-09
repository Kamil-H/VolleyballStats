package com.kamilh.storage.common.adapters

import com.squareup.sqldelight.ColumnAdapter
import java.util.*

internal class UuidAdapter : ColumnAdapter<UUID, String> {

    override fun decode(databaseValue: String): UUID = UUID.fromString(databaseValue)

    override fun encode(value: UUID): String = value.toString()
}