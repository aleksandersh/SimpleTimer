package io.github.aleksandersh.simpletimer.presentation.main

import io.github.aleksandersh.simpletimer.presentation.util.ResourceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainPresenterFactory
@Inject
constructor(private val resourceManager: ResourceManager) {

    fun create(): MainPresenter {
        return MainPresenter(resourceManager)
    }
}