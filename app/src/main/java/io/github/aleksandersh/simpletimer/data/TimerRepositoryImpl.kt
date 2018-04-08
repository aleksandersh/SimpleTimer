package io.github.aleksandersh.simpletimer.data

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TimerRepositoryImpl : TimerRepository {

    private val publisher: BehaviorSubject<Long> = BehaviorSubject.create()

    override fun setTime(time: Long) {
        publisher.onNext(time)
    }

    override fun observeTime(): Observable<Long> = publisher
}