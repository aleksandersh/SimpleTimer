package io.github.aleksandersh.simpletimer

import android.app.Application
import io.github.aleksandersh.simpletimer.di.DI

class SimpleTimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DI.init(this)
    }
}