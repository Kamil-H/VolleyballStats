package com.kamilh.authorization

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.provider
import storage.AccessTokenValidator
import storage.InMemoryAccessTokenValidator
import storage.InMemorySubscriptionKeyStorage
import storage.SubscriptionKeyStorage

private const val MODULE_NAME = "DI_STORAGE_MODULE"
val storageModule = DI.Module(name = MODULE_NAME) {
    bind<SubscriptionKeyStorage>() with provider { InMemorySubscriptionKeyStorage() }
    bind<AccessTokenValidator>() with provider { InMemoryAccessTokenValidator() }
}