package com.kamilh.storage.common.adapters

import com.kamilh.models.Country
import com.squareup.sqldelight.ColumnAdapter

class CountryAdapter : ColumnAdapter<Country, String> {

    override fun decode(databaseValue: String): Country =
        Country(databaseValue)

    override fun encode(value: Country): String =
        value.code
}