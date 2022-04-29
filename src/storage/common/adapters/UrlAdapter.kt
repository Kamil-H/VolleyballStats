package com.kamilh.storage.common.adapters

import com.kamilh.models.Url
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class UrlAdapter : ColumnAdapter<Url, String> {
    override fun decode(databaseValue: String): Url = Url.create(databaseValue)

    override fun encode(value: Url): String = value.value
}