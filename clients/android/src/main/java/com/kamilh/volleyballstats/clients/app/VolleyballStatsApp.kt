package com.kamilh.volleyballstats.clients.app

import android.app.Application
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.di.create
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VolleyballStatsApp : Application() {

    private val scope = CoroutineScope(context = Dispatchers.Main.immediate + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        val module = AppModule::class.create(this, scope)
        module.appInitializer.initialize()
        scope.launch {
            module.synchronizer.synchronize(League.POLISH_LEAGUE)
        }
    }
}
