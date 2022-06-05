package com.kamilh.volleyballstats.domain

import com.kamilh.volleyballstats.domain.models.Url

fun urlOf(url: String = "www.google.com"): Url = Url.create(url)