package io.github.aleksandersh.simpletimer.di.application.module

import dagger.Module
import dagger.Provides
import io.github.aleksandersh.simpletimer.data.TimerRepository
import io.github.aleksandersh.simpletimer.data.TimerRepositoryImpl
import javax.inject.Singleton

@Module
class TimerModule {

    @Singleton
    @Provides
    fun provideTimerRepository(): TimerRepository = TimerRepositoryImpl()
}