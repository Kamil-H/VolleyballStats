package storage

import com.kamilh.authorization.SubscriptionKey

interface SubscriptionKeyStorage {

    suspend fun insert(subscriptionKey: SubscriptionKey)

    suspend fun contains(subscriptionKey: SubscriptionKey): Boolean
}

class InMemorySubscriptionKeyStorage : SubscriptionKeyStorage {
    private val subscriptionKeys = mutableListOf<SubscriptionKey>()

    override suspend fun insert(subscriptionKey: SubscriptionKey) {
        subscriptionKeys.add(subscriptionKey)
    }

    override suspend fun contains(subscriptionKey: SubscriptionKey): Boolean = subscriptionKeys.contains(subscriptionKey)
}