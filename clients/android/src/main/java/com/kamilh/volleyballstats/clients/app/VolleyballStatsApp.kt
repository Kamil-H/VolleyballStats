package com.kamilh.volleyballstats.clients.app

import android.app.Application
import com.kamilh.volleyballstats.clients.app.di.AppModule

class VolleyballStatsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val module = AppModule.getInstance(this)
        module.appInitializer.initialize()
    }
}
