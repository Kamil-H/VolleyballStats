package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.domain.models.Country
import app.cash.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class CountryAdapter : ColumnAdapter<Country, String> {

    override fun decode(databaseValue: String): Country =
        Country(databaseValue)

    override fun encode(value: Country): String =
        value.code
}
