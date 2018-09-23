package io.github.aleksandersh.simpletimer.presentation.service

interface Timer {

    fun start(time: Int)

    fun add(time: Int)

    fun resume()

    fun pause()

    fun stop()

    fun setTickListener(listener: (Int) -> Unit)
}