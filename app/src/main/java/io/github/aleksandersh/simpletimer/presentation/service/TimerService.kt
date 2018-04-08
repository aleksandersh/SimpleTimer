package io.github.aleksandersh.simpletimer.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import io.github.aleksandersh.simpletimer.R
import io.github.aleksandersh.simpletimer.data.TimerRepository
import io.github.aleksandersh.simpletimer.di.DI
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

class TimerService : Service() {

    companion object {

        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID =
            "io.github.aleksandersh.simpletimer.TimerService.notification_channel"
    }

    @Inject
    internal lateinit var timerRepository: TimerRepository

    private val currentTime: AtomicLong = AtomicLong(0)
    @Volatile
    private var timerDisposable: Disposable? = null
    private var started: Boolean = false

    override fun onBind(intent: Intent): IBinder = TimerBinder()

    override fun onCreate() {
        super.onCreate()

        DI.applicationComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        timerDisposable?.dispose()
    }

    private fun checkServiceStarted(): Boolean {
        return started
    }

    private fun makeForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.timer_notification_channel_name),
                NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentText(getString(R.string.timer_notification_title))
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startService(time: Long) {
        started = true
        makeForeground()
        startTimer(time)
    }

    private fun startTimer(time: Long) {
        timerDisposable?.dispose()
        currentTime.set(time)
        timerRepository.setTime(time)
        timerDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS, Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onNextTick() }
    }

    private fun stopService() {
        started = false
        stopTimer()
        stopForeground(true)
        stopSelf()
    }

    private fun stopTimer() {
        timerDisposable?.dispose()
    }

    private fun onNextTick() {
        changeTime(-1)
    }

    private fun changeTime(delta: Long) {
        val newTime = currentTime.addAndGet(delta)
        timerRepository.setTime(newTime)
        if (newTime <= 0) {
            stopTimer()
        }
    }

    inner class TimerBinder : Binder() {

        fun start(time: Long) {
            if (!checkServiceStarted()) {
                startService(time)
            }
        }

        fun restart(time: Long) {
            startService(time)
        }

        fun stop() {
            stopService()
        }

        fun addTime(time: Long) {
            changeTime(time)
        }
    }
}
