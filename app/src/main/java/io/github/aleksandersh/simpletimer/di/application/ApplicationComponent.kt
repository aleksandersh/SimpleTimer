package io.github.aleksandersh.simpletimer.di.application

import dagger.Component
import io.github.aleksandersh.simpletimer.di.application.module.ApplicationModule
import io.github.aleksandersh.simpletimer.presentation.main.MainActivity
import io.github.aleksandersh.simpletimer.presentation.service.TimerService
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(target: MainActivity)

    fun inject(target: TimerService)
}