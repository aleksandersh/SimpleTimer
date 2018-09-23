package io.github.aleksandersh.simpletimer

import android.app.Application
import io.github.aleksandersh.simpletimer.di.DI
import timber.log.Timber

class SimpleTimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DI.init(this)
        Timber.plant(Timber.DebugTree())
    }
}