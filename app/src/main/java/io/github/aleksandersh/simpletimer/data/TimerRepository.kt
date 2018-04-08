package io.github.aleksandersh.simpletimer.data

import io.reactivex.Observable

interface TimerRepository {

    fun setTime(time: Long)

    fun observeTime(): Observable<Long>
}