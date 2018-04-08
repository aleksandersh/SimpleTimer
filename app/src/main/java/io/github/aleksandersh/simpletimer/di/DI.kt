package io.github.aleksandersh.simpletimer.di

import android.content.Context
import io.github.aleksandersh.simpletimer.di.application.ApplicationComponent
import io.github.aleksandersh.simpletimer.di.application.DaggerApplicationComponent
import io.github.aleksandersh.simpletimer.di.application.module.ApplicationModule

object DI {

    lateinit var applicationComponent: ApplicationComponent

    fun init(context: Context) {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(context))
            .build()
    }
}