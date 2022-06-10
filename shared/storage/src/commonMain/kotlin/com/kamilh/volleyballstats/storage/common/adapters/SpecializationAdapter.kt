package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.domain.models.Specialization
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class SpecializationAdapter : ColumnAdapter<Specialization, Long> {

    override fun decode(databaseValue: Long): Specialization = Specialization.create(databaseValue.toInt())

    override fun encode(value: Specialization): Long = value.id.toLong()
}