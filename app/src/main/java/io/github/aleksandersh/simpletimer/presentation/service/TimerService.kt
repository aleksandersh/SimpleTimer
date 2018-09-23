package io.github.aleksandersh.simpletimer.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.github.aleksandersh.simpletimer.R

class TimerService : Service() {

    companion object {

        private const val NOTIFICATION_ID = 1010
        private const val NOTIFICATION_CHANNEL_ID =
            "io.github.aleksandersh.simpletimer.TimerService.notification_channel"
    }

    private var timer: TimerImpl? = null
    private var started: Boolean = false

    private var listener: ((Int) -> Unit)? = null

    override fun onBind(intent: Intent): IBinder = TimerBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.stop()
    }

    private fun checkServiceStarted(): Boolean {
        return started
    }

    private fun makeForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.timer_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description =
                    getString(R.string.timer_notification_channel_description)
            val notificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.timer_notification_title))
            .setContentText(getString(R.string.timer_notification_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startService(time: Int) {
        started = true
        makeForeground()
        startTimer(time)
    }

    private fun startTimer(time: Int) {
        timer?.stop()
        val newTimer = TimerImpl()
        newTimer.setTickListener { newTime -> listener?.invoke(newTime) }
        newTimer.start(time)
        timer = newTimer
    }

    private fun stopService() {
        started = false
        stopTimer()
        stopForeground(true)
        stopSelf()
    }

    private fun stopTimer() {
        timer?.stop()
    }

    private fun changeTime(delta: Int) {
        timer?.add(delta)
    }

    inner class TimerBinder : Binder() {

        fun start(time: Int) {
            if (!checkServiceStarted()) {
                startService(time)
            }
        }

        fun restart(time: Int) {
            startService(time)
        }

        fun stop() {
            stopService()
        }

        fun addTime(time: Int) {
            changeTime(time)
        }

        fun setTimerListener(listener: ((Int) -> Unit)?) {
            this@TimerService.listener = listener
        }
    }
}
