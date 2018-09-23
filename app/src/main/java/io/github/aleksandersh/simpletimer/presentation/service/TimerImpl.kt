package io.github.aleksandersh.simpletimer.presentation.service

import android.os.Handler
import android.os.Looper
import timber.log.Timber
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TimerImpl : Timer {

    private val timer = AtomicInteger()
    private val currentTime = AtomicLong()
    private val timerScheduler = Executors.newSingleThreadScheduledExecutor()

    private val isPaused = AtomicBoolean(false)
    private val pauseTime = AtomicLong()
    private val pauseLock = ReentrantLock()
    private val pauseLockCondition = pauseLock.newCondition()

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var listener: ((Int) -> Unit)? = null

    override fun start(time: Int) {
        timer.set(time)
        currentTime.set(System.currentTimeMillis() + 1000)
        produce(time)
        timerScheduler.schedule(PeriodicTask(), 1000, TimeUnit.MILLISECONDS)
    }

    override fun add(time: Int) {
        val newTime = timer.addAndGet(time)
        produce(newTime)
    }

    override fun resume() {
        pauseLock.withLock {
            if (isPaused.compareAndSet(true, false)) {
                val pt = pauseTime.get()
                val ct = System.currentTimeMillis()
                currentTime.addAndGet(ct - pt)
                pauseLockCondition.signalAll()
            }
        }
    }

    override fun pause() {
        pauseLock.withLock {
            if (isPaused.compareAndSet(false, true)) {
                val ct = System.currentTimeMillis()
                pauseTime.set(ct)
            }
        }
    }

    override fun stop() {
        timerScheduler.shutdown()
    }

    override fun setTickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    private fun produce(tick: Int) {
        Timber.d("Timer produce: $tick")
        mainThreadHandler.post { listener?.invoke(tick) }
    }

    @Throws(InterruptedException::class)
    private fun checkPause() {
        if (isPaused.get()) {
            pauseLock.withLock {
                if (isPaused.get()) {
                    pauseLockCondition.await()
                }
            }
        }
    }

    private inner class PeriodicTask : Runnable {

        override fun run() {
            try {
                checkPause()
            } catch (exception: InterruptedException) {
                return
            }
            val ct = System.currentTimeMillis()
            val ct2 = ct + 500
            var nextTime = currentTime.get()
            while (nextTime < ct2) {
                nextTime = currentTime.addAndGet(1000)
                val currentTick = timer.decrementAndGet()
                produce(currentTick)
                if (currentTick <= 0) {
                    return
                }
            }
            val delay = nextTime - ct
            if (!Thread.interrupted()) {
                timerScheduler.schedule(this, delay, TimeUnit.MILLISECONDS)
            }
        }
    }
}