package com.kamilh.storage.common.adapters

import com.kamilh.models.Url
import com.squareup.sqldelight.ColumnAdapter

class UrlAdapter : ColumnAdapter<Url, String> {
    override fun decode(databaseValue: String): Url = Url.create(databaseValue)

    override fun encode(value: Url): String = value.value
}