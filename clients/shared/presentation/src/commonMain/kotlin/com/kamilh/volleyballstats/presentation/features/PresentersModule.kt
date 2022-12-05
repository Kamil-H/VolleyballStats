package com.kamilh.volleyballstats.presentation.features

import com.kamilh.volleyballstats.presentation.features.filter.PlayerFiltersPresenter
import com.kamilh.volleyballstats.presentation.features.home.HomePresenter
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.presentation.features.players.PlayerStatsPresenter
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides
import kotlin.reflect.KClass

typealias PresenterMap = Map<KClass<*>, Presenter.Factory<*, *>>
typealias PresenterMapEntry = Pair<KClass<*>, Presenter.Factory<*, *>>

interface PresentersModule {

    val presenterMap: Map<KClass<*>, Presenter.Factory<*, *>>

    private inline fun <reified T: Presenter> Presenter.Factory<T, *>.toPresenterMapEntry(): PresenterMapEntry =
        T::class to this

    @IntoMap
    @Provides
    fun providePresenter(factory: PlayerFiltersPresenter.Factory): Pair<KClass<*>, Presenter.Factory<*, *>> =
        factory.toPresenterMapEntry()

    @IntoMap
    @Provides
    fun providePresenter(factory: PlayerStatsPresenter.Factory): Pair<KClass<*>, Presenter.Factory<*, *>> =
        factory.toPresenterMapEntry()

    @IntoMap
    @Provides
    fun providePresenter(factory: MainPresenter.Factory): Pair<KClass<*>, Presenter.Factory<*, *>> =
        factory.toPresenterMapEntry()

    @IntoMap
    @Provides
    fun providePresenter(factory: HomePresenter.Factory): Pair<KClass<*>, Presenter.Factory<*, *>> =
        factory.toPresenterMapEntry()
}

inline fun <reified T : Presenter, reified E : Any> create(
    presenterMap: PresenterMap,
    coroutineScope: CoroutineScope,
    savableMap: SavableMap,
    extras: E,
): T {
    val key = presenterMap.keys.filterIsInstance<KClass<Presenter>>().find {
        it.simpleName == T::class.simpleName
    } ?: error("Factory for a given type ${T::class} not found")
    val factory = presenterMap[key] as? Presenter.Factory<T, E> ?: error("Wrong extras type: ${E::class}")
    return factory.create(
        coroutineScope = coroutineScope,
        savableMap = savableMap,
        extras = extras,
    )
}
