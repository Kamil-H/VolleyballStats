package com.kamilh.storage.common.adapters

import com.kamilh.models.Country
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class CountryAdapter : ColumnAdapter<Country, String> {

    override fun decode(databaseValue: String): Country =
        Country(databaseValue)

    override fun encode(value: Country): String =
        value.code
}