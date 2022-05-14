package com.kamilh.volleyballstats.models.api

interface ResponseMapper<DOMAIN, RESPONSE> {

    fun to(from: DOMAIN): RESPONSE

    fun from(from: RESPONSE): DOMAIN
}