package com.kamilh.volleyballstats.api

interface ResponseMapper<DOMAIN, RESPONSE> {

    fun to(from: DOMAIN): RESPONSE

    fun from(from: RESPONSE): DOMAIN
}
