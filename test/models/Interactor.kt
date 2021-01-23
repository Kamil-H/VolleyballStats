package com.kamilh.models

import com.kamilh.interactors.Interactor
import com.kamilh.interactors.NoInputInteractor

fun <P, T> interactorOf(f: suspend (P) -> T): Interactor<P, T> =
    object : Interactor<P, T>(appDispatchersOf()) {
        override suspend fun doWork(params: P): T = f(params)
    }

fun <P, T> interactorOf(t: T): Interactor<P, T> = interactorOf { t }

fun <T> noInputInteractorOf(f: suspend () -> T): NoInputInteractor<T> =
    object : NoInputInteractor<T>(appDispatchersOf()) {
        override suspend fun doWork(): T = f()
    }

fun <T> noInputInteractorOf(t: T): NoInputInteractor<T> = noInputInteractorOf { t }