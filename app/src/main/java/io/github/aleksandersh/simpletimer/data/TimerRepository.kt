package io.github.aleksandersh.simpletimer.data

import io.reactivex.Observable

interface TimerRepository {

    fun setTime(time: Int)

    fun observeTime(): Observable<Int>
}