package io.github.aleksandersh.simpletimer.di.application

import dagger.Component
import io.github.aleksandersh.simpletimer.di.application.module.ApplicationModule
import io.github.aleksandersh.simpletimer.di.application.module.TimerModule
import io.github.aleksandersh.simpletimer.presentation.main.MainActivity
import io.github.aleksandersh.simpletimer.presentation.service.TimerService
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, TimerModule::class])
interface ApplicationComponent {

    fun inject(target: MainActivity)

    fun inject(target: TimerService)
}