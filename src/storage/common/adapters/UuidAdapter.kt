package com.kamilh.storage.common.adapters

import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UuidAdapter : ColumnAdapter<UUID, String> {

    override fun decode(databaseValue: String): UUID = UUID.fromString(databaseValue)

    override fun encode(value: UUID): String = value.toString()
}